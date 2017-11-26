package com.lavaeater.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.lavaeater.Assets


/**
 * Created by tommie on 2017-10-03.
 */
class ParticleManager(val batch: SpriteBatch) :Disposable {
    override fun dispose() {
        clearAll()
    }

    fun clearAll() {
        for(effect in effects)
            effect.free()

        effects.clear()
    }

    val explosionEffect: ParticleEffect by lazy { ParticleEffect()
            .apply {
        load(Gdx.files.internal("pes/explosion.p"), Assets.textureAtlas)
    }}
    val explosionEffectPool: ParticleEffectPool by lazy { ParticleEffectPool(explosionEffect,10, 100)}
    val effects = arrayListOf<PooledEffect>()
    val doneEffects = arrayListOf<PooledEffect>()

    fun explosionAt(x:Float, y:Float) {
        val effect = explosionEffectPool.obtain()

        effect.setPosition(x,y)
        effects.add(effect)
    }

    fun renderEffects(delta:Float) {
        //Hmm, might be expensive to do two begin / ends as part of the update. Move this call to the render system?
        batch.begin()
        for(effect in effects) {
            effect.draw(batch, delta)
            if (effect.isComplete) {
                effect.free()
                doneEffects.add(effect)
            }
        }
        batch.end()
        effects.removeAll(doneEffects)
        doneEffects.clear()
    }

}