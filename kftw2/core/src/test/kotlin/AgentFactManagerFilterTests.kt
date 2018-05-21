import com.lavaeater.kftw.data.IAgent
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import story.AgentFactsManager
import story.Fact
import story.stateFactWithValue
import kotlin.test.assertEquals

class AgentFactManagerFilterTests {

	private lateinit var agentA: IAgent
	private lateinit var agentB: IAgent
	private lateinit var agentC: IAgent
	private lateinit var agentD: IAgent
	private lateinit var agentNoFacts: IAgent

	@Before
	fun explorativeFilterTest() {
		agentA = Mockito.mock(IAgent::class.java)
		agentB = Mockito.mock(IAgent::class.java)
		agentC = Mockito.mock(IAgent::class.java)
		agentD = Mockito.mock(IAgent::class.java)
		agentNoFacts = Mockito.mock(IAgent::class.java)
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
}