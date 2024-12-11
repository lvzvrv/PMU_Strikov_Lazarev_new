package com.example.referencebook

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class Sphere(private val context: Context, private val R: Float) {
    private val mVertexBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer
    private val textures = IntArray(11)
    private var n = 0

    init {
        val dtheta = 15
        val dphi = 15
        val DTOR = Math.PI / 180.0

        val byteBuf = ByteBuffer.allocateDirect(5000 * 3 * 4).apply {
            order(ByteOrder.nativeOrder())
        }
        mVertexBuffer = byteBuf.asFloatBuffer()

        val textureByteBuf = ByteBuffer.allocateDirect(5000 * 2 * 4).apply {
            order(ByteOrder.nativeOrder())
        }
        textureBuffer = textureByteBuf.asFloatBuffer()

        for (theta in -90 until 90 step dtheta) {
            for (phi in 0 until 360 step dphi) {

                mVertexBuffer.put((cos(theta * DTOR) * cos(phi * DTOR)).toFloat() * R)
                mVertexBuffer.put((cos(theta * DTOR) * sin(phi * DTOR)).toFloat() * R)
                mVertexBuffer.put(sin(theta * DTOR).toFloat() * R)

                mVertexBuffer.put((cos((theta + dtheta) * DTOR) * cos(phi * DTOR)).toFloat() * R)
                mVertexBuffer.put((cos((theta + dtheta) * DTOR) * sin(phi * DTOR)).toFloat() * R)
                mVertexBuffer.put(sin((theta + dtheta) * DTOR).toFloat() * R)

                mVertexBuffer.put((cos((theta + dtheta) * DTOR) * cos((phi + dphi) * DTOR)).toFloat() * R)
                mVertexBuffer.put((cos((theta + dtheta) * DTOR) * sin((phi + dphi) * DTOR)).toFloat() * R)
                mVertexBuffer.put(sin((theta + dtheta) * DTOR).toFloat() * R)

                mVertexBuffer.put((cos(theta * DTOR) * cos((phi + dphi) * DTOR)).toFloat() * R)
                mVertexBuffer.put((cos(theta * DTOR) * sin((phi + dphi) * DTOR)).toFloat() * R)
                mVertexBuffer.put(sin(theta * DTOR).toFloat() * R)

                n += 4

                textureBuffer.put(phi / 360.0f)
                textureBuffer.put(1.0f - (theta + 90) / 180.0f)

                textureBuffer.put(phi / 360.0f)
                textureBuffer.put(1.0f - (theta + dtheta + 90) / 180.0f)

                textureBuffer.put((phi + dphi) / 360.0f)
                textureBuffer.put(1.0f - (theta + dtheta + 90) / 180.0f)

                textureBuffer.put((phi + dphi) / 360.0f)
                textureBuffer.put(1.0f - (theta + 90) / 180.0f)
            }
        }
        mVertexBuffer.position(0)
        textureBuffer.position(0)
    }

    fun getTextureId(index: Int): Int {
        return textures[index]
    }

    fun loadTexture(gl: GL10, resourceId: Int, index: Int) {
        gl.glGenTextures(1, textures, index)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[index])

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
    }

    fun draw(gl: GL10) {
        gl.glPushMatrix()
        gl.glRotatef(90f, 1.0f, 0.0f, 0.0f)
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        for (i in 0 until n step 4) {
            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, i, 4)
        }
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glPopMatrix()
    }
}
