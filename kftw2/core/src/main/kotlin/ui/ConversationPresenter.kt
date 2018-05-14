package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Timer
import com.lavaeater.Assets
import com.lavaeater.kftw.statemachine.StateMachine
import ktx.actors.txt
import ktx.app.KtxInputAdapter
import story.IConversation
import ui.image
import ui.label

class ConversationPresenter(override val s: Stage, override val conversation: IConversation, override val conversationEnded: () -> Unit) : IConversationPresenter {
  private val npd = NinePatchDrawable(Assets.speechBubble)
  private val speechBubbleStyle = Label.LabelStyle(Assets.standardFont, Color.BLACK).apply { background = npd }

  private lateinit var pLabel: Label
  private lateinit var aLabel: Label
  private var pTable: Table
  private var aTable: Table

  val stateMachine : StateMachine<ConversationState, ConversationEvent> =
      StateMachine.buildStateMachine(ConversationState.NotStarted, ::stateChanged) {
        state(ConversationState.NotStarted) {
          edge(ConversationEvent.ConversationStarted, ConversationState.AntagonistIsSpeaking) {}
        }
        state(ConversationState.AntagonistIsSpeaking) {
          edge(ConversationEvent.AntagonistDoneTalking, ConversationState.ProtagonistChoosing) {}
          edge(ConversationEvent.ConversationEnded, ConversationState.Ended) {}
        }
        state(ConversationState.ProtagonistChoosing) {
          edge(ConversationEvent.ProtagonistMadeAChoice, ConversationState.AntagonistIsSpeaking) {}
          edge(ConversationEvent.ProtagonistCouldNotChoose, ConversationState.CanConversationContinue) {}
          edge(ConversationEvent.ConversationEnded, ConversationState.Ended) {}
        }
        state(ConversationState.CanConversationContinue) {
          edge(ConversationEvent.ConversationEnded, ConversationState.Ended) {}
        }
        state(ConversationState.Ended) {}
      }

  init {
    Gdx.input.inputProcessor = object : KtxInputAdapter {
      override fun keyDown(keycode: Int): Boolean {
	      if(stateMachine.currentState == ConversationState.ProtagonistChoosing)
        if (keycode !in 7..16) return true//Not a numeric key!
        val index = keycode - 7
        makeChoice(index)
        return true
      }
    }

    pTable = ktx.scene2d.table {
      pLabel = label("", speechBubbleStyle) {
        isVisible = false
      }
      image(Assets.portraits["femalerogue"]!!) {
	      scaleBy(4f)
      }
      width = 300f
      x = s.camera.position.x - 100f
      y = s.camera.position.y
      isVisible = true
	    debug = true
    }

    aTable = ktx.scene2d.table {
      aLabel = label("", speechBubbleStyle) {
        isVisible = false
      }
      image(Assets.portraits["orc"]!!) {
	      scaleBy(4f)
      }
      width = 300f
      x = s.camera.position.x + 100f
      y = s.camera.position.y
      isVisible = true
	    debug = true
    }
    s.addActor(pTable)
    s.addActor(aTable)
    stateMachine.initialize()
  }


  override fun dispose() {
    pTable.remove()
  }

  fun showProtagonistChoices(protagonistChoices: Iterable<String>) {
    var choiceText = ""
    for ((i, line) in protagonistChoices.withIndex()) {
      choiceText += "$i: " + line + "\n\n"
    }
    pLabel.txt = ""
    pLabel.txt = choiceText
    pLabel.invalidate()
    pLabel.width = pLabel.parent.width
    pLabel.parent.height = pLabel.prefHeight
    pLabel.isVisible = true
  }

  fun showAntagonistLines(lines: Iterable<String>) {
    aLabel.txt = ""
    aLabel.isVisible = true
    Timer.instance().clear()
    var index = 0

    Timer.instance().scheduleTask(object: Timer.Task() {
      override fun run() {
        if(index < lines.count()) {
          aLabel.text.append(lines.elementAt(index) + "\n\n")
          index++
          aLabel.invalidate()
          aLabel.width = aLabel.parent.width //We might need TWO tables... don't know yet
          aLabel.parent.height = aLabel.prefHeight
        } else {
          //Last time we're running, send done event!
          stateMachine.acceptEvent(ConversationEvent.AntagonistDoneTalking)
        }
      }
    }, 0f, 2f, lines.count())
  }

  private fun makeChoice(index: Int) {
    if(conversation.protagonistCanChoose) {
      if(conversation.makeChoice(index))
        pLabel.isVisible = false
        stateMachine.acceptEvent(ConversationEvent.ProtagonistMadeAChoice)
    }

  }

  fun stateChanged(state:ConversationState) {
    when (state) {
      ConversationState.NotStarted -> stateMachine.acceptEvent(ConversationEvent.ConversationStarted)
      ConversationState.Ended -> conversationEnded()
      ConversationState.AntagonistIsSpeaking -> letTheManSpeak()
      ConversationState.ProtagonistChoosing -> makeTheManChoose()
      ConversationState.CanConversationContinue -> checkIfWeAreDone()
    }
  }

  private fun checkIfWeAreDone() {
    stateMachine.acceptEvent(ConversationEvent.ConversationEnded)
  }

  private fun makeTheManChoose() {
    if(conversation.protagonistCanChoose)
      showProtagonistChoices(conversation.getProtagonistChoices())
    else
      stateMachine.acceptEvent(ConversationEvent.ProtagonistCouldNotChoose)
  }


  private fun letTheManSpeak() {
    pLabel.isVisible = false
    if(conversation.antagonistCanSpeak)
      showAntagonistLines(conversation.getAntagonistLines())
    else
      stateMachine.acceptEvent(ConversationEvent.AntagonistDoneTalking)
  }

  enum class ConversationEvent {
    ConversationStarted,
    AntagonistDoneTalking,
    ProtagonistMadeAChoice,
    ConversationEnded,
    ProtagonistCouldNotChoose
  }

  enum class ConversationState {
    NotStarted,
    Ended,
    AntagonistIsSpeaking,
    ProtagonistMustChoose,
    ProtagonistChoosing,
    CanConversationContinue
  }
}
