package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * String pool entry. Points to an UTF constant.
 *
 * @author Matt Coley
 */
class CpString
/**
 * @param index Index of UTF string in pool.
 */(
    /**
     * @param index New index of UTF string in pool.
     */
    var index: Int,
) : ConstPoolEntry(Constants.ConstantPool.STRING)