package com.lavaeater.kftw.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import data.GameSettings
import com.lavaeater.kftw.KidsFromTheWastelandGame

/** Launches the desktop (LWJGL3) application.  */
object Lwjgl3Launcher {

	private val defaultConfiguration: Lwjgl3ApplicationConfiguration
		get() {
			val configuration = Lwjgl3ApplicationConfiguration()
			configuration.setTitle("Lost Beamon People")
			configuration.setWindowedMode(1920, 1080)
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