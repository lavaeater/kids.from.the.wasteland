package core

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.graphics.use

class FirstScreen : KtxScreen {
    private val image = Texture("ktx-logo.png")
    private val batch = SpriteBatch()

    override fun render(delta: Float) {
        clearScreen(0.8f, 0.8f, 0.8f)
        batch.use {
            it.draw(image, 47.5f, 140f)
        }
    }

    override fun dispose() {
        image.dispose()
        batch.dispose()
    }
}

class KidsFromTheWasteland : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}
