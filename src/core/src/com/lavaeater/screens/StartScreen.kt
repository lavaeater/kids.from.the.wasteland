package com.lavaeater.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.lavaeater.Assets
import com.lavaeater.Game
import com.lavaeater.Player
import com.lavaeater.gamestate.GameStartEvent
import com.lavaeater.util.XBox360Pad

/**
 * Created by barry on 12/8/15 @ 8:24 PM.
 */
class StartScreen(internal var batch: SpriteBatch) : ScreenAdapter() {

    internal var cam: OrthographicCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    private val shapeRenderer: ShapeRenderer
    private val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P.ttf"))
    private val unselectedParams = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
        color = Color.GRAY
        size = 24
    }
    private val selectedParams = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
        color = Color.WHITE
        size = 24
    }
    private val unselectedFont: BitmapFont
    private val selectedFont: BitmapFont

    private val keyInputListener = object: InputProcessor {
        override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
            return true
        }

        override fun keyTyped(character: Char): Boolean {
            return true
        }

        override fun scrolled(amount: Int): Boolean {
            return true
        }

        override fun keyUp(keycode: Int): Boolean {
            return true
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            return true
        }

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            return true
        }

        override fun keyDown(keycode: Int): Boolean {
            when(keycode) {
                Input.Keys.ENTER -> Game.instance.executor.fire(GameStartEvent())
                Input.Keys.SPACE -> addKeyboardPlayerIfNotExists()
                Input.Keys.BACKSPACE -> removeKeyboardPlayerIfExists()
            }

            return true
        }
    }

    private val controllerListener = object: ControllerListener {
        override fun connected(controller: Controller?) {
        }

        override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
            /*
            If button a, then create player (if not already created), connect controller

            Fuck the keyboard shit, that is for losers. You're a winner, you just
            code the fuck
            away
            If button B, disconnect / remove the player object
             */
            val sc = controller!! //Nullsafety

            if(buttonCode == XBox360Pad.BUTTON_A)
                addPlayerIfNotExists(sc)

            if(buttonCode == XBox360Pad.BUTTON_B)
                removePlayerIfExists(sc)

            if(buttonCode == XBox360Pad.BUTTON_START)
                Game.instance.executor.fire(GameStartEvent())

            return false
        }

        override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
return false
        }

        override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
return false
        }

        override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
            return false
        }

        override fun disconnected(controller: Controller?) {
        }

        override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
            return false
        }

        override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
            return false
        }

        override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
            return false
        }
    }

    private fun removePlayerIfExists(controller: Controller) {
        if(Game.instance.hasPlayer(controller))
            Game.instance.removePlayer(controller)
    }

    private fun addPlayerIfNotExists(sc: Controller) {
        if(!Game.instance.hasPlayer(sc))
            Game.instance.addPlayer(sc)
    }

    private fun addKeyboardPlayerIfNotExists() {
        if(!Game.instance.hasKeyboardPlayer())
            Game.instance.addKeyboardPlayer()
    }

    private fun removeKeyboardPlayerIfExists() {
        if(Game.instance.hasKeyboardPlayer())
            Game.instance.removeKeyboardPlayer()
    }

    init {
        cam.position.set(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f, 0f)
        this.shapeRenderer = ShapeRenderer()
        unselectedFont = fontGenerator.generateFont(unselectedParams) //generate 2 fonts, one for active and one for inactive
        selectedFont = fontGenerator.generateFont(selectedParams)
        fontGenerator.dispose()
    }

    fun drawPlayerText(batch: SpriteBatch, p:Int, selected: Boolean) {
        val playerText = "P$p"
        val x = (Gdx.graphics.width / 5).toFloat()
        val y = (Gdx.graphics.height / 2).toFloat()
        if(selected) {
            selectedFont.draw(batch, playerText, (p * x), y)
        }
        else
            unselectedFont.draw(batch, playerText, (p * x), y)
    }

    fun drawPlayerScore(batch: SpriteBatch, player: Player, p:Int) {
        val x = (Gdx.graphics.width / 5).toFloat()
        val y = (Gdx.graphics.height / 2).toFloat() + 100
        selectedFont.draw(batch, "LastGame: ${player.position}", (p * x), y)
    }

    override fun hide() {
        super.hide()
        Controllers.removeListener(controllerListener)
    }

    override fun show() {
        super.show()
        Controllers.addListener(controllerListener)
        Gdx.input.inputProcessor = keyInputListener
    }

    override fun render(delta: Float) {
        super.render(delta)
        cam.update()
        batch.projectionMatrix = cam.combined
        batch.enableBlending()
        batch.begin()

        var x=0.0f
        var y=0.0f
        for(sprite in Assets.darkDirtSprites.values) {
            sprite.x = 300 + (x *8)
            sprite.y = 300 + (y * 8)
            sprite.draw(batch)
            x++
            y++
        }

        var p = 1
        while(p < 5) {
            drawPlayerText(batch, p, Game.instance.hasPlayer(p))
            if(Game.instance.hasPlayer(p))
                drawPlayerScore(batch, Game.instance.getPlayer(p), p)
            p++
        }
        batch.end()

        Gdx.gl20.glLineWidth(1f)
        shapeRenderer.projectionMatrix = cam.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.CYAN
        shapeRenderer.rect(0f, 0f, cam.viewportWidth * Assets.am.getProgress(), cam.viewportHeight / 5f)
        shapeRenderer.end()
    }
}

