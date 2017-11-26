package com.lavaeater.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.ArrayMap

/**
 * Created by barry on 12/8/15 @ 8:30 PM.
 */
class AnimationComponent : Component {
    var animations = ArrayMap<String, Animation<TextureRegion>>()

}
