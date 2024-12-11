package com.example.referencebook


import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.GLU
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Render(context: Context) : GLSurfaceView.Renderer {
    private val texturedSquare: Square = Square(context)
    private val cube = Cube()

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        gl.glEnable(GL10.GL_TEXTURE_2D)
        texturedSquare.loadTexture(gl, R.drawable.space)
        gl.glEnable(GL10.GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()

        GLU.gluPerspective(gl, 45.0f, width.toFloat() / height.toFloat(), 0.1f, 100.0f)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        gl.glLoadIdentity()
        gl.glTranslatef(0.0f, 0.0f, -5.0f)
        gl.glScalef(2.0f, 2.0f, 1.0f)
        texturedSquare.draw(gl)

        gl.glPushMatrix()
        gl.glTranslatef(0.0f, 0.0f, 1.0f)
        gl.glScalef(0.2f, 0.2f, 0.2f)
        gl.glRotatef(30.0f, 1.0f, 0.5f, 0.0f)
        cube.draw(gl)
        gl.glPopMatrix()
    }
}
