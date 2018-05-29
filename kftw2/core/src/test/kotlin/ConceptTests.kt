
import com.badlogic.gdx.Preferences
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito
import world.Contexts
import world.Facts
import world.FactsOfTheWorld
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConceptTests {

	lateinit var factsOfTheWorld: FactsOfTheWorld
	companion object {
		@JvmStatic
		@BeforeClass
		fun beforeClass() {
		}
	}

	@Before
	fun before() {
		factsOfTheWorld = FactsOfTheWorld(Mockito.mock(Preferences::class.java))
		factsOfTheWorld.addToList(Facts.VisitedPlaces, "Yokohama")
		factsOfTheWorld.addToList(Facts.VisitedPlaces, "Berlin")
		factsOfTheWorld.addToList(Facts.VisitedPlaces, "London")
		factsOfTheWorld.addToList(Facts.VisitedPlaces, "Paris")
		factsOfTheWorld.stateBoolFact(Facts.FoundKey, true)
		factsOfTheWorld.stateIntFact(Facts.MetOrcs, 12)
		factsOfTheWorld.stateIntFact(Facts.NumberOfVisitedPlaces, 4)
		factsOfTheWorld.stateStringFact(Facts.Context, Contexts.MetNpc)
	}

	@After
	fun after() {
		factsOfTheWorld.clearAllFacts()
	}

	@Test
	fun addStringToList_ListContainsString() {
		//arrange
		//act
		assertEquals(4, factsOfTheWorld.getFactList(Facts.VisitedPlaces).value.count())
		factsOfTheWorld.addToList(Facts.VisitedPlaces, "Bratislava")

		//assert
		assertEquals(5, factsOfTheWorld.getFactList(Facts.VisitedPlaces).value.count())
		assertTrue(factsOfTheWorld.getFactList(Facts.VisitedPlaces).contains("Bratislava"))
	}

//  @Test
//  fun stringCriterion_MatchesExistingString() {
//    val fact = Fact.createFact(Facts.VisitedPlaces, "Berlin")
//
//		val criterion = Criterion.equalsCriterion(Facts.VisitedPlaces, "Berlin")
//
//		assertTrue { criterion.isMatch(fact) }
//  }
//
//	@Test
//	fun rulesThatPass_OnlyOneRulePasses() {
//		/*
//		We can have a global state of the game where
//		the player has visited three major sites,
//		traversed more than x tiles uniquely,
//		talked to n number of NPCs, killed r number
//		of ravagers, found four pieces of a map and
//		if all of this is true - a consequence emerges!
//
//		and what is a consequence?
//
//		Well, a goddamned lambda, that's what!
//
//		Tie-in to message system, conversation system, everything.
//
//		Sometimes I feel smart.
//
//		We could make a Fact-builder, sort of like a bootstrapper that ensures
//		a bunch of standard values that we just define. It would be great
//		to support some crazy import / export architecture...
//
//		But nah, not right now.
//
//		Anyways, this gives us, soonish, the possibility for "dull" conversations,
//		meaning that if the player has actually met
//		 */
//
//		/*
//		We need a method to get a set of facts.
//		 */
//
//		val passRule = Rule("UserFoundKey_VisitedBerlin_MetSomeOrcs", mutableListOf(
//				Criterion.booleanCriterion(Facts.FoundKey, true),
//				Criterion.containsCriterion(Facts.VisitedPlaces, "Berlin"),
//				Criterion.rangeCriterion(Facts.MetOrcs, 8..12)), ApplyFactsConsequence(emptyMap()))
//
//		val failRule = Rule("UserHasntFoundKey_BeenToBerlin_MetFewOrcs", mutableListOf(
//				Criterion.booleanCriterion(Facts.FoundKey, false),
//				Criterion.containsCriterion(Facts.VisitedPlaces, "Berlin"),
//				Criterion.rangeCriterion(Facts.MetOrcs, 3..6)),ApplyFactsConsequence(emptyMap()))
//
//		val result = FactsOfTheWorld.rulesThatPass(setOf(passRule, failRule))
//
//		assertEquals(1, result.count())
//		assertEquals("UserFoundKey_VisitedBerlin_MetSomeOrcs", result.first().name)
//	}
//
//	@Test
//	fun rulesThatPass_WithContext() {
//		val passRule = Rule("Pass", mutableListOf(
//				Criterion.booleanCriterion(Facts.FoundKey, true),
//				Criterion.containsCriterion(Facts.VisitedPlaces, "Berlin"),
//				Criterion.rangeCriterion(Facts.MetOrcs, 8..12),
//				Criterion.context(Contexts.MetNpc)), ApplyFactsConsequence(emptyMap()))
//
//		val failRule = Rule("Fail", mutableListOf(
//				Criterion.booleanCriterion(Facts.FoundKey, false),
//				Criterion.containsCriterion(Facts.VisitedPlaces, "Berlin"),
//				Criterion.rangeCriterion(Facts.MetOrcs, 3..6),
//				Criterion.context(Contexts.MetNpc)), ApplyFactsConsequence(emptyMap()))
//
//		val noContextRule = Rule("No_Context", mutableListOf(
//				Criterion.booleanCriterion(Facts.FoundKey, true),
//				Criterion.containsCriterion(Facts.VisitedPlaces, "Berlin"),
//				Criterion.rangeCriterion(Facts.MetOrcs, 8..12)), ApplyFactsConsequence(emptyMap()))
//
//		val result = FactsOfTheWorld.rulesThatPass(setOf(passRule, failRule, noContextRule))
//
//		assertEquals(2, result.count())
//		assertTrue(result.map { it.name }.containsAll(setOf("Pass", "No_Context")))
//		assertEquals("Pass", result.first().name)
//	}
//
//	@Test
//	fun ruleWithContextualCriteria_Passes() {
//		FactsOfTheWorld.addStringToList(Facts.NpcsPlayerHasMet, "SteveTheOrc")
//		FactsOfTheWorld.addStringToList(Facts.NpcsPlayerHasMet, "LeifTheBard")
//		FactsOfTheWorld.addStringToList(Facts.NpcsPlayerHasMet, "GunnarTheHunnar")
//		FactsOfTheWorld.stateStringFact(Facts.CurrentNpc, "SteveTheOrc")
//
//		val contextRule = Rule("Contextual", mutableListOf(
//				Criterion.listContainsFact<String>(Facts.NpcsPlayerHasMet, Facts.CurrentNpc)
//		), ApplyFactsConsequence(emptyMap()))
//	}
//
//
//	@Test
//	fun rules_With_Consequence() {
//		var consequenceHappened = ""
//		val passRule = Rule("Pass", mutableListOf(
//				Criterion.booleanCriterion(Facts.FoundKey, true),
//				Criterion.containsCriterion(Facts.VisitedPlaces, "Berlin"),
//				Criterion.rangeCriterion(Facts.MetOrcs, 8..12),
//				Criterion.context(Contexts.MetNpc)), ApplyLambdaConsequence{r, f ->  consequenceHappened = "${r.name }"})
//
//		val result = FactsOfTheWorld.rulesThatPass(setOf(passRule))
//    (result.first().consequence as ApplyConsequence).applyConsequence()
//
//		assertEquals("Pass", consequenceHappened)
//	}
}