package com.example.presentation.fragment.openGl

import android.opengl.Matrix

class Sprite2d(private val mDrawable: Drawable2d) {
    private val mColor: FloatArray = FloatArray(4)
    private var mTextureId: Int
    private var mAngle = 0f

    private var mPosX = 0f
    private var mPosY = 0f
    private val mModelViewMatrix: FloatArray
    private var mMatrixReady: Boolean
    private val mScratchMatrix = FloatArray(16)

    var scaleX = 0f
        private set
    var scaleY = 0f
        private set

    /**
     * Re-computes mModelViewMatrix, based on the current values for rotation, scale, and
     * translation.
     */
    private fun recomputeMatrix() {
        val modelView = mModelViewMatrix
        Matrix.setIdentityM(modelView, 0)
        Matrix.translateM(modelView, 0, mPosX, mPosY, 0.0f)
        if (mAngle != 0.0f) {
            Matrix.rotateM(modelView, 0, mAngle, 0.0f, 0.0f, 1.0f)
        }
        Matrix.scaleM(modelView, 0, scaleX, scaleY, 1.0f)
        mMatrixReady = true
    }

    /**
     * Sets the sprite scale (size).
     */
    fun setScale(scaleX: Float, scaleY: Float) {
        this.scaleX = scaleX
        this.scaleY = scaleY
        mMatrixReady = false
    }
    /**
     * Gets the sprite rotation angle, in degrees.
     */// Normalize.  We're not expecting it to be way off, so just iterate.
    /**
     * Sets the sprite rotation angle, in degrees.  Sprite will rotate counter-clockwise.
     */
    var rotation: Float = mAngle

    /**
     * Returns the position on the X axis.
     */
    fun getPositionX(): Float {
        return mPosX
    }

    /**
     * Returns the position on the Y axis.
     */
    fun getPositionY(): Float {
        return mPosY
    }

    /**
     * Sets the sprite position.
     */
    fun setPosition(posX: Float, posY: Float) {
        mPosX = posX
        mPosY = posY
        mMatrixReady = false
    }

    /**
     * Returns the model-view matrix.
     *
     * To avoid allocations, this returns internal state.  The caller must not modify it.
     */
    fun getModelViewMatrix(): FloatArray {
        if (!mMatrixReady) {
            recomputeMatrix()
        }
        return mModelViewMatrix
    }

    /**
     * Sets color to use for flat-shaded rendering.  Has no effect on textured rendering.
     */
    fun setColor(red: Float, green: Float, blue: Float) {
        mColor[0] = red
        mColor[1] = green
        mColor[2] = blue
    }

    /**
     * Sets texture to use for textured rendering.  Has no effect on flat-shaded rendering.
     */
    fun setTexture(textureId: Int) {
        mTextureId = textureId
    }

    /**
     * Returns the color.
     *
     *
     * To avoid allocations, this returns internal state.  The caller must not modify it.
     */
    fun getColor(): FloatArray {
        return mColor
    }

    /**
     * Draws the rectangle with the supplied program and projection matrix.
     */
    fun draw(program: FlatShadedProgram, projectionMatrix: FloatArray?) {
        // Compute model/view/projection matrix.
        Matrix.multiplyMM(mScratchMatrix, 0, projectionMatrix, 0, getModelViewMatrix(), 0)
        program.draw(
            mScratchMatrix, mColor, mDrawable.vertexArray, 0,
            mDrawable.vertexCount, mDrawable.coordsPerVertex,
            mDrawable.vertexStride
        )
    }

    override fun toString(): String {
        return "[Sprite2d pos=" + mPosX + "," + mPosY +
                " scale=" + scaleX + "," + scaleY + " angle=" + mAngle +
                " color={" + mColor[0] + "," + mColor[1] + "," + mColor[2] +
                "} drawable=" + mDrawable + "]"
    }

    companion object {
        private const val TAG = GlUtil.TAG
    }

    init {
        mColor[3] = 1.0f
        mTextureId = -1
        mModelViewMatrix = FloatArray(16)
        mMatrixReady = false
    }
}
