package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Interface method reference pool entry. Points to a reference's [defining class][CpClass] in pool
 * and the reference's [name and descriptor][CpNameType] in pool.
 *
 * @author Matt Coley
 */
class CpInterfaceMethodRef(
    classIndex: Int,
    nameTypeIndex: Int
) : ConstRef(Constants.ConstantPool.INTERFACE_METHOD_REF, classIndex, nameTypeIndex)