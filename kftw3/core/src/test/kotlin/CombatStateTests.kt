import kotlin.test.Test

/*
The state of Combat, USA.

Combat is a state machine competition.

Every round both teams try to affect their own and the others
state. Everything you can do in combat affects your ability to
change your own and your opponents' state.

The Basic States we have are (we might add more later)
Overwhelmed - the team has been ambushed, this is a start state
Pushed Back - the team loses ground and advantage, morale is deteriorating quickly
Pinned - the team is controlled by the opponents, leaving them little room to act
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
//
//object GraphAss {
//  val allNodes = mutableMapOf<String, Node<String, CombatRelation>>()
//  val g = Graph<String, CombatRelation>(emptyMap())
//  init {
//    for (state in CombatStates.AllStates) {
//      allNodes[state] = g.addNode(CombatNode.CombatState(state))
//    }
//  }
//}
//
//sealed class CombatNode(open val name: String)  : Node<String, CombatRelation>(name) {
//  class CombatMove(override val name:String) : CombatNode(name)
//  class CombatState(override val name:String) : CombatNode(name)
//}
//
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
//sealed class CombatRelation {
//  var id = UUID.randomUUID()
//  data class CombatMoveRelation(val name:String) : CombatRelation()
//  data class StateRelation(val outcome: SkillOutcome) : CombatRelation()
//}

class CombatAgent(val name: String, var state: String)

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
  const val Dominating = "Dominating" //Doing lots of damage and disciplined
  const val Defeated = "Defeated" //Combat ends
  const val Pinned = "Pinned" //Limited options
  const val Neutral = "Neutral" //Neutral
  const val Victorious = "Victorious"  //combat ends

  val AllStates = setOf(
      Ambushed,
      Overwhelmed,
      Disengaged,
      Disciplined,
      Defeated,
      Pinned,
      Neutral,
      Victorious)
}

/*
Combat moves are performed with Discipline + Offensive Roll towards the difficulty

If successful, they infer benefits / penalties on
Damage Roll
Defensive Roll
Discipline Roll


 */

object SomeMaps {
  val outcomes = mapOf(
      15..200 to SkillOutcome.UltraSuccess,
      10..14 to SkillOutcome.MajorSuccess,
      1..9 to SkillOutcome.Success,
      -9..0 to SkillOutcome.Failure,
      -200..-10 to SkillOutcome.CriticalFailure)
  val regularModifier = mapOf(
      SkillOutcome.UltraSuccess to 10,
      SkillOutcome.MajorSuccess to 5,
      SkillOutcome.Success to 2,
      SkillOutcome.Failure to -5,
      SkillOutcome.CriticalFailure to -10
  )

  val regularSuccessStates = mapOf(
      SkillOutcome.Success to CombatStates.Neutral,
      SkillOutcome.MajorSuccess to CombatStates.Disciplined,
      SkillOutcome.UltraSuccess to CombatStates.Dominating
  )

  val regularFailStates = mapOf(
      SkillOutcome.Failure to CombatStates.Pinned,
      SkillOutcome.CriticalFailure to CombatStates.Overwhelmed)
}

object SkillDifficulty {
  val Easy = 5
  val Medium = 10
  val Hard = 15
  val Impossible = 20
}

data class CombatMove(
    val name: String,
    val validStates: Set<String> = CombatStates.AllStates,
    val successStates: Map<SkillOutcome, String> = SomeMaps.regularSuccessStates,
    val failStates: Map<SkillOutcome, String> = SomeMaps.regularFailStates,
    val difficulty: Int = SkillDifficulty.Medium,
    val damageModifier: Map<SkillOutcome, Int>,
    val defensiveModifier: Map<SkillOutcome, Int>,
    val disciplineModifier: Map<SkillOutcome, Int>)

object CombatMoves {
  val moves = setOf(
      CombatMove("Pin",
          setOf(CombatStates.Disciplined),
          mapOf(
              SkillOutcome.Success to CombatStates.Disciplined,
              SkillOutcome.MajorSuccess to CombatStates.Dominating,
              SkillOutcome.UltraSuccess to CombatStates.Victorious),
          mapOf(
              SkillOutcome.Failure to CombatStates.Pinned,
              SkillOutcome.CriticalFailure to CombatStates.Overwhelmed),
          18,
          SomeMaps.regularModifier,
          SomeMaps.regularModifier,
          SomeMaps.regularModifier
      )
  )
}

/*
This is a relevant question. Now, we are thinking about the combat as a graph.
Every team in the combat has a current node, a combat state. This node dictates
potential available moves, which we are trying to construct a new map of,
above.

But who decides what happens to you in this map? Both teams want to affect both
teams combatstate. One can see it as a board game where using certain nodes
one can hop to, get to, end of game-nodes. Yes, that's it. We are NOT using a
state machine, remember this. We use States -> Moves -> SuccessOutcomes -> States
 */


class CombatStateTests {
  @Test
  fun boilerPlateTest() {
  }

}