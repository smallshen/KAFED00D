package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Package pool entry. Points to an UTF constant.
 *
 * @param index Index of package name UTF in pool.
 */
data class CpPackage(val index: Int) : ConstPoolEntry(Constants.ConstantPool.PACKAGE)