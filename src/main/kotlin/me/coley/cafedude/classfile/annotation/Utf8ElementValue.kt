package me.coley.cafedude.classfile.annotation

import java.util.*

/**
 * UTF8 string element value.
 *
 * @author Matt Coley
 */
class Utf8ElementValue(tag: Char, utfIndex: Int) : ElementValue(tag) {
    /**
     * @return Index of utf8 constant.
     */

    /**
     * @param utfIndex Index of utf8 constant.
     */
    var utfIndex: Int


    /**
     * @param tag      ASCII tag representation, must be `s`.
     * @param utfIndex Index of utf8 constant.
     */
    init {
        require(tag == 's') { "UTF8 element value must have 's' tag" }
        this.utfIndex = utfIndex
    }

    override fun cpAccesses(): Set<Int> {
        val set: MutableSet<Int> = TreeSet()
        set.add(utfIndex)
        return set
    }

    override fun computeLength(): Int {
        // u1: tag
        // u2: utf8_index
        return 3
    }
}