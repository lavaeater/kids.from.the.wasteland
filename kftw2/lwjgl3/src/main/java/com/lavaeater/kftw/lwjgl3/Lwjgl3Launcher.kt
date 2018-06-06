package com.lavaeater.kftw.lwjgl3

import com.badlogic.gdx.Graphics
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
<<<<<<< HEAD
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration
import com.lavaeater.kftw.GameSettings
=======
import data.GameSettings
>>>>>>> dev
import com.lavaeater.kftw.KidsFromTheWastelandGame

/** Launches the desktop (LWJGL3) application.  */
object Lwjgl3Launcher {

	private val defaultConfiguration: Lwjgl3ApplicationConfiguration
		get() {
			val configuration = Lwjgl3ApplicationConfiguration()
			configuration.setTitle("Lost Beamon People")
<<<<<<< HEAD
			configuration.setWindowedMode(1800, 1600)
=======
			configuration.setWindowedMode(1280, 720)
>>>>>>> dev
			configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
			return configuration
		}

	@JvmStatic
	fun main(args: Array<String>) {
		createApplication()
	}

	private fun createApplication(): Lwjgl3Application {
		//return new Lwjgl3Application(new KidsFromTheWastelandGame(), getDefaultConfiguration());
		return Lwjgl3Application(KidsFromTheWastelandGame(GameSettings(196f, 144f, 8, 16)), defaultConfiguration)
	}
}