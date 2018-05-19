import com.lavaeater.kftw.data.IAgent
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import story.Fact
import story.has
import story.stateFact
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AgentFactsManagerTests {
  @Test
  fun agentWithNoFactsHasNoFacts() {
    val agent : IAgent = mock(IAgent::class.java)
    assertFalse { agent.has(Fact.MetPlayer) }
  }

  @Test
  fun agentStatesFact_HasFact() {
    //arrange
    val agent = mock(IAgent::class.java)
    //act
    agent.stateFact(Fact.MetPlayer)
    //assert
    assertTrue { agent.has(Fact.MetPlayer) }
  }
}