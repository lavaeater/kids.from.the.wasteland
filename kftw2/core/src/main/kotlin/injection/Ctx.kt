package injection

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.lavaeater.kftw.GameSettings
import data.Player
import com.lavaeater.kftw.managers.*
import systems.*
import map.IMapManager
import map.MapManager
import ui.UserInterface
import ui.IUserInterface
import ktx.box2d.createWorld
import ktx.inject.Context
import managers.*
import map.TileManager
import world.ConversationManager

class Ctx {

  companion object {
    val context = Context()

	  fun getEngine(context: Context) : Engine {
		  return Engine().apply {
			  addSystem(CharacterControlSystem(
					  inputProcessor = context.inject(),
					  gameState = context.inject()))
			  addSystem(NpcControlSystem())
			  addSystem(RenderMapSystem(
					  context.inject(),
					  context.inject(),
					  context.inject(),
					  false))
			  addSystem(RenderCharactersSystem())
			  addSystem(AiSystem())
			  addSystem(PhysicsSystem())
//			  addSystem(PhysicsDebugSystem())
			  addSystem(WorldFactsSystem())

		  }
	  }

    fun buildContext(gameSettings: GameSettings) {
      context.register {
	      bindSingleton(GameState())
        bindSingleton<InputProcessor>(InputMultiplexer())
        bindSingleton(TileManager())
        bindSingleton(Player(name = "William Hamparsomian"))
        bindSingleton<Batch>(SpriteBatch())
        bindSingleton<Camera>(OrthographicCamera())

	      //Bind provider for a viewport with the correct settings for this game!
	      bind<Viewport> {
		      ExtendViewport(gameSettings.width,
				      gameSettings.height,
				      this.inject())
	      }

	      bindSingleton<Telegraph>(MessageSwitch())

	      bindSingleton<MessageDispatcher>(
			      com.badlogic.gdx.ai.msg.MessageManager
					      .getInstance().apply {
						      addListener(this@register.inject(), Messages.CollidedWithImpassibleTerrain)
						      addListener(this@register.inject(), Messages.PlayerMetSomeone)
					      })

	      bindSingleton(createWorld().apply {
		      setContactListener(CollisionManager(this@register.inject()))
	      })
        bindSingleton(BodyFactory())
        bindSingleton<IMapManager>(MapManager())

	      bindSingleton(getEngine(this))

	      bind { ActorFactory() }

	      bindSingleton<IUserInterface>(
			      UserInterface(
					     this.inject(),
					      this.inject()))

	      bindSingleton(ConversationManager())


	      bindSingleton(GameManager(
		        gameSettings,
			      this.inject(),
			      this.inject(),
			      this.inject(),
			      this.provider(),
			      this.inject(),
			      this.provider(),
			      this.inject(),
			      this.inject(),
			      this.inject()))
      }
    }
  }
}