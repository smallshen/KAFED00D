package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Class pool entry. Points to an UTF constant.
 *
 * @property index Index of class name UTF in pool.
 */
data class CpClass(val index: Int) : ConstPoolEntry(Constants.ConstantPool.CLASS)