package com.lavaeater.kftw.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.lavaeater.kftw.KidsFromTheWasteLandGame

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.fullscreen = true
        LwjglApplication(KidsFromTheWasteLandGame(), config)
    }
}
