package com.lavaeater.screens

import com.badlogic.gdx.Screen

/**
 * Created by barry on 12/8/15 @ 8:13 PM.
 */
interface IScreenDispatcher {

    fun endCurrentScreen()
    val nextScreen: Screen
}
