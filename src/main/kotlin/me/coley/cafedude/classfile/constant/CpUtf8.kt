package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * UTF8 pool entry.
 *
 * @author Matt Coley
 */
class CpUtf8
/**
 * Create UTF8 attribute.
 *
 * @param text Constant text.
 */(
    /**
     * @param text New constant text.
     */
    var text: String,
) : ConstPoolEntry(Constants.ConstantPool.UTF8)