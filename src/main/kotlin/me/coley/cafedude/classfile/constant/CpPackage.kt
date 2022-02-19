package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Package pool entry. Points to an UTF constant.
 *
 * @author Matt Coley
 */
class CpPackage
/**
 * @param index Index of package name UTF in pool.
 */(
    /**
     * @param index New index of package name UTF in pool.
     */
    var index: Int,
) : ConstPoolEntry(Constants.ConstantPool.PACKAGE)