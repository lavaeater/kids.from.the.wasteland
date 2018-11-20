import statemachine.StateMachine

/*
The state of Combat, USA.

Combat is a state machine competition.

Every round both teams try to affect their own and the others
state. Everything you can do in combat affects your ability to
change your own and your opponents' state.

The Basic States we have are (we might add more later)
Ambushed - the team has been ambushed, this is a start state
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

 */

enum class CombatEvent {
  MajorFail,
  Fail,
  Success,
  MajorSuccess,
}

/**
 * There's just to many states here. We're
 * building a prototype, damnit!
 *
 * These basically reflect the morale of the team? Kinda?
 */
enum class CombatState {
	Defeated, //Negative
  Ambushed, //Negative
  Controlled, //Negative
  Neutral, //Neutral
  Disciplined, //Positive
  Victorious,  //Positive
  Disengaged //Neutral
}

class CombatStateMachine(private val globalStateAction: (CombatState)->Unit) {
  val stateMachine: StateMachine<CombatState, CombatEvent>
      = StateMachine.buildStateMachine(CombatState.Neutral, globalStateAction) {
	  /*
	  When ambushed, a major succes is getting your team into a neutral
	  state. Failure means you are defeated. For now.
	   */
	  state(CombatState.Ambushed) {
      edge(CombatEvent.MajorSuccess, CombatState.Controlled) {}
      edge(CombatEvent.Success, CombatState.Ambushed) {}
      edge(CombatEvent.Fail, CombatState.Defeated) {}
      edge(CombatEvent.MajorFail, CombatState.Defeated) {}}
	  /*
	  If you are controlled, this means you are pushed back,
	  movement is hard to do because of firing ways, the opposing
	  team has their sights on you. You might be in some kind of cover
	  but your initiative is very limited.
	   */
	  state(CombatState.Controlled) {
		  edge(CombatEvent.MajorSuccess, CombatState.Neutral) {}
		  edge(CombatEvent.Success, CombatState.Controlled) {}
		  edge(CombatEvent.Fail, CombatState.Ambushed) {}
		  edge(CombatEvent.MajorFail, CombatState.Defeated) {}}
	  /*
	  The neutral state represents a team being able to do
	  pretty much anything and not having an advantage nor a
	  disadvantage.
	   */
	  state(CombatState.Neutral) {
		  edge(CombatEvent.MajorSuccess, CombatState.Disciplined) {}
		  edge(CombatEvent.Success, CombatState.Disciplined) {}
		  edge(CombatEvent.Fail, CombatState.Neutral) {}
		  edge(CombatEvent.MajorFail, CombatState.Controlled) {}}
}

class CombatStateTests {

}