package world

import com.badlogic.gdx.math.MathUtils
import com.bladecoder.ink.runtime.RTObject
import com.bladecoder.ink.runtime.Story
import com.bladecoder.ink.runtime.VariableAssignment
import com.lavaeater.kftw.data.IAgent

class InkConversation(val story:Story, override val protagonist:IAgent, override val antagonist:IAgent) : IConversation {
  init {
    story.variablesState["c_name"] = antagonist.name

    val potentialNames = mutableListOf("Tommie Nygren",
        "Frank Artschwager",
        "Fredrik Lindvall",
        "Petter Knöös",
        "Fredrik Anundson",
        "Karl Johan Andreasson").filter { it != antagonist.name }.toMutableList()

    val correctIndex = MathUtils.random(0,2)

    story.variablesState["name_guess_0"] = if(correctIndex == 0) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size -1))
    story.variablesState["name_guess_1"] = if(correctIndex == 1) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size -1))
    story.variablesState["name_guess_2"] = if(correctIndex == 2) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size -1))
    //Query the global facts to see if we have met before:
    story.variablesState["met_before"] = FactsOfTheWorld.getFactList<String>(Facts.NpcsPlayerHasMet).contains(antagonist.id)
    /*
    VAR met_before = false
VAR c_name = "Petter Knöös"
VAR knows_c_name = false
VAR guessed_right = false

VAR name_guess_0 = "Petter Knöös"
VAR name_guess_1 = "Frank Artschwager"
VAR name_guess_2 = "Ellika Skoogh"
     */


  }
  override val antagonistCanSpeak: Boolean
    get() = story.canContinue()
  override val protagonistCanChoose: Boolean
    get() = story.currentChoices.size > 0

  override val choiceCount: Int
    get() = story.currentChoices.size

  override fun getAntagonistLines(): Iterable<String> {
    val lines = mutableListOf<String>()
    if(story.canContinue()) {
      while (story.canContinue()) {
        lines.add(story.Continue())
      }
    }
    return lines
  }

  override fun getProtagonistChoices(): Iterable<String> {
      return story.currentChoices.map { it.text }
  }

  override fun makeChoice(index: Int): Boolean {
    if(index in 0..story.currentChoices.size -1) {
      story.chooseChoiceIndex(index)
      return true
    }
    return false
  }
}

