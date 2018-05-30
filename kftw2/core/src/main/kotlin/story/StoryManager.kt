package story

import injection.Ctx
import story.fact.Facts

class StoryManager {
	private val stories  = mutableListOf<Story>()
	private val finishedStories = mutableListOf<Story>()
	//val rulesOfTheWorld by lazy { Ctx.context.inject<RulesOfTheWorld>() }
	val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>() }

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
			//A STORY NEEDS A CONSEQUENCE!
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
				applier = { r, f ->
					//Just stop right here
					val s = "s"
				}
			}
			rule {
				name = "WhenMeetingNpcStartConversation"
				context("MetNpc")
				rangeCriterion(Facts.Score, 0..3)
				conversation {
					storyPath = "conversations/beamon_memory.ink.json"
				}
			}
			rule {
				name = "CheckIfScoreIsFour"
				equalsCriterion(Facts.Score, 4)
				applyLambdaConsequence {
					applier = { r, f -> }

				}
			}
		})
	}
}