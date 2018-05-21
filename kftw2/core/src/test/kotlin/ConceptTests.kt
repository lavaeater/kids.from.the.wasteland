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

}