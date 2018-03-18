package story

import com.badlogic.gdx.Gdx
import com.bladecoder.ink.runtime.Choice
import com.bladecoder.ink.runtime.Story

/**
 * Created by tommie on 2018-03-18.
 */

class StoryManager {
  val story = Story(Gdx.files.internal("ink/start.ink.json").readString())


  fun continueStory() {
    val lines = mutableListOf<String>()
    while(story.canContinue()) {
      //Get current lines until we stop for choices.
      lines.add(story.Continue())
    }
    showStoryLines(lines)
    showChoices(story.currentChoices)
  }

  fun showStoryLines(lines: List<String>) {
    //Show them somehow.
  }

  fun showChoices(choices: List<Choice>) {
    //Show some goddamned choices
  }

  fun makeChoice(index: Int) {
    //Make a friggin' choice
    story.chooseChoiceIndex(index)
    continueStory()
  }





}