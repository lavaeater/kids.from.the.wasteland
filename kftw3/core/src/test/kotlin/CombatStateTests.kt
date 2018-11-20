import kotlin.test.Test
import kotlin.test.assertEquals
import statemachine.StateMachine

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

enum class CombatEvent {
  MajorFail, //Critical failure, basically, no good mate.
  Fail, //Normal failure
  Success, //Normal success
  MajorSuccess, //Rare
  UltraSuccess, //Very very rare
	Disengage
}

/**
 * There's just to many states here. We're
 * building a prototype, damnit!
 *
 * These basically reflect the morale of the team? Kinda?
 */
enum class CombatState {
	Defeated, //Negative
  Overwhelmed, //Negative - you end up here if ambushed
  Controlled, //Negative
  Neutral, //Neutral
  Disciplined, //Positive
  Victorious,  //Positive
	Disengaged //Neutral
}

class CombatStateMachine(private val globalStateAction: (CombatState)->Unit) {
	val stateMachine: StateMachine<CombatState, CombatEvent> = StateMachine.buildStateMachine(CombatState.Neutral, globalStateAction) {
		/*
	  When ambushed, a major succes is getting your team into a neutral
	  state. Failure means you are defeated. For now.
	   */
		state(CombatState.Overwhelmed) {
			edge(CombatEvent.UltraSuccess, CombatState.Disciplined) {}
			edge(CombatEvent.MajorSuccess, CombatState.Neutral) {}
			edge(CombatEvent.Success, CombatState.Controlled) {}
			edge(CombatEvent.Fail, CombatState.Overwhelmed) {}
			edge(CombatEvent.MajorFail, CombatState.Defeated) {}
		}
		/*
	  If you are controlled, this means you are pushed back,
	  movement is hard to do because of firing ways, the opposing
	  team has their sights on you. You might be in some kind of cover
	  but your initiative is very limited.
	   */
		state(CombatState.Controlled) {
			edge(CombatEvent.UltraSuccess, CombatState.Disciplined) {}
			edge(CombatEvent.MajorSuccess, CombatState.Neutral) {}
			edge(CombatEvent.Success, CombatState.Controlled) {}
			edge(CombatEvent.Fail, CombatState.Overwhelmed) {}
			edge(CombatEvent.MajorFail, CombatState.Defeated) {}
			edge(CombatEvent.Disengage, CombatState.Disengaged) {}
		}
		/*
	  The neutral state represents a team being able to do
	  pretty much anything and not having an advantage nor a
	  disadvantage.
	   */
		state(CombatState.Neutral) {
			edge(CombatEvent.UltraSuccess, CombatState.Victorious) {}
			edge(CombatEvent.MajorSuccess, CombatState.Disciplined) {}
			edge(CombatEvent.Success, CombatState.Neutral) {}
			edge(CombatEvent.Fail, CombatState.Neutral) {}
			edge(CombatEvent.MajorFail, CombatState.Controlled) {}
			edge(CombatEvent.Disengage, CombatState.Disengaged) {}
		}

		/*
	  Disciplined is a state where *more* options exists for the
	  team and success brings victory in a lot more cases.
	   */
		state(CombatState.Disciplined) {
			edge(CombatEvent.UltraSuccess, CombatState.Victorious) {}
			edge(CombatEvent.MajorSuccess, CombatState.Victorious) {}
			edge(CombatEvent.Success, CombatState.Disciplined) {}
			edge(CombatEvent.Fail, CombatState.Neutral) {}
			edge(CombatEvent.MajorFail, CombatState.Controlled) {}
			edge(CombatEvent.Disengage, CombatState.Disengaged) {} //Disengage in this context is an option at majorsuccess and up, I guess? It's a move that is a bit dynamic.
		}
	}
	init {
		stateMachine.initialize()
	}
}

/**
 * The combatmove class is a definition of a combat move
 * with its modifiers, requirements, bonuses and of course,
 * states in which it can be performed. Some combatmoves
 * can be performed in a special context, i.e. before a combat
 * one can perform an ambush, for instance.
 */
data class CombatMove(val name: String, val validStates: Set<CombatState> = setOf(
		CombatState.Disciplined,
		CombatState.Neutral,
		CombatState.Overwhelmed,
		CombatState.Controlled))

class CombatMoves {
	val combatMoves = mutableSetOf<CombatMove>()
	init {
		combatMoves.add(CombatMove("Shell"))//Shell is valid everywhere
		combatMoves.add(CombatMove("Control", setOf(CombatState.Disciplined))) //Control can only be done when disciplined.
		combatMoves.add(CombatMove("All out attack"))
		combatMoves.add(CombatMove("Rout"))
		combatMoves.add(CombatMove("Disengage",
				setOf(
						CombatState.Controlled,
						CombatState.Disciplined,
						CombatState.Neutral))) //Should be harder when controlled, perhaps?
		combatMoves.add(CombatMove("Bargain",
				setOf(CombatState.Controlled,
						CombatState.Neutral,
						CombatState.Disciplined)))
		combatMoves.add(CombatMove("Defensive Posture",
				setOf(CombatState.Controlled,
						CombatState.Neutral,
						CombatState.Disciplined)))
	}
}


class CombatStateTests {
	@Test
	fun boilerPlateTest() {
		val machine = CombatStateMachine {
			println(it)
		}


	}

}