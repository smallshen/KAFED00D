package me.coley.cafedude.classfile.behavior

/**
 * Applied to a data type that requires access to the constant pool.
 *
 * @author Matt Coley
 */
interface CpAccessor {
    /**
     * @return Indices accessed.
     */
    fun cpAccesses(): MutableSet<Int>
}