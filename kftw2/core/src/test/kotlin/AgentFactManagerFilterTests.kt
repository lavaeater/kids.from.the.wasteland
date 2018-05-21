import com.lavaeater.kftw.data.IAgent
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito
import story.AgentFactsManager
import story.Fact
import story.stateFactWithValue
import kotlin.test.assertEquals

class AgentFactManagerFilterTests {

	companion object {
		lateinit var agentA: IAgent
		lateinit var agentB: IAgent
		lateinit var agentC: IAgent
		lateinit var agentD: IAgent
		lateinit var agentNoFacts: IAgent

		@JvmStatic
		@BeforeClass
		fun beforeClass() {

			/*
			We only mock the agents one, but the statements below should be idempotent...
			 */
			agentA = Mockito.mock(IAgent::class.java)
			agentB = Mockito.mock(IAgent::class.java)
			agentC = Mockito.mock(IAgent::class.java)
			agentD = Mockito.mock(IAgent::class.java)
			agentNoFacts = Mockito.mock(IAgent::class.java)

			AgentFactsManager.addAgent(agentNoFacts) //All agents are added in the system, we add this one specifically
		}
	}

	@Before
	fun before() {
		agentA.stateFactWithValue(Fact.PlayerHate, 12)
		agentB.stateFactWithValue(Fact.PlayerHate, 100)
		agentC.stateFactWithValue(Fact.PlayerHate, 1)
		agentD.stateFactWithValue(Fact.PlayerHate, 45)

		agentA.stateFactWithValue(Fact.MetPlayer, 1)
		agentB.stateFactWithValue(Fact.MetPlayer, 2)
		agentC.stateFactWithValue(Fact.MetPlayer, 3)
		agentD.stateFactWithValue(Fact.MetPlayer, 15)

		agentA.stateFactWithValue(Fact.UsedConversations, "intro")
		agentB.stateFactWithValue(Fact.UsedConversations, "meetagain")
		agentB.stateFactWithValue(Fact.UsedConversations, "intro")
		agentC.stateFactWithValue(Fact.UsedConversations, "intro")
		agentC.stateFactWithValue(Fact.UsedConversations, "meetagain")
		agentC.stateFactWithValue(Fact.UsedConversations, "quest")
		agentD.stateFactWithValue(Fact.UsedConversations, "intro")
		agentD.stateFactWithValue(Fact.UsedConversations, "meetagain")
		agentD.stateFactWithValue(Fact.UsedConversations, "leavemealone")
	}

	@Test
	fun filterOnHavingFact() {
		//act
		val agentsHavingMetPlayer = AgentFactsManager.filterAgentsOnHavingFact(Fact.MetPlayer)

		//assert
		assertEquals(4, agentsHavingMetPlayer.count())
	}

	@Test
	fun filterOnNotHavingFact() {
		//act
		val agentsHavingMetPlayer = AgentFactsManager.filterAgentsOnNotHavingFact(Fact.MetPlayer)

		//assert
		assertEquals(1, agentsHavingMetPlayer.count())
	}

	@Test
	fun filterOnPlayerHateInRange() {
		//act
		val hatingPlayerALot = AgentFactsManager.filterAgentsOnIntValueInRange(Fact.PlayerHate, 40..100)

		//assert
		assertEquals(2, hatingPlayerALot.count())
	}

	@Test
	fun filterOnPlayerHateNotInRange() {
		//act
		val hatingPlayerLess = AgentFactsManager.filterAgentsOnIntValueNotInRange(Fact.PlayerHate, 40..100)

		//assert
		assertEquals(3, hatingPlayerLess.count())
	}

	@Test
	fun fuzzyTester() {
		/*
		We need to set a bunch of ranges and stuff and check if an agent
		satisfies the matcher

		AND THIS IS WHERE WE DO A DSL!
		 */


	}

}