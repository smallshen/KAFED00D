package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Integer pool entry.
 *
 * @param value Constant value.
 */
data class CpInt(val value: Int) : ConstPoolEntry(Constants.ConstantPool.INTEGER)