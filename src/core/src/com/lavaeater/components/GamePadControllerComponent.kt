package com.lavaeater.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.controllers.Controller
import com.lavaeater.Player

/**
 * Created by barry on 12/9/15 @ 11:49 PM.
 */
class GamePadControllerComponent(val controller: Controller, val player: Player) : Component
