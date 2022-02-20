package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Long pool entry.
 *
 * @author Matt Coley
 */
data class CpLong(val value: Long) : ConstPoolEntry(Constants.ConstantPool.LONG) {
    override val isWide: Boolean = true
}