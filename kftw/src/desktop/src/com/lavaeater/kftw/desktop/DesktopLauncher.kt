package com.lavaeater.kftw.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.lavaeater.kftw.KidsFromTheWasteLandGame

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.fullscreen = false
        config.width = 1024
        config.height = 768
        config.vSyncEnabled = true
        LwjglApplication(KidsFromTheWasteLandGame(), config)
    }
}
