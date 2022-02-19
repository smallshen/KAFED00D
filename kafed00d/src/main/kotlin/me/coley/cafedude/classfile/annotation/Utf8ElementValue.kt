package me.coley.cafedude.classfile.annotation

import java.util.*

/**
 * UTF8 string element value.
 *
 * @author Matt Coley
 */
class Utf8ElementValue(tag: Char, utfIndex: Int) : ElementValue(tag) {
    /**
     * [utfIndex] Index of utf8 constant.
     */
    var utfIndex: Int


    init {
        require(tag == 's') { "UTF8 element value must have 's' tag" }
        this.utfIndex = utfIndex
    }

    override fun cpAccesses(): MutableSet<Int> {
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