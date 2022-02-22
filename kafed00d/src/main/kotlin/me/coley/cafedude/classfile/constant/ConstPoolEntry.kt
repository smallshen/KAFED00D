package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Create base attribute.
 *
 * @property tag Constant's tag.
 */
sealed class ConstPoolEntry(val tag: Int) : Constants.ConstantPool {

    /**
     * @return `true` if constant uses two pool entries.
     */
    open val isWide: Boolean = false
}