package com.lavaeater.mapstuff

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder
import com.lavaeater.IRenderable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram


/**
 * Created by 78899 on 2017-10-09.
 */

class PrimitiveMap<T:PolygonSpriteBatch>: IRenderable<T>{

    val polygonShapeDrawer = PolygonShapeDrawer()

    override fun render(batch: T) {
    }
}

class PolygonShapeDrawer : MeshBuilder() {
    private var texture: Texture? = null

    init {
        super.begin(
                VertexAttributes(VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE), VertexAttribute
                        .ColorPacked(), VertexAttribute.TexCoords(0)), GL20.GL_TRIANGLES)
    }

    override fun end(): Mesh {
        throw GdxRuntimeException("Not supported!")
    }

    override fun end(mesh: Mesh): Mesh {
        throw GdxRuntimeException("Not supported!")
    }

    fun setTextureRegion(region: TextureRegion) {
        if (numIndices > 0)
            throw GdxRuntimeException("Cannot change the TextureRegion in while creating a shape, call draw first.")
        texture = region.texture
        setUVRange(region)
    }

    fun draw(batch: PolygonSpriteBatch) {
        if (texture == null)
            throw GdxRuntimeException("No texture specified, call setTextureRegion before creating the shape")
        batch.draw(texture, vertices, 0, numVertices * floatsPerVertex, indices, 0, numIndices)
        clear()
    }
}
