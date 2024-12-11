package com.example.referencebook

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

class SphereMoon(private val context: Context, private val R: Float) {
    lateinit var mVertexBuffer: FloatBuffer
    lateinit var mNormalBuffer: FloatBuffer
    lateinit var textureBuffer: FloatBuffer
    var textureId: Int = 0
    private var n = 0
    //хранит идентификатор OpenGL-программы
    private var program: Int = 0

    //ссылки для атрибутов и униформ шейдеров
    private var positionHandle = 0
    private var normalHandle = 0
    private var texCoordHandle = 0
    private var textureHandle = 0
    private var mvpMatrixHandle = 0
    private var viewMatrixHandle = 0
    private var lightPosHandle = 0

    init {
        initBuffers()
        initShaders()
    }

    private fun initBuffers() {
        val dtheta = 15
        val dphi = 15
        val DTOR = Math.PI / 180.0

        val vertexByteBuf = ByteBuffer.allocateDirect(5000 * 3 * 4).apply {
            order(ByteOrder.nativeOrder())
        }
        mVertexBuffer = vertexByteBuf.asFloatBuffer()

        val normalByteBuf = ByteBuffer.allocateDirect(5000 * 3 * 4).apply {
            order(ByteOrder.nativeOrder())
        }
        mNormalBuffer = normalByteBuf.asFloatBuffer()

        val textureByteBuf = ByteBuffer.allocateDirect(5000 * 2 * 4).apply {
            order(ByteOrder.nativeOrder())
        }
        textureBuffer = textureByteBuf.asFloatBuffer()

        for (theta in -90 until 90 step dtheta) {
            for (phi in 0 until 360 step dphi) {
                val x1 = (cos(theta * DTOR) * cos(phi * DTOR)).toFloat() * R
                val y1 = (cos(theta * DTOR) * sin(phi * DTOR)).toFloat() * R
                val z1 = sin(theta * DTOR).toFloat() * R

                val x2 = (cos((theta + dtheta) * DTOR) * cos(phi * DTOR)).toFloat() * R
                val y2 = (cos((theta + dtheta) * DTOR) * sin(phi * DTOR)).toFloat() * R
                val z2 = sin((theta + dtheta) * DTOR).toFloat() * R

                val x3 = (cos((theta + dtheta) * DTOR) * cos((phi + dphi) * DTOR)).toFloat() * R
                val y3 = (cos((theta + dtheta) * DTOR) * sin((phi + dphi) * DTOR)).toFloat() * R
                val z3 = sin((theta + dtheta) * DTOR).toFloat() * R

                val x4 = (cos(theta * DTOR) * cos((phi + dphi) * DTOR)).toFloat() * R
                val y4 = (cos(theta * DTOR) * sin((phi + dphi) * DTOR)).toFloat() * R
                val z4 = sin(theta * DTOR).toFloat() * R

                mVertexBuffer.put(x1).put(y1).put(z1)
                mVertexBuffer.put(x2).put(y2).put(z2)
                mVertexBuffer.put(x3).put(y3).put(z3)
                mVertexBuffer.put(x4).put(y4).put(z4)

                mNormalBuffer.put(x1 / R).put(y1 / R).put(z1 / R)
                mNormalBuffer.put(x2 / R).put(y2 / R).put(z2 / R)
                mNormalBuffer.put(x3 / R).put(y3 / R).put(z3 / R)
                mNormalBuffer.put(x4 / R).put(y4 / R).put(z4 / R)

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
        mNormalBuffer.position(0)
        textureBuffer.position(0)
    }

    private fun initShaders() {
        val vertexShaderCode = """
            attribute vec4 aPosition;
            attribute vec2 aTexCoord;
            attribute vec3 aNormal;
            uniform mat4 uMVPMatrix;
            uniform mat4 uViewMatrix;
            uniform vec3 uLightPos;
            varying vec2 vTexCoord;
            varying float vLightIntensity;

            void main() {
                // Вычисляем положение вершины на экране.
                gl_Position = uMVPMatrix * aPosition;
                
                // Передаем текстурные координаты во фрагментный шейдер.
                vTexCoord = aTexCoord;

                // Нормализуем нормаль после преобразования из пространства модели в пространство камеры 
                vec3 normal = normalize(mat3(uViewMatrix) * aNormal);

                // Вычисляем направление к источнику света
                vec3 lightDir = normalize(uLightPos - vec3(gl_Position));

                // Вычисляем интенсивность света с помощью диффузной компоненты модели Фонга
                vLightIntensity = max(dot(normal, lightDir), 0.0);
            }
        """

        val fragmentShaderCode = """
            precision mediump float;
            varying vec2 vTexCoord;
            varying float vLightIntensity;
            uniform sampler2D uTexture;

            void main() {
                // Извлекаем цвет из текстуры по координатам текстуры.
                vec4 texColor = texture2D(uTexture, vTexCoord);
                
                // Применяем интенсивность света к цвету текстуры.
                gl_FragColor = vec4(texColor.rgb * vLightIntensity, texColor.a);
            }
        """

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader) //прикрепляет шейдеры (вершинный и фрагментный) к программе.
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)//связывает шейдеры в единую программу, проверяет совместимость и готовит её для использования.
            //Теперь program содержит ссылку на программу шейдеров
        }
        //находим индексы (дескрипторы) атрибутов и униформ.
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        normalHandle = GLES20.glGetAttribLocation(program, "aNormal")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        viewMatrixHandle = GLES20.glGetUniformLocation(program, "uViewMatrix")
        lightPosHandle = GLES20.glGetUniformLocation(program, "uLightPos")
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader // Возвращаем идентификатор шейдера
    }

    fun loadTexture(resourceId: Int) {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        textureId = textures[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
    }

    fun draw(mvpMatrix: FloatArray, viewMatrix: FloatArray, lightPosition: FloatArray) {
        GLES20.glUseProgram(program)

        //передаются значения в униформы шейдеров
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(viewMatrixHandle, 1, false, viewMatrix, 0)
        GLES20.glUniform3fv(lightPosHandle, 1, lightPosition, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0) //активирует слот текстуры 0.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0) // передаёт в шейдер, что текстура привязана к слоту 0.

        //передаем данные о позициях вершин
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, mNormalBuffer)
        GLES20.glEnableVertexAttribArray(normalHandle)

        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glEnableVertexAttribArray(texCoordHandle)

        for (i in 0 until n step 4) {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, i, n)
        }

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }
}
