package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Field reference pool entry. Points to a reference's [defining class][CpClass] in pool
 * and the reference's [name and descriptor][CpNameType] in pool.
 *
 * @author Matt Coley
 */
class CpFieldRef
/**
 * @param classIndex    Index of field's [defining class][CpClass] in pool.
 * @param nameTypeIndex Index of field's [name and descriptor][CpNameType] in pool.
 */
    (classIndex: Int, nameTypeIndex: Int) : ConstRef(Constants.ConstantPool.FIELD_REF, classIndex, nameTypeIndex)