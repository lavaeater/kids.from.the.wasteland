import kotlin.math.pow
import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals

/*
Use Peters excellent pattern for testing!

What do we need to test?

Stat calculating

Success calculations

Bonus calcs

Effect resolution

Change later so we can use the full power of ktest

 */

class StatsTests {

  open class GivenOneCombattant {
    val statValue = 6f
    val c1 = fastStat(statValue)
    @Test
    fun CombatStatsAreEqual() {

      val combatStats = sumCombatStats(c1)
      assertEquals(c1, combatStats, "When one combattant, stats should be equal")
    }
  }

  open class GivenTwoCombattants:GivenOneCombattant() {
    val c2 = fastStat(statValue)

    @Test
    fun CombatStatsShouldBeNine() {
      val combatStats = sumCombatStats(c1, c2)
      assertEquals(fastStat(9f), combatStats)
    }
  }

  class GivenThreeCombattants: GivenTwoCombattants() {
    val c3 = fastStat(statValue)
    @Test
    fun CombatStatsShouldBeEleven() {
      val combatStats = sumCombatStats(c1, c2, c3)
      assertEquals(fastStat(11f), combatStats)
    }
  }
}

class SkillResolutionTests {
  @Test
  fun randomTesting() {
    val iterations = 100
    for (i in 1..100) {

    }
  }
}

fun sumCombatStats(vararg stats: Stats, precision: Int = 1) : Stats {
  /*
  Formula... is...
   */
  var returnStat = Stats()

  for ((n, stat) in stats.withIndex()) {
    val factor = (1f / (n+ 1f))
    returnStat += stat.factor(factor)
  }
  return returnStat
}

/**
 * @param precision the number of decimals to round to. Default is one decimal
 * @return returns the value rounded to precision decimals
 */
fun Float.roundTo(precision: Int = 1) : Float {
  val f = 10f.pow(precision)
  return round(this * f) / f
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
  return this * fastStat(factor)
}

fun fastStat(value: Float):Stats = Stats(
    value,
    value,
    value,
    value,
    value,
    value
)

data class Stats(
    var attack: Float = 0f,
    var defense: Float = 0f,
    var damage: Float = 0f,
    var morale: Float = 0f,
    var constitution: Float = 0f,
    var health: Float = 0f)