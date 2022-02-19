package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Base attribute.
 *
 * @author Matt Coley
 */
abstract class Attribute
/**
 * @param nameIndex Name index in constant pool.
 */(
    /**
     * @return Name index in constant pool.
     */
    val nameIndex: Int,
) : CpAccessor {

    /**
     * @return Computed size for the internal length value of this attribute for serialization.
     */
    abstract fun computeInternalLength(): Int

    /**
     * Complete length is the [U2:name_index][.getNameIndex]
     * plus the [U4:attribute_length][.computeInternalLength]
     * plus the [internal length][.computeInternalLength]
     *
     * @return Computed size for the complete attribute.
     */
    fun computeCompleteLength(): Int {
        // u2: Name index
        // u4: Attribute length
        // ??: Internal length
        return 6 + computeInternalLength()
    }

    override fun cpAccesses(): MutableSet<Int> {
        val set: MutableSet<Int> = TreeSet()
        set.add(nameIndex)
        return set
    }
}