import com.lavaeater.kftw.data.IAgent
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import story.ConceptManager
import story.Criterion
import story.Fact
import story.Rule
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConceptTests {

	companion object {
		lateinit var agentA: IAgent
		lateinit var agentB: IAgent
		lateinit var agentC: IAgent
		lateinit var agentD: IAgent
		lateinit var agentNoFacts: IAgent

		val factKey = "VisitedCities"
		val subKey = "Something"

		@JvmStatic
		@BeforeClass
		fun beforeClass() {
			ConceptManager.addStringToList("VisitedPlaces", "Berlin")
			ConceptManager.addStringToList("VisitedPlaces", "Yokohama")
			ConceptManager.addStringToList("VisitedPlaces", "London")
			ConceptManager.addStringToList("VisitedPlaces", "Paris")
			ConceptManager.stateBoolFact("FoundKey", true)
			ConceptManager.stateIntFact("MetOrcs", 12)
			ConceptManager.stateIntFact("NumberOfVisitedPlaces", 4)
		}
	}

	@Before
	fun before() {

	}

	@Test
	fun addStringToList_ListContainsString() {
		//arrange
		//act
		ConceptManager.addStringToList(factKey, "Berlin")

		//assert
		assertEquals(1, ConceptManager.getFactList<String>(factKey).count())
		assertTrue(ConceptManager.getFactList<String>(factKey).contains("Berlin"))
	}

  @Test
  fun stringCriterion_MatchesExistingString() {
    val fact = Fact.createFact(factKey, "Berlin")

		val criterion = Criterion.equalsCriterion(factKey, "Berlin")

		assertTrue { criterion.isMatch(fact) }
  }

	@Test
	fun rulesThatPass_OnlyOneRulePasses() {
		/*
		We can have a global state of the game where
		the player has visited three major sites,
		traversed more than x tiles uniquely,
		talked to n number of NPCs, killed r number
		of ravagers, found four pieces of a map and
		if all of this is true - a consequence emerges!

		and what is a consequence?

		Well, a goddamned lambda, that's what!

		Tie-in to message system, conversation system, everything.

		Sometimes I feel smart.

		We could make a Fact-builder, sort of like a bootstrapper that ensures
		a bunch of standard values that we just define. It would be great
		to support some crazy import / export architecture...

		But nah, not right now.

		Anyways, this gives us, soonish, the possibility for "dull" conversations,
		meaning that if the player has actually met 
		 */

		/*
		We need a method to get a set of facts.
		 */

		val passRule = Rule("UserFoundKey_VisitedBerlin_MetSomeOrcs", mutableListOf(
				Criterion.booleanCriterion("FoundKey", true),
				Criterion.containsCriterion("VisitedPlaces", "Berlin"),
				Criterion.rangeCriterion("MetOrcs", 8..12)))
		val failRule = Rule("UserHasntFoundKey_BeenToBerlin_MetFewOrcs", mutableListOf(
				Criterion.booleanCriterion("FoundKey", false),
				Criterion.containsCriterion("VisitedPlaces", "Berlin"),
				Criterion.rangeCriterion("MetOrcs", 3..6)))

		val result = ConceptManager.rulesThatPass(setOf(passRule, failRule))

		assertEquals(1, result.count())
		assertEquals("UserFoundKey_VisitedBerlin_MetSomeOrcs", result.first().name)
	}

	@Test
	fun rulesThatPass_WithContext() {
		val passRule = Rule("Pass", mutableListOf(
				Criterion.booleanCriterion("FoundKey", true),
				Criterion.containsCriterion("VisitedPlaces", "Berlin"),
				Criterion.rangeCriterion("MetOrcs", 8..12),
				Criterion.context("MetNpc")))
		val failRule = Rule("Fail", mutableListOf(
				Criterion.booleanCriterion("FoundKey", false),
				Criterion.containsCriterion("VisitedPlaces", "Berlin"),
				Criterion.rangeCriterion("MetOrcs", 3..6),
				Criterion.context("MetNpc")))

		val noContextRule = Rule("No_Context", mutableListOf(
				Criterion.booleanCriterion("FoundKey", true),
				Criterion.containsCriterion("VisitedPlaces", "Berlin"),
				Criterion.rangeCriterion("MetOrcs", 8..12)))

		val result = ConceptManager.rulesThatPass(setOf(passRule, failRule, noContextRule), "MetNpc")

		assertEquals(2, result.count())
		assertTrue(result.map { it.name }.containsAll(setOf("Pass", "No_Context")))
		assertEquals("Pass", result.first().name)
	}

}