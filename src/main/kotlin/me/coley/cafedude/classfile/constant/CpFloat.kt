package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Float pool entry.
 *
 * @author Matt Coley
 */
class CpFloat
/**
 * @param value Constant value.
 */(
    /**
     * @param value New constant value.
     */
    var value: Float,
) : ConstPoolEntry(Constants.ConstantPool.FLOAT)