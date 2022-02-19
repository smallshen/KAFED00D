package me.coley.cafedude.classfile.annotation

import java.util.*

/**
 * Enum element value.
 *
 * @author Matt Coley
 */
class EnumElementValue(tag: Char, typeIndex: Int, nameIndex: Int) : ElementValue(tag) {
    /**
     * @return Index of enum type descriptor constant.
     */
    /**
     * @param typeIndex Index of enum type descriptor constant.
     */
    var typeIndex: Int
    /**
     * @return Index of enum value name constant.
     */
    /**
     * @param nameIndex Index of enum value name constant.
     */
    var nameIndex: Int

    /**
     * @param tag       ASCII tag representation, must be `e`.
     * @param typeIndex Index of enum type descriptor constant.
     * @param nameIndex Index of enum value name constant.
     */
    init {
        require(tag == 'e') { "UTF8 element value must have 'e' tag" }
        this.typeIndex = typeIndex
        this.nameIndex = nameIndex
    }


    override fun cpAccesses(): Set<Int> {
        val set: MutableSet<Int> = TreeSet()
        set.add(nameIndex)
        set.add(typeIndex)
        return set
    }

    override fun computeLength(): Int {
        // u1: tag
        // u2: enum_type_index
        // u2: enum_name_index
        return 5
    }
}