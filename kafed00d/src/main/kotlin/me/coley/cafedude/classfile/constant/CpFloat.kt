package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Float pool entry.
 *
 * @param value Constant value.
 */
data class CpFloat(val value: Float) : ConstPoolEntry(Constants.ConstantPool.FLOAT)