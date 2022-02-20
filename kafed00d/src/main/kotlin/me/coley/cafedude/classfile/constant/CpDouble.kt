package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Double pool entry.
 *
 * @property value Constant value.
 */
data class CpDouble(val value: Double) : ConstPoolEntry(Constants.ConstantPool.DOUBLE) {

    override val isWide: Boolean = true
}