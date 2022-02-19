package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Integer pool entry.
 *
 * @author Matt Coley
 */
class CpInt
/**
 * @param value Constant value.
 */(
    /**
     * @param value New constant value.
     */
    var value: Int,
) : ConstPoolEntry(Constants.ConstantPool.INTEGER)