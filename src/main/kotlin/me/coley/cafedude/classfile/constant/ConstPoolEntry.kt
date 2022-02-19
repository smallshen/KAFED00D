package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Base constant pool entry.
 *
 * @author Matt Coley
 */
abstract class ConstPoolEntry
/**
 * Create base attribute.
 *
 * @param tag Constant's tag.
 */(
    /**
     * @return Constant's tag.
     */
    val tag: Int,
) : Constants.ConstantPool {

    /**
     * @return `true` if constant uses two pool entries.
     */
    open val isWide: Boolean
        get() = false
}