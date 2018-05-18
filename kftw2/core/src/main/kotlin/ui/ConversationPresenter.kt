package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.Timer
import com.lavaeater.Assets
import com.lavaeater.kftw.map.getWorldScreenCoordinats
import com.lavaeater.kftw.statemachine.StateMachine
import ktx.actors.*
import ktx.app.KtxInputAdapter
import ktx.math.vec2
import ktx.scene2d.KTableWidget
import ktx.scene2d.table
import ktx.scene2d.textButton
import story.IConversation
import ui.IConversationPresenter
import ui.image
import ui.label
import ui.toNumber

class ConversationPresenter(override val s: Stage, override val conversation: IConversation, override val conversationEnded: () -> Unit) : IConversationPresenter {
  private val speechBubbleNinePatch = NinePatchDrawable(Assets.speechBubble)
  private val speechBubbleStyle = Label.LabelStyle(Assets.standardFont, Color.BLACK).apply { background = speechBubbleNinePatch }

  private val baseWidth = UserInterface.uiWidth / 3
  private val baseHeight = UserInterface.uiHeight / 3

  private lateinit var antagonistSpeechBubble: Label
  private var antagonistRoot: Table
	private var protagonistRoot: Table
	private lateinit var choiceTable: KTableWidget
	private var rootTable: KTableWidget

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



	  protagonistRoot = table {
		  choiceTable = table {
			  background = speechBubbleNinePatch
			  keepWithinParent()
			  left()
			  bottom()
		  }.cell(expandY = true, width = baseWidth, align = Align.bottomRight, padLeft = 16f, padBottom = 2f)
		  row()
		  image(Assets.portraits["femalerogue"]!!) {
			  setScaling(Scaling.fit)
			  keepWithinParent()
		  }.cell(fill = true, width = baseWidth / 3, height = baseWidth / 3, align = Align.bottomLeft, pad = 2f, colspan = 2)
		  isVisible = false
		  pack()
	  }

    antagonistRoot = table {
	    antagonistSpeechBubble = label("", speechBubbleStyle) {
		    setWrap(true)
		    keepWithinParent()
	    }.cell(expandY = true, width = baseWidth, align = Align.bottomRight, padLeft = 16f, padBottom = 2f)
	    row()
	    image(Assets.portraits["orc"]!!) {
		    setScaling(Scaling.fit)
		    keepWithinParent()
	    }.cell(fill = true, width = baseWidth / 3, height = baseWidth / 3, align = Align.bottomLeft,pad = 2f, colspan = 2)
      isVisible = true
	    pack()
    }

		rootTable = table {
			setFillParent(true)
			center()
			add(protagonistRoot).expandX().align(Align.center)
			add(antagonistRoot).expandX().align(Align.center)
		}

    s.addActor(rootTable)
    s.keyboardFocus = rootTable

    rootTable.onKey { key ->
      if (key.isDigit() && key in '0'..'9') {
        makeChoice(key.toNumber())
      }
    }
    stateMachine.initialize()
  }



  override fun dispose() {
	  s.actors.removeValue(rootTable, true)
  }

  fun showProtagonistChoices(protagonistChoices: Iterable<String>) {
	  choiceTable.clearChildren()
	  protagonistRoot.isVisible = true
	  choiceTable.apply {
		  protagonistChoices.withIndex().forEach { indexedValue ->
        val text = "${indexedValue.index}: ${indexedValue.value}"
			  val button = textButton(text)
        button.onChange {
          makeChoice(indexedValue.index)
        }
//        button.addListener (object: KtxInputListener() {
//          override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
//            makeChoice(indexedValue.index)
//            return super.touchDown(event, x, y, pointer, button)
//          }
//        })
//			  button.onChange {
//          makeChoice(indexedValue.index)
//        }
			  button.label.setWrap(true)
			  add(button).align(Align.left).expandY().growX().pad(4f).row()
			  button.keepWithinParent()
		  }
	  }
	  choiceTable.pack()
    choiceTable.isVisible = true
	  protagonistRoot.invalidate()
  }

  private fun showAntagonistLines(lines: Iterable<String>) {
    antagonistRoot.isVisible = true
    antagonistSpeechBubble.txt = ""
    Timer.instance().clear()
    var index = 0

    Timer.instance().scheduleTask(object: Timer.Task() {
      override fun run() {
        if(index < lines.count()) {
          antagonistSpeechBubble.text.append(lines.elementAt(index) + "\n")
	        antagonistSpeechBubble.invalidate()
	        index++
        } else {
          //Last time we're running, send done event!
          stateMachine.acceptEvent(ConversationEvent.AntagonistDoneTalking)
        }
	      antagonistRoot.invalidate()
      }
    }, 0f, 2f, lines.count())
  }

  private fun makeChoice(index: Int) {
    if(stateMachine.currentState.state == ConversationState.ProtagonistChoosing &&
        conversation.protagonistCanChoose) {
      if(conversation.makeChoice(index)) {
        choiceTable.isVisible = false
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
    choiceTable.isVisible = false
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
