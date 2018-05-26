package world

import com.badlogic.gdx.math.MathUtils
import com.bladecoder.ink.runtime.RTObject
import com.bladecoder.ink.runtime.Story
import com.bladecoder.ink.runtime.VariableAssignment
import com.lavaeater.kftw.data.IAgent

class InkConversation(val story:Story, override val protagonist:IAgent, override val antagonist:IAgent) : IConversation {
  init {
    story.variablesState["c_name"] = antagonist.name

    val potentialNames = mutableListOf(
        "John Linder",
        "Natalia Molina",
        "Sarah Molander",
        "Ellika Skohg",
        "Jenny Hall",
        "Kim Dinh Thi",
        "Kristina Andrén",
        "Andreas Gustafsson",
        "Jonas Personne",
        "Christine Andreasson",
        "Johan Thorgren",
        "Lina Fjeldgård",
        "Ulrica Wikren",
        "Emma Vikström",
        "Johan Odenfors",
        "Homan Peida",
        "Jessica Tennfors",
        "Maria Söderlund",
        "Madeleine Marmborg",
        "Ilhan Erdal",
        "Elisabeth Swahn",
        "Louise Thessman",
        "Jessika Nilsson",
        "Jennie Frejd",
        "Daniel Grufman",
        "Jan Pettersson",
        "Frida Kilany",
        "Daniel Madsen",
        "Marta Muñoz",
        "Kristoffer Skog",
        "Niklas Sjöström",
        "Malin Karlsson",
        "Nazila Norén",
        "Agata Roberts",
        "David Zevallos",
        "Petra Englund",
        "Maryam Engelbrecht",
        "Christoffer Nordvall",
        "Daniel Koenig",
        "Julia Garellick Lindborg",
        "Lotta Asplund",
        "Ruken Cetiner",
        "David Holmstrand",
        "Jon Wålstedt",
        "Thomas Larsson",
        "Christoffer Johansson",
        "Sonia Ling Persson",
        "Sofia Bråvander",
        "Ilona Jastrzebska",
        "Rosie Jonsson",
        "Isabell Sollman",
        "Niklas Peiper",
        "Adam Boman",
        "Kristin Stråhle",
        "Rebecca Hedberg",
        "Anita Sehat",
        "Elin Karasalo",
        "Mårten Hedlund",
        "Eddie Bjuvenius",
        "Karolina Willner",
        "Andreas Lindblad",
        "Claes Lundgren",
        "Mimmi Boman",
        "Kjell Bernhager",
        "Fredrik Anundsson",
        "Jörgen Eskelid",
        "Kalle Hansson",
        "Sabine Neumann",
        "Oskar Grunning",
        "Petter Knöös",
        "Mathias Lindberg",
        "Omid Safiyari",
        "Maria Herkner",
        "Johan Timander",
        "Jonas Oskö",
        "Fredrik Lindvall",
        "Jens Tinfors",
        "Peter Andersson",
        "Malin Forne",
        "Elias Nilsson",
        "Fredrik Meyer",
        "Daniel J:son Lindh",
        "Tommie Nygren",
        "Babak Varfan",
        "Sebastian Qvarfordt",
        "Sandra Damavandi",
        "Annika Uppenberg",
        "Louise Ajax",
        "Felix Lindström",
        "Anna Fjellström").filter { it != antagonist.name }.toMutableList()

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
