package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.Timer
import com.lavaeater.Assets
import com.lavaeater.kftw.map.getWorldScreenCoordinats
import com.lavaeater.kftw.map.tileWorldCenter
import com.lavaeater.kftw.statemachine.StateMachine
import ktx.actors.keepWithinParent
import ktx.actors.txt
import ktx.app.KtxInputAdapter
import ktx.math.vec2
import ktx.math.vec3
import story.IConversation
import ui.IConversationPresenter
import ui.image
import ui.label

class ConversationPresenter(override val s: Stage, override val conversation: IConversation, override val conversationEnded: () -> Unit) : IConversationPresenter {
  private val speechBubbleNinePatch = NinePatchDrawable(Assets.speechBubble)
  private val speechBubbleStyle = Label.LabelStyle(Assets.standardFont, Color.BLACK).apply { background = speechBubbleNinePatch }
  private val tableBg by lazy { NinePatchDrawable(Assets.tableBackGround) }

  private val cWidth = s.width / 3
  private val cHeight = s.height / 3

  private val pX = cWidth / 2
  private val pY = s.height / 3 + cHeight

  private val aX = pX + s.width / 2
  private val aY = pY

	private val antagonistScreen = getWorldScreenCoordinats(conversation.antagonist.currentX, conversation.antagonist.currentY)
	private val antagonistStage = s.screenToStageCoordinates(antagonistScreen.toVec2())

  private lateinit var aLabel: Label
  //private lateinit var aCell: Cell<Label>
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

        if (keycode !in 7..16) return true//Not a numeric key!
        val index = keycode - 7
        makeChoice(index)
        return true
      }
    }

    aTable = ktx.scene2d.table {

	    aLabel = label("I don't want anything to happen anymore.\nI Want to take control and make it happen. This is  a long line before a break\nIt must work with word wrap.", speechBubbleStyle) {
		    setWrap(true)
		    keepWithinParent()
	    }.cell(expandY = true, width = 128f, align = Align.topRight, padLeft = 16f, padBottom = 2f)
	    row()
	    image(Assets.portraits["orc"]!!) {
		    setScaling(Scaling.fit)
		    keepWithinParent()
	    }.cell(fill = true, width = 32f, height = 32f, align = Align.bottomLeft,pad = 2f, colspan = 2)
      x = antagonistStage.x + 20f
      y = antagonistStage.y + 60f
      isVisible = true
	    debug = true
//	    width = prefWidth
//	    height = prefHeight
	    pack()
    }

    //aLabel = aCell.actor

    s.addActor(aTable)
    stateMachine.initialize()
  }

  override fun dispose() {
  }

  fun showProtagonistChoices(protagonistChoices: Iterable<String>) {
//    pTable.invalidate()
//	  pTable.isVisible = true
//    var choiceText = ""
//    for ((i, line) in protagonistChoices.withIndex()) {
//      choiceText += "$i: " + line + "\n\n"
//    }
//    pLabel.txt = ""
//    pLabel.txt = choiceText
//    pLabel.invalidate()
//    pLabel.width = pLabel.parent.width
//    pLabel.parent.height = pLabel.prefHeight
//    pLabel.isVisible = true
  }

  fun showAntagonistLines(lines: Iterable<String>) {
//    aTable.isVisible = true
//    aLabel.txt = ""
//    aLabel.isVisible = true
//    Timer.instance().clear()
//    var index = 0
//
//    Timer.instance().scheduleTask(object: Timer.Task() {
//      override fun run() {
//        if(index < lines.count()) {
//          aLabel.text.append(lines.elementAt(index) + "\n\n")
//	        index++
//          aLabel.invalidate()
//          aLabel.width = aLabel.parent.width //We might need TWO tables... don't know yet
//          aLabel.parent.height = aLabel.prefHeight
//        } else {
//          //Last time we're running, send done event!
//          stateMachine.acceptEvent(ConversationEvent.AntagonistDoneTalking)
//        }
//	      aTable.invalidate()
//      }
//    }, 0f, 2f, lines.count())
  }

  private fun makeChoice(index: Int) {
    if(stateMachine.currentState.state == ConversationState.ProtagonistChoosing &&
        conversation.protagonistCanChoose) {
      if(conversation.makeChoice(index)) {
        //pLabel.isVisible = false
        stateMachine.acceptEvent(ConversationEvent.ProtagonistMadeAChoice)
      }
    }

  }

  fun stateChanged(state:ConversationState) {
    when (state) {
      ConversationState.NotStarted -> stateMachine.acceptEvent(ConversationEvent.ConversationStarted)
      ConversationState.Ended -> conversationEnded()
      ConversationState.AntagonistIsSpeaking -> letTheManSpeak()
      ConversationState.ProtagonistChoosing -> makeTheManChoose()
      else -> checkIfWeAreDone()
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
    //pLabel.isVisible = false
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

private fun Vector3.toVec2(): Vector2 {
	return vec2(this.x, this.y)
}
