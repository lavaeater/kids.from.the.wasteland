import com.lavaeater.kftw.data.IAgent
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import story.*
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

  @Test
  fun agentHasFactButNoStringValue_EmptyStringReturned() {
    //arrange
    val agent = mock(IAgent::class.java)
    //act
    agent.stateFact(Fact.UsedConversations)
    //assert
    assertEquals("", agent.stringFor(Fact.UsedConversations))
  }

  @Test
  fun statingSingleStringOnFactReturnsOnlyOneString() {
    //arrange
    val agent = mock(IAgent::class.java)
    //act
    agent.stateFactWithSingle(Fact.Name, "My First name")
    //assert
    assertEquals("My First name", agent.stringFor(Fact.Name))

    //act
    agent.stateFactWithSingle(Fact.Name, "My Second name")
    //assert
    assertEquals("My Second name", agent.stringFor(Fact.Name))
    assertEquals(1, agent.stringsFor(Fact.Name).count())
  }


}