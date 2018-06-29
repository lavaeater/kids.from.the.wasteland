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
import data.GameSettings
import data.Player
import factory.ActorFactory
import factory.BodyFactory
import ktx.box2d.createWorld
import ktx.inject.Context
import managers.*
import map.IMapManager
import map.MapManager
import map.TileManager
import story.FactsOfTheWorld
import story.RulesOfTheWorld
import story.StoryManager
import story.conversation.ConversationManager
import story.places.PlacesOfTheWorld
import systems.*
import ui.IUserInterface
import ui.UserInterface

class Ctx {

  companion object {
    val context = Context()

	  private fun getEngine(context: Context) : Engine {
		  return Engine().apply {
			  addSystem(GameInputSystem(
					  inputProcessor = context.inject(),
					  gameState = context.inject()))
			  addSystem(NpcControlSystem())
			  addSystem(RenderMapSystem(
					  context.inject(),
					  context.inject(),
					  context.inject(),
					  false))
			  addSystem(RenderCharactersSystem(context.inject()))
				addSystem(RenderFeatureSystem(context.inject()))
			  addSystem(AiSystem())
			  addSystem(PhysicsSystem(context.inject()))
			  addSystem(PhysicsDebugSystem(
						context.inject(),
						context.inject()))
			  addSystem(WorldFactsSystem())
				addSystem(FollowCameraSystem(context.inject()))
				addSystem(PlayerEntityDiscoverySystem())
				addSystem(FeatureDiscoverySystem())
			}
	  }

    fun buildContext(gameSettings: GameSettings) {
      context.register {
	      bindSingleton(gameSettings)
	      bindSingleton(FactsOfTheWorld(Gdx.app.getPreferences("default"),true)
						.apply {
		      setupInitialFacts()
	      })
	      bindSingleton(RulesOfTheWorld()) //Might be pointless
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

	      bindSingleton<Telegraph>(MessageTelegraph(this.inject()))

	      bindSingleton<MessageDispatcher>(
			      com.badlogic.gdx.ai.msg.MessageManager
					      .getInstance().apply {
						      addListeners(this@register.inject(),
								      Messages.CollidedWithImpassibleTerrain,
								      Messages.EncounterOver,
								      Messages.PlayerWentToAPlace,
											Messages.PlayerEnteredANewLocation,
								      Messages.FactsUpdated,
								      Messages.PlayerMetSomeone,
								      Messages.StoryCompleted)
					      })

	      bindSingleton(createWorld().apply {
		      setContactListener(CollisionListener(this@register.inject()))
	      })
        bindSingleton(BodyFactory(this.inject()))
        bindSingleton<IMapManager>(MapManager(
						this.inject(),
						this.inject()))

	      bind {
		      ActorFactory(
				      this.inject(),
				      this.inject(),
				      this.inject(),
				      this.inject(),
				      this.inject())
	      }

	      bindSingleton(getEngine(this))


	      bindSingleton<IUserInterface>(
			      UserInterface(
					      this.inject(),
					      this.inject(),
					      this.inject<InputProcessor>() as InputMultiplexer,
					      this.inject()))

	      bindSingleton(ConversationManager(
			      this.inject(),
			      this.inject()
	      ))

	      bindSingleton(StoryManager())

				bindSingleton(PlacesOfTheWorld())

	      bindSingleton(GameManager(
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