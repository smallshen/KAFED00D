package me.coley.cafedude.classfile.annotation

import java.util.*

/**
 * Primitive value element value.
 *
 * @author Matt Coley
 */
class PrimitiveElementValue
/**
 * @param tag        ASCII tag representation, indicating the type of primitive element value.
 * @param valueIndex Index of primitive value constant.
 */(
    tag: Char,
    /**
     * @param valueIndex Index of primitive value constant.
     */
    var valueIndex: Int,
) : ElementValue(tag) {
    /**
     * @return Index of primitive value constant.
     */


    override fun cpAccesses(): MutableSet<Int> {
        val set: MutableSet<Int> = TreeSet()
        set.add(valueIndex)
        return set
    }

    override fun computeLength(): Int {
        // u1: tag
        // u2: value_index
        return 3
    }
}