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
		val story = stories.first { it.active } //just grab the first active story - null  check later

		/*
		Consequences MUST be self-contained, I realize this now
		they need to lazy-load all dependencies and just do their THANG
		 */
		val rule = factsOfTheWorld.rulesThatPass(story.rules.toSet()).firstOrNull()
		if(rule != null) {
			rule.consequence.apply()
			story.finishedRules.add(rule.name)
		}
		if(story.storyFinished) {
			//A STORY NEEDS A CONSEQUENCE! <- Mind blown!
			stories.remove(story)
			finishedStories.add(story)
			story.consequence.apply()
		}
	}

	fun addStory(story: Story) {
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
					inkStory("conversations/beamon_memory.ink.json") {} //This block can be used to set vars at time of creation, but we need something more powerful
					beforeConversation = {
						val antagonist = factsOfTheWorld.getCurrentNpc()
						if(antagonist != null) {
							it.variablesState["c_name"] = antagonist.name

							val potentialNames = mutableListOf(
									"John Linder",
									"Natalia Molina",
									"Sarah Molander",
									"Ellika Skohg",
									"Jenny Hall",
									"Kim Dinh Thi",
									"Kristina Andrén",
									"Andreas Gustafsson",
									"Jonas Personne",
									"Christine Andreasson",
									"Johan Thorgren",
									"Lina Fjeldgård",
									"Ulrica Wikren",
									"Emma Vikström",
									"Johan Odenfors",
									"Homan Peida",
									"Jessica Tennfors",
									"Maria Söderlund",
									"Madeleine Marmborg",
									"Ilhan Erdal",
									"Elisabeth Swahn",
									"Louise Thessman",
									"Jessika Nilsson",
									"Jennie Frejd",
									"Daniel Grufman",
									"Jan Pettersson",
									"Frida Kilany",
									"Daniel Madsen",
									"Marta Muñoz",
									"Kristoffer Skog",
									"Niklas Sjöström",
									"Malin Karlsson",
									"Nazila Norén",
									"Agata Roberts",
									"David Zevallos",
									"Petra Englund",
									"Maryam Engelbrecht",
									"Christoffer Nordvall",
									"Daniel Koenig",
									"Julia Garellick Lindborg",
									"Lotta Asplund",
									"Ruken Cetiner",
									"David Holmstrand",
									"Jon Wålstedt",
									"Thomas Larsson",
									"Christoffer Johansson",
									"Sonia Ling Persson",
									"Sofia Bråvander",
									"Ilona Jastrzebska",
									"Rosie Jonsson",
									"Isabell Sollman",
									"Niklas Peiper",
									"Adam Boman",
									"Kristin Stråhle",
									"Rebecca Hedberg",
									"Anita Sehat",
									"Elin Karasalo",
									"Mårten Hedlund",
									"Eddie Bjuvenius",
									"Karolina Willner",
									"Andreas Lindblad",
									"Claes Lundgren",
									"Mimmi Boman",
									"Kjell Bernhager",
									"Fredrik Anundsson",
									"Jörgen Eskelid",
									"Kalle Hansson",
									"Sabine Neumann",
									"Oskar Grunning",
									"Petter Knöös",
									"Mathias Lindberg",
									"Omid Safiyari",
									"Maria Herkner",
									"Johan Timander",
									"Jonas Oskö",
									"Fredrik Lindvall",
									"Jens Tinfors",
									"Peter Andersson",
									"Malin Forne",
									"Elias Nilsson",
									"Fredrik Meyer",
									"Daniel J:son Lindh",
									"Tommie Nygren",
									"Babak Varfan",
									"Sebastian Qvarfordt",
									"Sandra Damavandi",
									"Annika Uppenberg",
									"Louise Ajax",
									"Felix Lindström",
									"Anna Fjellström").filter { it != antagonist.name }.toMutableList()

							val correctIndex = MathUtils.random(0, 2)

							it.variablesState["name_guess_0"] = if (correctIndex == 0) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size - 1))
							it.variablesState["name_guess_1"] = if (correctIndex == 1) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size - 1))
							it.variablesState["name_guess_2"] = if (correctIndex == 2) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size - 1))
							//Query the global facts to see if we have met before:
							it.variablesState["met_before"] = factsOfTheWorld.getFactList(Facts.NpcsPlayerHasMet).contains(antagonist.id)
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