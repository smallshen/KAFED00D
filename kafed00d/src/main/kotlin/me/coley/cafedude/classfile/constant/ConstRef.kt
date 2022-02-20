package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Base reference pool entry. Points to a reference's [defining class][CpClass] in pool
 * and the reference's [name and descriptor][CpNameType] in pool.
 *
 * @param type          Reference type.
 * Must be [Constants.ConstantPool.FIELD_REF], [Constants.ConstantPool.METHOD_REF],
 * or [Constants.ConstantPool.INTERFACE_METHOD_REF].
 * @param classIndex    Index of reference [defining class][CpClass] in pool.
 * @param nameTypeIndex Index of field/method [name and descriptor][CpNameType] in pool.
 */
abstract class ConstRef(type: Int, val classIndex: Int, val nameTypeIndex: Int) : ConstPoolEntry(type)