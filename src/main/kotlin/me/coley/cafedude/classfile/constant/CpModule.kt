package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Module pool entry. Points to an UTF constant.
 *
 * @author Matt Coley
 */
class CpModule
/**
 * @param index
 * Index of module name UTF in pool.
 */(
    /**
     * @param index
     * New index of module name UTF in pool.
     */
    var index: Int,
) : ConstPoolEntry(Constants.ConstantPool.MODULE)
