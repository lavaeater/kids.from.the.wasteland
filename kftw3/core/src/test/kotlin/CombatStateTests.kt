import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import graph.Graph
import graph.Node
import kotlin.test.Test
import statemachine.StateMachine
import world.CompassDirection
import java.util.*

/*
The state of Combat, USA.

Combat is a state machine competition.

Every round both teams try to affect their own and the others
state. Everything you can do in combat affects your ability to
change your own and your opponents' state.

The Basic States we have are (we might add more later)
Overwhelmed - the team has been ambushed, this is a start state
Pushed Back - the team loses ground and advantage, morale is deteriorating quickly
Controlled - the team is controlled by the opponents, leaving them little room to act
Disciplined - the team is in control of their situation and can make good decisions
Dominating - the team controls / dominates the opponent, gains ground, gets advantages
Defeated - the team has lost - end state
Victorious - the team has won - end state
Disengaged - the team has managed to leave the encounter

Add states for bargaining.

Moving between states is done by making moves. Different moves
require different skills and stats and are not always available in all modes.

A controlled team might not be able to do a tactical retreat (escape routes are cut off) or it might
at least be very hard. A team can always make a complete break for it, but that is dangerous.

Every action in combat yields a result. This result dictates what state one ends up
in next. The result is sort of an "event", used by the state machine.

Possible Events?

UltraLoss
MajorLoss
Loss
Neutral
Win
MajorWin
UltraWin

Perhaps the moves and their definitions should be defining the state machine?
What if states and events were strings instead of enums?

Could we make a list of moves, states and events that could work with that?


 */

enum class SkillOutcome {
	CriticalFailure, //Natural 1
	Failure, //Not over skill
	Success, //Over skill
	MajorSuccess, //High bonus value
	UltraSuccess //Natural 20 or more, high bonus
}

/**
 * The combatmove class is a definition of a combat move
 * with its modifiers, requirements, bonuses and of course,
 * states in which it can be performed. Some combatmoves
 * can be performed in a special context, i.e. before a combat
 * one can perform an ambush, for instance.
 */

/*
Jesus. Is the combat shit a fucking graph?

Is it a fucking graph I ask you?

If it were, the combatagent could have a current state (a node)
with a list of possible moves (its relations). If successful,
every move has a list of outcomes, that are in turn fucking nodes.
 */

object GraphAss {
  val allNodes = mutableMapOf<String, Node<CombatNode, CombatRelation>>()
  val g = Graph<CombatNode, CombatRelation>(emptyMap())
  init {
    for (state in CombatStates.AllStates) {
      allNodes[state] = g.addNode(Node(CombatNode.CombatState(state)))
    }
  }
}

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
sealed class CombatNode {
  var id = UUID.randomUUID()
  data class CombatMove(val name: String) : CombatNode()
  data class CombatState(val name: String) : CombatNode()
}

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
sealed class CombatRelation {
  var id = UUID.randomUUID()
  data class CombatMoveRelation(val name:String) : CombatRelation()
  data class StateRelation(val outcome: SkillOutcome) : CombatRelation()
}

class CombatAgent(val name:String, var state: String)

/**
 * The event disengaged can be conjured up from some
 * other state and it would obviously put that team
 * in the state Disengaged. So we map outcomes -> events
 * and these events map to states, of course.
 *
 * So the state machine is built by saying that all states can be
 * gotten to from all other states by a certain event (the state)
 *
 * For that... we don't need a state machine.
 *
 * Or actually, the mapping is now outcomes -> states
 * But we need a probability mapping for a move -> what states
 * it can lead to.
 *
 * I think, that for now, we could skip the state machine altother and just
 * consider this an excercise in mapping moves / skills -> outcomes -> states.
 *
 * Like if Team A performs some move, it can effect their opponents state AND
 * their own
 *
 */
object CombatStates {
	const val Ambushed = "Ambushed"
	const val Overwhelmed = "Overwhelmed" //Not a good look
	const val Disengaged = "Disengaged" //Combat ends
	const val Disciplined = "Disciplined" //Team is doing well
	const val Defeated = "Defeated" //Combat ends
	const val Controlled = "Controlled" //Limited options
	const val Neutral = "Neutral" //Neutral
	const val Victorious = "Victorious"  //combat ends

	val AllStates = setOf(
			Ambushed,
			Overwhelmed,
			Disengaged,
			Disciplined,
			Defeated,
			Controlled,
			Neutral,
			Victorious)
}

class CombatMoves {
	val combatMoves = mutableSetOf<CombatMove>()
}


class CombatStateTests {
	@Test
	fun boilerPlateTest() {
		val machine = CombatStateMachine {
			println(it)
		}


	}

}