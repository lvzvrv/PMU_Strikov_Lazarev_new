package com.example.referencebook

import android.content.Context
import android.opengl.GLES20
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log

class MoonRender(private val context: Context) : GLSurfaceView.Renderer {
    private lateinit var sphereMoon: SphereMoon
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        sphereMoon = SphereMoon(context, 0.6f)
        sphereMoon.loadTexture(R.drawable.moon_texture)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        //устанавливаем видовую матрицу
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 4f, 0f, 0f, 0f, 0f, 1f, 0f)
        //Создаёт модельно-видово-проекционную (MVP) матрицу
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        Matrix.rotateM(mvpMatrix, 0, -90f, 1f, 0f, 0f)

        sphereMoon.draw(mvpMatrix, viewMatrix, floatArrayOf(4f, -4f, 3f))
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
    }
}