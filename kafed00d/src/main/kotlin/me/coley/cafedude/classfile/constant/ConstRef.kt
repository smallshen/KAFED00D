package me.coley.cafedude.classfile.constant

/**
 * Base reference pool entry. Points to a reference's [defining class][CpClass] in pool
 * and the reference's [name and descriptor][CpNameType] in pool.
 *
 * @author Matt Coley
 */
abstract class ConstRef
/**
 * @param type          Reference type.
 * Must be [Constants.ConstantPool.FIELD_REF], [Constants.ConstantPool.METHOD_REF],
 * or [Constants.ConstantPool.INTERFACE_METHOD_REF].
 * @param classIndex    Index of reference [defining class][CpClass] in pool.
 * @param nameTypeIndex Index of field/method [name and descriptor][CpNameType] in pool.
 */(
    type: Int,
    /**
     * @param classIndex New index of reference [defining class][CpClass] in pool.
     */
    var classIndex: Int,
    /**
     * @param nameTypeIndex New index of field/method [name and descriptor][CpNameType] in pool.
     */
    var nameTypeIndex: Int,
) : ConstPoolEntry(type)