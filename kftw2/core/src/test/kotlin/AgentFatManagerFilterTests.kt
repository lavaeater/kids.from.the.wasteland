import data.IAgent
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito
import world.FatsManager
import world.Fat
import world.stateFactWithValue
import kotlin.test.assertEquals

class AgentFatManagerFilterTests {

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

			FatsManager.addAgent(agentNoFacts) //All agents are added in the system, we add this one specifically
		}
	}

	@Before
	fun before() {
		agentA.stateFactWithValue(Fat.PlayerHate, 12)
		agentB.stateFactWithValue(Fat.PlayerHate, 100)
		agentC.stateFactWithValue(Fat.PlayerHate, 1)
		agentD.stateFactWithValue(Fat.PlayerHate, 45)

		agentA.stateFactWithValue(Fat.MetPlayer, 1)
		agentB.stateFactWithValue(Fat.MetPlayer, 2)
		agentC.stateFactWithValue(Fat.MetPlayer, 3)
		agentD.stateFactWithValue(Fat.MetPlayer, 15)

		agentA.stateFactWithValue(Fat.UsedConversations, "intro")
		agentB.stateFactWithValue(Fat.UsedConversations, "meetagain")
		agentB.stateFactWithValue(Fat.UsedConversations, "intro")
		agentC.stateFactWithValue(Fat.UsedConversations, "intro")
		agentC.stateFactWithValue(Fat.UsedConversations, "meetagain")
		agentC.stateFactWithValue(Fat.UsedConversations, "quest")
		agentD.stateFactWithValue(Fat.UsedConversations, "intro")
		agentD.stateFactWithValue(Fat.UsedConversations, "meetagain")
		agentD.stateFactWithValue(Fat.UsedConversations, "leavemealone")
	}

	@Test
	fun filterOnHavingFact() {
		//act
		val agentsHavingMetPlayer = FatsManager.filterAgentsOnHavingFact(Fat.MetPlayer)

		//assert
		assertEquals(4, agentsHavingMetPlayer.count())
	}

	@Test
	fun filterOnNotHavingFact() {
		//act
		val agentsHavingMetPlayer = FatsManager.filterAgentsOnNotHavingFact(Fat.MetPlayer)

		//assert
		assertEquals(1, agentsHavingMetPlayer.count())
	}

	@Test
	fun filterOnPlayerHateInRange() {
		//act
		val hatingPlayerALot = FatsManager.filterAgentsOnIntValueInRange(Fat.PlayerHate, 40..100)

		//assert
		assertEquals(2, hatingPlayerALot.count())
	}

	@Test
	fun filterOnPlayerHateNotInRange() {
		//act
		val hatingPlayerLess = FatsManager.filterAgentsOnIntValueNotInRange(Fat.PlayerHate, 40..100)

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