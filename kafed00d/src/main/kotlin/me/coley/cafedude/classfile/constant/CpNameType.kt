package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * NameType pool entry. Points to two UTF constants.
 *
 * @author Matt Coley
 */
class CpNameType
/**
 * @param nameIndex Index of name UTF string in pool.
 * @param typeIndex Index of descriptor UTF string in pool.
 */(
    /**
     * @param nameIndex New index of name UTF string in pool.
     */
    var nameIndex: Int,
    /**
     * @param typeIndex New index of descriptor UTF string in pool.
     */
    var typeIndex: Int,
) : ConstPoolEntry(Constants.ConstantPool.NAME_TYPE)