package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * MethodHandle pool entry. Holds a byte to indicate behavior and points to a relevant reference constant
 * based on the byte's value.
 *
 * @property kind           Byte indicating handle behavior.
 * @property referenceIndex Index of handle's [reference][ConstRef] in pool.
 * Reference type depends on the byte value.
 */
data class CpMethodHandle(val kind: Byte, val referenceIndex: Int) :
    ConstPoolEntry(Constants.ConstantPool.METHOD_HANDLE)