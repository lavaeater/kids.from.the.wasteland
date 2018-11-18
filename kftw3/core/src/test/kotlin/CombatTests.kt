import kotlin.test.Test

/*
Use Peters excellent pattern for testing!

What do we need to test?

Stat calculating

Success calculations

Bonus calcs

Effect resolution



 */

class StatCalculatorTests {

  @Test
  fun given_one_combattant_sum_is_same() {
    val stat = Stats(5,5,5,5,5,5)

    val combatStats =
  }
}

fun sumOfStats(vararg stats: Stats) : Stats {
  /*
  Formula... is...
   */
  val returnStat = Stats()

  for ((n, stat) in stats.withIndex()) {
    val factor = 1 / (n+ 1)

  }
}

operator fun Stats.plus(b:Stats):Stats{
  return Stats(attack + b.attack,
      defense + b.defense,
      damage + b.damage,
      morale + b.morale,
      constitution + b.constitution,
      health + b.health)
}

operator fun Stats.minus(b:Stats) : Stats {
  return Stats(attack - b.attack,
      defense - b.defense,
      damage - b.damage,
      morale - b.morale,
      constitution - b.constitution,
      health - b.health)
}

operator fun Stats.times(b: Stats) : Stats {
  return Stats(
      attack * b.attack,
      defense * b.defense,
      damage * b.damage,
      morale * b.morale,
      constitution * b.constitution,
      health * b.health)
}
operator fun Stats.div(b: Stats) : Stats {
  return Stats(
      attack / b.attack,
      defense / b.defense,
      damage / b.damage,
      morale / b.morale,
      constitution / b.constitution,
      health / b.health)
}

fun Stats.factor(factor: Float): Stats {
  return this * statFactor(factor)
}

fun statFactor(factor: Float):Stats = Stats(
    factor,
    factor,
    factor,
    factor,
    factor,
    factor
)

data class Stats(
    var attack: Float = 0f,
    var defense: Float = 0f,
    var damage: Float = 0f,
    var morale: Float = 0f,
    var constitution: Float = 0f,
    var health: Float = 0f)