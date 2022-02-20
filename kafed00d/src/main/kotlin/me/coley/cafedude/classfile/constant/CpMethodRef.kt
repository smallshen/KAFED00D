package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Method reference pool entry. Points to a reference's [defining class][CpClass] in pool
 * and the reference's [name and descriptor][CpNameType] in pool.
 *
 * @property classIndex    Index of method's [defining class][CpClass] in pool.
 * @property nameTypeIndex Index of method's [name and descriptor][CpNameType] in pool.
 */
class CpMethodRef(
    classIndex: Int,
    nameTypeIndex: Int
) : ConstRef(Constants.ConstantPool.METHOD_REF, classIndex, nameTypeIndex)