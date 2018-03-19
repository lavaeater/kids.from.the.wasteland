package com.lavaeater.kftw

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.lavaeater.Assets
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.screens.MainGameScreen
import ktx.app.KtxGame

/*
Rethink, rework.

The game itself is like, this class. Then we have a main game.
Should that main game manage a bunch of screens and systems and
assets and resources and what?

Who is the boss of this little system we have going here?

Who manages screens?

Who renders screens?

Screens are just... I don't know, just not very important, really.

I think that the correct architecture is actually the one we're going for.

The game manager manages the entire game's state, which includes what
screens are being shown. There is no start page that is not part of the game state.

This means that our game state machine can become so much more involved an also
that we can have an entire screen for the combat, inventory, etc. They CAN, and
perhaps should, be thought of as parts of the actual / main ui, but in some
instances they may not.

This also makes it easire to divvy up concepts such as detailmapviews, worldmapviews etc,
they can inherit / implement or be instances of the normal maingamescreen but with
additions / modifications.


 */

class KidsFromTheWastelandGame : KtxGame<Screen>() {

  private lateinit var mainGameScreen: MainGameScreen
  private lateinit var gameManager: GameManager

  override fun create() {
    Gdx.app.logLevel = Application.LOG_ERROR

    Assets.load()
    Ctx.buildContext()

    gameManager = GameManager(this::setScreen, this::addScreen)

    gameManager.start()
  }

  override fun dispose() {
    super.dispose()
    gameManager.stop()
    gameManager.dispose()

  }
}
