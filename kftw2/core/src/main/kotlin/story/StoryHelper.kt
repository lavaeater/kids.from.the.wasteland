package story

import com.badlogic.gdx.math.MathUtils
import data.Player
import factory.ActorFactory
import injection.Ctx
import map.IMapManager
import story.fact.Contexts
import story.fact.Facts
import ui.IUserInterface

class StoryHelper {
	val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>()}

	fun createStoryWithStartingFactsEtc() : Story {
		var npcId = ""
		var story = story {
			name = "Find a certain guy"
			initializer = {
				/*
				Inject a factory to create a specific npc at some location in the world.


				 */
				val actorFactory = Ctx.context.inject<ActorFactory>()

				val mapManager = Ctx.context.inject<IMapManager>()

				val someTilesInRange = mapManager.getBandOfTiles(0,0, 100, 3).filter {
					it.tile.tileType != "rock" && it.tile.tileType != "water"
				}

				val randomlySelectedTile = someTilesInRange[MathUtils.random(0, someTilesInRange.count() - 1)]

				//Type set to townsfolk to make the behavior tree random, basically
				val npcToFind = actorFactory.addNpcAtTileWithAnimation("Flexbert", "townsfolk", "stephenhawking",randomlySelectedTile.x, randomlySelectedTile.y)
				npcId = npcToFind.second.id
			}
			rule {
				name = "First time meeting Flexbert"
				context(Contexts.MetNpc)
				equalsCriterion(Facts.CurrentNpc, npcId)
				notContainsCriterion(Facts.NpcsPlayerHasMet, npcId)
				conversation {
					inkStory("conversations/flexbert.ink.json") {
						/*
						Thougts: in this case we can
						certainly imagine keeping this particular story around. Maybe we will set some
						flag for the story using a different rule, opening up more options, but
						the story will remember "itself" so we can use ONE story...
						 */
					}
					beforeConversation = {
						it.variablesState["met_before"] = false
						it.variablesState["player_name"] = Ctx.context.inject<Player>().name
					}
					afterConversation = {
						/*
						save story state in prefs?
						we need a general "update basic facts about the world-method for all things
						that will be getting
						 */
						factsOfTheWorld.addToList(Facts.NpcsPlayerHasMet, npcId)
						factsOfTheWorld.addToList(Facts.KnownNames, "Flexbert")
					}
				}
			}

		}

		return story
	}

	fun createMainStory(): Story {
		return (story {
			name = "MeetAllTheEmployees"
			consequence {
				apply = {
					if (factsOfTheWorld.getBooleanFact(Facts.GameWon).value)
						Ctx.context.inject<IUserInterface>().showSplashScreen()
				}
			}
			rule {
				name = "WhenMeetingNpcStartConversation"
				context("MetNpc")
				rangeCriterion(Facts.Score, 0..3)
				conversation {
					inkStory("conversations/basic_dialog.ink.json") {} //This block can be used to set vars at time of creation, but we need something more powerful
					beforeConversation = {
						val antagonist = factsOfTheWorld.getCurrentNpc()
						if (antagonist != null) {
							it.variablesState["c_name"] = antagonist.name

							val potentialNames = mutableListOf(
									"Carl Sagan",
									"Stephen Hawking",
									"Erwin Hubble",
									"Nikolas Kopernikus",
									"Julius Caeasar",
									"Marcus Antonious",
									"Sun Tzu",
									"Mark Wahlberg",
									"Galileo Galilei",
									"Carolyn Shoemaker",
									"Sandra Faber"
							).filter { it != antagonist.name }.toMutableList()

							val correctIndex = MathUtils.random(0, 2)

							it.variablesState["name_guess_0"] = if (correctIndex == 0) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size - 1))
							it.variablesState["name_guess_1"] = if (correctIndex == 1) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size - 1))
							it.variablesState["name_guess_2"] = if (correctIndex == 2) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size - 1))
							//Query the global facts to see if we have met before:
							it.variablesState["met_before"] = factsOfTheWorld.getFactList(Facts.NpcsPlayerHasMet).contains(antagonist.id)
							it.variablesState["first_encounter"] = factsOfTheWorld.getIntValue(Facts.MetNumberOfNpcs) == 0
						}
					}
					afterConversation = {
						val npc = factsOfTheWorld.getCurrentNpc()
						if (npc != null) {
							factsOfTheWorld.addToList(Facts.NpcsPlayerHasMet, npc.id)
							//Add to counter of this particular type
							factsOfTheWorld.addToIntFact(Facts.MetNumberOfNpcs, 1)

							if (!factsOfTheWorld.getFactList(Facts.KnownNames).contains(npc.name)
									&& it.variablesState["guessed_right"] as Int == 1) {
								factsOfTheWorld.addToIntFact(Facts.Score, 1)
								factsOfTheWorld.addToList(Facts.KnownNames, npc.name)
							}
						}
					}
				}
			}
			rule {
				name = "CheckIfScoreIsFour"
				equalsCriterion(Facts.Score, 4)
				consequence {
					apply = {
						factsOfTheWorld.stateBoolFact(Facts.GameWon, true)
						val prop by lazy { Ctx.context.inject<IUserInterface>() }
						prop.showSplashScreen()
					}
				}
			}
		})
	}
}