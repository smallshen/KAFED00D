package me.coley.cafedude.io

import java.io.ByteArrayInputStream
import java.io.DataInputStream

/**
 * An implementation of [DataInputStream] that can seek backwards using [.reset]
 * based off an internal [ByteArrayInputStream].
 *
 * @author Matt Coley
 */
class IndexableByteStream(data: ByteArray) : DataInputStream(IndexableByteArrayInputStream(data)) {
    private val exposer: IndexableByteArrayInputStream

    /**
     * @param data Data to read from.
     */
    init {
        exposer = `in` as IndexableByteArrayInputStream
    }

    /**
     * @return Current input stream index.
     */
    val index: Int
        get() = exposer.index

    /**
     * Seek backwards in the stream.
     *
     * @param distance Distance to move backwards.
     */
    fun reset(distance: Int) {
        exposer.reset(distance)
    }

    /**
     * @return Backing byte stream buffer.
     */
    val buffer: ByteArray
        get() = exposer.buffer

    /**
     * Exposes position in [java.io.ByteArrayInputStream].
     *
     * @author Matt Coley
     */
    class IndexableByteArrayInputStream(data: ByteArray) : ByteArrayInputStream(data) {
        fun reset(distance: Int) {
            pos -= distance
        }

        val index: Int
            get() = pos
        val buffer: ByteArray
            get() = buf
    }
}