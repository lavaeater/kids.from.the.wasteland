@file:JvmName("Lwjgl3Launcher")

package core.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import core.KidsFromTheWasteland

/** Launches the desktop (LWJGL3) application. */
fun main(args: Array<String>) {
    Lwjgl3Application(KidsFromTheWasteland(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("KidsFromTheWasteland")
        setWindowedMode(640, 480)
        setResizable(false)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
