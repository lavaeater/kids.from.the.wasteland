import com.lavaeater.kftw.data.IAgent
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import world.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IAgentFatExtensionMethodTests {
  @Test
  fun agentWithNoFactsHasNoFacts() {
    val agent : IAgent = mock(IAgent::class.java)
    assertFalse { agent.has(Fat.MetPlayer) }
  }

  @Test
  fun agentStatesFact_HasFact() {
    //arrange
    val agent = mock(IAgent::class.java)
    //act
    agent.stateFact(Fat.MetPlayer)
    //assert
    assertTrue { agent.has(Fat.MetPlayer) }
  }

  @Test
  fun agentHasFactButNoStringValue_EmptyStringReturned() {
    //arrange
    val agent = mock(IAgent::class.java)
    //act
    agent.stateFact(Fat.UsedConversations)
    //assert
    assertEquals("", agent.stringFor(Fat.UsedConversations))
  }

  @Test
  fun statingSingleStringOnFactReturnsOnlyOneString() {
    //arrange
    val agent = mock(IAgent::class.java)
    //act
    agent.stateFactWithSingle(Fat.Name, "My First name")
    //assert
    assertEquals("My First name", agent.stringFor(Fat.Name))

    //act
    agent.stateFactWithSingle(Fat.Name, "My Second name")
    //assert
    assertEquals("My Second name", agent.stringFor(Fat.Name))
    assertEquals(1, agent.stringsFor(Fat.Name).count())
  }

  @Test
  fun statingStringFacts_ReturnsCorrectNumberOfStrings() {
    //arrange
    val agent = mock(IAgent::class.java)
    //act
    agent.stateFactWithValue(Fat.UsedConversations, "A")
    agent.stateFactWithValue(Fat.UsedConversations, "B")
    agent.stateFactWithValue(Fat.UsedConversations, "C")
    agent.stateFactWithValue(Fat.UsedConversations, "A") // fats are unique so this one won't be added
    //assert
    assertTrue(agent.stringsFor(Fat.UsedConversations).containsAll(listOf("A", "B", "C")))
    assertEquals(3, agent.stringsFor(Fat.UsedConversations).count())
  }

	@Test
	fun addToIntFact_FactHasCorrectValue() {
		//arrange
		val agent = mock(IAgent::class.java)
		agent.stateFactWithValue(Fat.PlayerHate, 2)

		//act
		agent.addToIntFact(Fat.PlayerHate, 10)

		//assert
		assertEquals(12, agent.intFor(Fat.PlayerHate))
	}
}