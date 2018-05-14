package ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor


inline fun <S> KWidget<S>.label(
    text: CharSequence,
    style: Label.LabelStyle,
    init: (@Scene2dDsl Label).(S) -> Unit = {}) = actor(Label(text, style), init)

inline fun <S> KWidget<S>.image(
		texture: Texture,
		init: (@Scene2dDsl Image).(S) -> Unit = {}) = actor(Image(TextureRegionDrawable(TextureRegion(texture))), init)