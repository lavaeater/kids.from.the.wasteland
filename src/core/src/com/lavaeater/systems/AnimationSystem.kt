package com.lavaeater.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.lavaeater.components.AnimationComponent
import com.lavaeater.components.StateComponent
import com.lavaeater.components.TextureComponent

class AnimationSystem : IteratingSystem(Family.all(TextureComponent::class.java,
        AnimationComponent::class.java,
        StateComponent::class.java).get()) {

    internal var tm: ComponentMapper<TextureComponent>
    internal var am: ComponentMapper<AnimationComponent>
    internal var sm: ComponentMapper<StateComponent>

    init {

        tm = ComponentMapper.getFor<TextureComponent>(TextureComponent::class.java)
        am = ComponentMapper.getFor<AnimationComponent>(AnimationComponent::class.java)
        sm = ComponentMapper.getFor<StateComponent>(StateComponent::class.java)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        val ani = am.get(entity)
        val state = sm.get(entity)

        //Aaah, statehantering, nu ska det ske!

//        if (ani.animations.containsKey(state.isThrusting())) {
//            val tex = tm.get(entity)
//            //state.isThrusting()).getKeyFrame(state.time, state.isLooping
//            tex.region = ani.animations[state.isThrusting()].getKeyFrame(state.time, state.isLooping)
//        }

        state.time += deltaTime
    }
}
