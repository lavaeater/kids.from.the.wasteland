package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.lavaeater.Assets
import com.lavaeater.kftw.statemachine.StateMachine
import ktx.actors.txt
import ktx.app.KtxInputAdapter
import story.IConversation
import ui.label

class ConversationPresenter(override val s: Stage, override val conversation: IConversation, override val conversationEnded: () -> Unit) : IConversationPresenter {

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
      }

  override fun dispose() {
    table.remove()
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

  fun showNextAnttagonistLine(nextAntagonistLine: String) {
    aLabel.txt = ""
    aLabel.text.append(nextAntagonistLine + "\n\n")
    aLabel.invalidate()
    aLabel.width = aLabel.parent.width //We might need TWO tables... don't know yet
    aLabel.parent.height = aLabel.prefHeight
    aLabel.isVisible = true

    Thread.sleep(2000)
  }

  private val npd = NinePatchDrawable(Assets.speechBubble)
  private val speechBubbleStyle = Label.LabelStyle(Assets.standardFont, Color.BLACK).apply { background = npd }

  private lateinit var pLabel: Label
  private lateinit var aLabel: Label
  private var table: Table

  init {
    Gdx.input.inputProcessor = object : KtxInputAdapter {
      override fun keyDown(keycode: Int): Boolean {
        if (keycode !in 7..16) return true//Not a numeric key!
        val index = keycode - 7
        makeChoice(index)
        return true
      }
    }

    table = ktx.scene2d.table {
      pLabel = label("", speechBubbleStyle) {
        isVisible = false
      }
      aLabel = label("", speechBubbleStyle) {
        isVisible = false
      }
      width = 400f
      x = s.camera.position.x
      y = s.camera.position.y
      isVisible = true
      setDebug(true)
    }
    s.addActor(table)
    stateMachine.initialize()
  }

  private fun makeChoice(index: Int) {
    if(conversation.protagonistCanChoose) {
      if(conversation.makeChoice(index))
        stateMachine.acceptEvent(ConversationEvent.ProtagonistMadeAChoice)
    }

  }

  private lateinit var _state: ConversationState

  fun stateChanged(state:ConversationState) {
    _state = state
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
    while(conversation.antagonistCanSpeak) {
      showNextAnttagonistLine(conversation.getNextAntagonistLine())
    }
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
