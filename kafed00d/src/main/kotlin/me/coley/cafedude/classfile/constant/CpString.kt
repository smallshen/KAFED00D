package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * String pool entry. Points to a UTF constant.
 *
 * @property index Index of UTF string in pool.
 */
data class CpString(val index: Int) : ConstPoolEntry(Constants.ConstantPool.STRING)