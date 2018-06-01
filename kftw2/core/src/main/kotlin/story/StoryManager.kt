package story

import com.badlogic.gdx.math.MathUtils
import injection.Ctx
import story.fact.Facts
import ui.IUserInterface

class StoryManager {
	private val stories  = mutableListOf<Story>()
	private val finishedStories = mutableListOf<Story>()
	//val rulesOfTheWorld by lazy { Ctx.context.inject<RulesOfTheWorld>() }
	private val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>() }

	fun checkStories() {
		val matchingStories = stories.filter { it.active && factsOfTheWorld.storyMatches(it) }.sortedByDescending { it.matchingRule?.criteriaCount } //just grab the first active story - null  check later

		/*
		Consequences MUST be self-contained, I realize this now
		they need to lazy-load all dependencies and just do their THANG

		Thing is, the code below will not wait for the consequences of stories before it continues...
		how do we implement this?

		A call back? Some kind of async mechanism?

		I think some kind of simple callback mechanism to do one story at a time,
		somehow. So to begin with we do "more complicated story first" if there are more than one!
		 */
		var story = matchingStories.firstOrNull()
		if(story != null) {

			val rule = factsOfTheWorld.rulesThatPass(story.rules.toSet()).firstOrNull()
			if (rule != null) {
				rule.consequence.apply()
				story.finishedRules.add(rule.name)
			}
			if (story.storyFinished) {
				//A story sets its own finished state when it's done
				//A STORY NEEDS A CONSEQUENCE! <- Mind blown!
				stories.remove(story)
				finishedStories.add(story)
				story.consequence.apply()
			}
		}
	}

	fun addStory(story: Story) {
		story.activate()
		stories.add(story)
	}

	init {
		addStory(story {
			name = "MeetAllTheEmployees"
			consequence {
				apply = {
					if(factsOfTheWorld.getBooleanFact(Facts.GameWon).value)
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
						if(antagonist != null) {
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
					apply  = {
						factsOfTheWorld.stateBoolFact(Facts.GameWon, true)
						val prop by lazy { Ctx.context.inject<IUserInterface>() }
						prop.showSplashScreen()
					}
				}
			}
		})
	}
}