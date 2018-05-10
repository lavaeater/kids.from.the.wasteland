package ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor


inline fun <S> KWidget<S>.label(
    text: CharSequence,
    style: Label.LabelStyle,
    init: (@Scene2dDsl Label).(S) -> Unit = {}) = actor(Label(text, style), init)