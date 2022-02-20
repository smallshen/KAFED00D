package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Create UTF8 attribute.
 *
 * @param text Constant text.
 */
data class CpUtf8(val text: String) : ConstPoolEntry(Constants.ConstantPool.UTF8)