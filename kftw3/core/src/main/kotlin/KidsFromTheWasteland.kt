import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graph.Graph
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.graphics.use
import world.MapBuilder

class FirstScreen : KtxScreen {
    private val image = Texture("ktx-logo.png")
    private val batch = SpriteBatch()

    override fun show() {
        super.show()

        val worldGraph = MapBuilder.createWorld(0,0,3,3)
        //2. Serialize to JSON
        val mapper = jacksonObjectMapper()
        val writer = mapper.writerWithDefaultPrettyPrinter()
        val data = writer.writeValueAsString(worldGraph)
        //3. Write to file
        val file = Gdx.files.local("world.json")
        file.writeString(data, false)

        val readfile = Gdx.files.local("world.json")

        val readData = readfile.readString()
        //3. read?

        val readWorld = mapper.readValue(readData, Graph::class.java)
    }

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
