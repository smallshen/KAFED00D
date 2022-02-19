package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Double pool entry.
 *
 * @author Matt Coley
 */
class CpDouble
/**
 * @param value Constant value.
 */(
    /**
     * @param value New constant value.
     */
    var value: Double,
) : ConstPoolEntry(Constants.ConstantPool.DOUBLE) {

    override val isWide: Boolean
        get() = true
}