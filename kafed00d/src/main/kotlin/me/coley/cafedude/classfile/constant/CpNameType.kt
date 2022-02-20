package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * NameType pool entry. Points to two UTF constants.
 *
 * @property nameIndex Index of name UTF string in pool.
 * @property typeIndex Index of descriptor UTF string in pool.
 */
data class CpNameType(val nameIndex: Int, val typeIndex: Int) : ConstPoolEntry(Constants.ConstantPool.NAME_TYPE)