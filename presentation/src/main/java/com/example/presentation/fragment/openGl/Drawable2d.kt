package com.example.presentation.fragment.openGl

import java.lang.RuntimeException
import java.nio.FloatBuffer

class Drawable2d(shape: Prefab) {
    /**
     * Returns the array of vertices.
     *
     * To avoid allocations, this returns internal state.  The caller must not modify it.
     */
    var vertexArray: FloatBuffer? = null

    /**
     * Returns the number of vertices stored in the vertex array.
     */
    var vertexCount = 0

    /**
     * Returns the number of position coordinates per vertex.  This will be 2 or 3.
     */
    var coordsPerVertex = 0

    /**
     * Returns the width, in bytes, of the data for each vertex.
     */
    var vertexStride = 0

    /**
     * Returns the width, in bytes, of the data for each texture coordinate.
     */
    val texCoordStride: Int
    private val mPrefab: Prefab?

    /**
     * Enum values for constructor.
     */
    enum class Prefab {
        PENTAGON
    }

    override fun toString(): String {
        return if (mPrefab != null) {
            "[Drawable2d: $mPrefab]"
        } else {
            "[Drawable2d: ...]"
        }
    }

    companion object {
        private const val SIZEOF_FLOAT = 4

        /**
         * Simple equilateral triangle (1.0 per side).  Centered on (0,0).
         */
        private val PENTAGON_COORDS = floatArrayOf(
            0f, 2f,  // 0 top
            -1f, 1f,  // 1 center left
            1f, 1f, // 2 center right
            -1f, -1f, // 3 bottom left
            1f, -1f, // 4 bottom right
        )
        private val PENTAGON_BUF = GlUtil.createFloatBuffer(PENTAGON_COORDS)
    }

    /**
     * Prepares a drawable from a "pre-fabricated" shape definition.
     *
     *
     * Does no EGL/GL operations, so this can be done at any time.
     */
    init {
        when (shape) {
            Prefab.PENTAGON -> {
                vertexArray = PENTAGON_BUF
                coordsPerVertex = 2
                vertexStride = coordsPerVertex * SIZEOF_FLOAT
                vertexCount = PENTAGON_COORDS.size / coordsPerVertex
            }
            else -> throw RuntimeException("Unknown shape $shape")
        }
        texCoordStride = 2 * SIZEOF_FLOAT
        mPrefab = shape
    }
}