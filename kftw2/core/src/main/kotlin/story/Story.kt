package story

import injection.Ctx
import story.fact.Facts
import story.rule.Rule


/**
 * A "story" in the game.
 *
 * A story, conceptually, is a collection of steps.
 * A step is simply a rule that must be fulfilled.
 *
 * A rule is, as we know, a collection of criteria
 *
 * It would be nice with a criteria->rule->story
 * DSL and also a human-readable syntax for writing
 * them.
 *
 * Anyways, the facts of a criteria and or rule will
 * be set somewhere else. So, a story cannot base its
 * rules on stuff that isn't set "somewhere else"
 *
 * A lot of the things that could be a basis for a story
 * isn't implemented yet, such as "Player has visited place
 * A" or "Player has found 5 magical crystals"
 *
 * Actually, finding stuff must be stored in the facts
 * of the world. At least "unique" stuff. So if an item has
 * a "special" flag, it's string based key (or integer,
 * but then we need integer lists oh my goad) must be stored
 * in some list of items that is used to keep track of it.
 *
 * We must also have some way of triggering everything
 * that happens in the game.
 *
 * So, the player moves about - triggers updates about
 * areas and such.
 *
 * Bah, we refactor it as we need.
 *
 * Anyway, we need a hook or message that is sent to some
 * class that handles messages
 *
 */
data class Story(val name:String, val rules: List<Rule>)

class StoryManager {
	private val stories = mutableSetOf<Story>()
	val rulesOfTheWorld by lazy { Ctx.context.inject<RulesOfTheWorld>() }

	fun addStory(story:Story) {
		stories.add(story)
	}

	init {
		addStory(story {
			name = "MetAllTheEmployees"
			rule {
				name = "WhenMeetingNpcStartConversation"
				context("MetNpc")
				conversation {
					storyPath = "conversations/beamon_memory.ink.json"
				}
			}
			rule {
				name = "CheckIfScoreIsFour"
				equalsCriterion(Facts.Score, 4)
				applyLambdaConsequence {
					applier = {r, f -> }

				}
			}
		})
	}
}
