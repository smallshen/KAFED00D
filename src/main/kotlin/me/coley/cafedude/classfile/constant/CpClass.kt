package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Class pool entry. Points to an UTF constant.
 *
 * @author Matt Coley
 */
class CpClass
/**
 * @param index Index of class name UTF in pool.
 */(
    /**
     * @param index New index of class name UTF in pool.
     */
    var index: Int,
) : ConstPoolEntry(Constants.ConstantPool.CLASS)