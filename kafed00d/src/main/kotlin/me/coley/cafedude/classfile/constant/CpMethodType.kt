package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Method type pool entry. Points to an UTF constant.
 *
 * @param index Index of method descriptor UTF in pool.
 */
data class CpMethodType(val index: Int) : ConstPoolEntry(Constants.ConstantPool.METHOD_TYPE)