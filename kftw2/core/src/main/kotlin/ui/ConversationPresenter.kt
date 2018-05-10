package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Disposable
import com.lavaeater.Assets
import com.lavaeater.kftw.statemachine.StateMachine
import com.lavaeater.kftw.ui.UserInterface.*
import ktx.actors.txt
import ktx.app.KtxInputAdapter
import ktx.scene2d.*
import story.IConversation

class ConversationPresenter(val s: Stage, val conversation: IConversation, val conversationEnded: () -> Unit) : IConversationPresenter, Disposable {

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

  override fun showProtagonistChoices(protagonistChoices: Iterable<String>) {
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

  override fun showNextAnttagonistLine(nextAntagonistLine: String) {
    aLabel.text.append(nextAntagonistLine + "\n\n")
    aLabel.invalidate()
    aLabel.width = aLabel.parent.width //We might need TWO tables... don't know yet
    aLabel.parent.height = aLabel.prefHeight
    aLabel.isVisible = true
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
        if (index !in 0..conversation.choiceCount - 1) return true//Out of range for correct choices, just ignore

        makeChoice(index)

        return true
      }
    }


    table = ktx.scene2d.table {
      pLabel = label("", speechBubbleStyle)
      aLabel = label("", speechBubbleStyle)
      width = 400f
      x = s.camera.position.x
      y = s.camera.position.y
      isVisible = true
      setDebug(true)
    }
    s.addActor(table)
  }

  private fun makeChoice(index: Int) {

    if(conversation.protagonistCanChoose) {
      conversation.makeChoice(index)
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
    while(conversation.antagonistCanSpeak) {
      showNextAnttagonistLine(conversation.getNextAntagonistLine())
      Thread.sleep(2000)
    }
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

inline fun <S> KWidget<S>.label(
    text: CharSequence,
    style: Label.LabelStyle,
    init: (@Scene2dDsl Label).(S) -> Unit = {}) = actor(Label(text, style), init)