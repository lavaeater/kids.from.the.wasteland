package com.lavaeater.screens

import com.badlogic.gdx.Screen
import java.util.*

/**
 * Created by barry on 12/8/15 @ 8:14 PM.
 */
class ScreenDispatcher internal constructor() : IScreenDispatcher {

    var screens: ArrayList<Screen> = ArrayList()
    private var isCurrenScreenEnded = false
    private var currentIndex = 0


    fun AddScreen(screen: Screen) {
        screens.add(screen)
    }


    override fun endCurrentScreen() {
        isCurrenScreenEnded = true
    }

    override //Do logic to pick the next screen
    val nextScreen: Screen
        get() {
            if (isCurrenScreenEnded) {
                isCurrenScreenEnded = false
                currentIndex++
            }

            if (screens.size > currentIndex) {
                return screens[currentIndex]
            } else {
                return screens[0]
            }
        }
}
