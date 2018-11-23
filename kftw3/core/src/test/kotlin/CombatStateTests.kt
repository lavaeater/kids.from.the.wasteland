import kotlin.test.Test

/*
Combat IS a state machine, but that state machine OOONLY keeps
track of the combat loop, for simplicity
 */

enum class SkillOutcome {
  CriticalFailure, //Natural 1
  MajorFailure,
  Failure, //Not over skill
  Success, //Over skill
  MajorSuccess, //High bonus value
  UltraSuccess //Natural 20 or more, high bonus
}

class CombatAgent(
    val name: String,
    var discipline: Int = DisciplineLevels.levelOf(DisciplineLevels.Neutral))

object DisciplineLevels {

  const val Dominating = "Dominating" //Doing lots of damage and disciplined
  const val Disciplined = "Disciplined" //Team is doing well
  const val Neutral = "Neutral" //Neutral
  const val Pinned = "Pinned" //Limited options
  const val Overwhelmed = "Overwhelmed" //Not a good look
  const val Panic = "Panic"

  fun levelOf(level:String):Int {
    return Levels.indexOf(level)
  }

  val Levels = sortedSetOf(
      Panic,
      Overwhelmed,
      Pinned,
      Neutral,
      Disciplined,
      Dominating)
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
      -19..10 to SkillOutcome.MajorFailure,
      -200..-20 to SkillOutcome.CriticalFailure)

  val regularModifier = mapOf(
      SkillOutcome.UltraSuccess to 10,
      SkillOutcome.MajorSuccess to 5,
      SkillOutcome.Success to 2,
      SkillOutcome.Failure to -3,
      SkillOutcome.MajorFailure to -5,
      SkillOutcome.CriticalFailure to -10
  )

  val regularSuccessStates = mapOf(
      SkillOutcome.Success to DisciplineLevels.Neutral,
      SkillOutcome.MajorSuccess to DisciplineLevels.Disciplined,
      SkillOutcome.UltraSuccess to DisciplineLevels.Dominating
  )

  val regularFailStates = mapOf(
      SkillOutcome.Failure to DisciplineLevels.Pinned,
      SkillOutcome.CriticalFailure to DisciplineLevels.Overwhelmed)
}

object SkillDifficulty {
  val Easy = 10
  val Medium = 15
  val Hard = 20
  val Impossible = 30
}

/*
The discipline map should be like a map of modifiers to the discipline
roll and that gives you what... no, but I like the idea of the states and
stuff, it just became very weird with the shell thing. We might need more
states? Or rethink?

See Discipline as a morale-ladder. Different moves boost your own morale
and / or lowers the opponents morale. So being ambushed basically means that
you start at a serious morale disadvantage. We don't have nodes,
we have a level of morale for your team. Not states. And then we have to keep
track of what happens, well, if morale goes low enough, you route or something,
which is just a terrible death sentence. NPC's might just use the "give up"
move because it is the only one that they can reliably use.

So we have Levels of morale:
Dominating
Disciplined
Neutral
Pinned
Overwhelmed - same as ambushed then, your opponent succeeds in ambushing you, you start at
a moral disadvantage
Panic

This makes it easy to map going up and down in levels of morale!

To STAY IN THE FIGHT, the team has to succeed their discipline roll, that roll can, if failed,
result in defeat etc.

But what about moves that have special consequences? Like combat has to have outcomes?

CombatOutcomes comes in pairs, I guess? Like, a dominating party might try to negotiate with the
enemy, if the enemy can negotiate? Moves can have requirements like that. So they are pairs of
things:

Defeat - Negotiation / Neutral?
Victory - Negotiation / Neutral?

Victory

Victory - Disengaged (for the team that succeeds in a disengage move
Defeat - Disengaged (team that was "left behind")

Victory - Retreat
Defeat - Retreat

Victory is obviously defined as the team SUCCEEDING with something.

We should perhaps call that
POSITIVE
and non-wanted outcomes as NEGATIVE.

Meaning that for instance team A performs the move Pin on team B. Team B on the other hand
tries to Disengage. Pin *can* lead to Victory but in this case Team A simply succeed with their
skill roll

But when, when does one actually "win"?

Perhaps we can work with...

We need to pick ONE definition of victory that can work for *now*. That definition can
change later.

So - being victorious only means you got what you wanted. Losing means not being able to
control what happens in the combat at all, anymore.

So, failing the discipline roll at the end of the round *can* result in defeat. The nature of that
defeat is contingent on the action of the opponent. Say that they tried to Pin their opponents.
They actually failed this time but the opposing team is already in bad shape and gets a
serious disadvantage on their discipline roll. The get a failure and lose the fight.

 */

data class CombatMove(
    val name: String,
    val minDisciplineLevel: Int = DisciplineLevels.levelOf(DisciplineLevels.Neutral),
    val difficulty: Int = SkillDifficulty.Medium,
    val damageModifier: Map<SkillOutcome, Int> = SomeMaps.regularModifier,
    val defensiveModifier: Map<SkillOutcome, Int> = SomeMaps.regularModifier,
    val disciplineModifier: Map<SkillOutcome, Int> = SomeMaps.regularModifier)

object CombatMoves {
  val moves = setOf(
      CombatMove("Shell",
          DisciplineLevels.levelOf(DisciplineLevels.Pinned)),
      CombatMove("Fire at will"),
      CombatMove("Controlled fire"),
      CombatMove("Negotiate"),
      CombatMove("Regroup"),
      CombatMove("Flank"),
      CombatMove("Disengage"),
      CombatMove("Pin",
          DisciplineLevels.levelOf(DisciplineLevels.Disciplined))
  )
}

class CombatStateTests {
  @Test
  fun boilerPlateTest() {
  }

}