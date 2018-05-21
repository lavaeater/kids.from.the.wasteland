import com.lavaeater.kftw.data.IAgent
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import story.ConceptManager
import story.Criterion
import story.Fact
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

		}
	}

	@Before
	fun before() {

	}

	@Test
	fun exploreAndThenNightyNighty() {
		//arrange
		//act
		ConceptManager.addStringToList(factKey, "Berlin")

		//assert
		assertEquals(1, ConceptManager.getFactList<String>(factKey).count())
	}

  @Test
  fun testACriterion() {
    val fact = Fact.createFact(factKey, "Berlin")

		val criterion = Criterion.equalsCriterion(factKey, "Berlin")

		assertTrue { criterion.isMatch(fact) }
  }

	@Test
	fun testComplicated() {
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
	}

}