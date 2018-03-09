package com.lavaeater.kftw.systems

import com.badlogic.ashley.systems.IteratingSystem
import com.lavaeater.kftw.components.TransformComponent
import ktx.ashley.allOf

class CurrentTileSystem : IteratingSystem(allOf(TransformComponent::class,).get()) {
}