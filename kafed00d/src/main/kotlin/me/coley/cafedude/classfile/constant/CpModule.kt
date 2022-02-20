package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Module pool entry. Points to an UTF constant.
 *
 * @param index
 * Index of module name UTF in pool.
 */
data class CpModule(val index: Int) : ConstPoolEntry(Constants.ConstantPool.MODULE)
