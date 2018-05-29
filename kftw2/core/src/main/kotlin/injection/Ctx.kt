package injection

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
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
import com.lavaeater.kftw.managers.CollisionManager
import com.lavaeater.kftw.managers.Messages
import data.Player
import ktx.box2d.createWorld
import ktx.inject.Context
import managers.*
import map.IMapManager
import map.MapManager
import map.TileManager
import systems.*
import ui.IUserInterface
import ui.UserInterface
import world.ConversationManager
import world.FactsOfTheWorld
import world.RulesOfTheWorld

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
			  addSystem(RenderCharactersSystem(context.inject()))
			  addSystem(AiSystem())
			  addSystem(PhysicsSystem(context.inject()))
//			  addSystem(PhysicsDebugSystem())
			  addSystem(WorldFactsSystem())
				addSystem(FollowCameraSystem(
						context.inject<ActorFactory>().addHeroEntity(),
						context.inject()))
			}
	  }

    fun buildContext(gameSettings: GameSettings) {
      context.register {
	      bindSingleton(FactsOfTheWorld(Gdx.app.getPreferences("default")).apply {
		      setupInitialFacts()
	      })
	      bindSingleton(RulesOfTheWorld().apply {
		      setupRules()
	      })
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

	      bindSingleton<Telegraph>(MessageSwitch(this.inject()))

	      bindSingleton<MessageDispatcher>(
			      com.badlogic.gdx.ai.msg.MessageManager
					      .getInstance().apply {
						      addListener(this@register.inject(), Messages.CollidedWithImpassibleTerrain)
						      addListener(this@register.inject(), Messages.PlayerMetSomeone)
					      })

	      bindSingleton(createWorld().apply {
		      setContactListener(CollisionManager(this@register.inject()))
	      })
        bindSingleton(BodyFactory(this.inject()))
        bindSingleton<IMapManager>(MapManager(
						this.inject(),
						this.inject()))

	      bindSingleton(getEngine(this))

	      bind { ActorFactory(
			      this.inject(),
			      this.inject(),
			      this.inject(),
			      this.inject(),
			      this.inject()) }

	      bindSingleton<IUserInterface>(
			      UserInterface(
					      this.inject(),
					      this.inject(),
					      this.inject<InputProcessor>() as InputMultiplexer,
					      this.inject()))

	      bindSingleton(ConversationManager(
						this.inject(),
						this.inject(),
						this.inject(),
			      this.inject(),
			      this.inject()
				))

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
			      this.inject(),
			      this.inject()))
      }
    }
  }
}