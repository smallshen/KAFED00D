package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Method type pool entry. Points to an UTF constant.
 *
 * @author Matt Coley
 */
class CpMethodType
/**
 * @param index Index of method descriptor UTF in pool.
 */(
    /**
     * @param index New index of method descriptor UTF in pool.
     */
    var index: Int,
) : ConstPoolEntry(Constants.ConstantPool.METHOD_TYPE)