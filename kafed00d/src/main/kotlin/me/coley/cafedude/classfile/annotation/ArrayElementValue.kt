package me.coley.cafedude.classfile.annotation

import java.util.*

/**
 * Array element value.
 *
 * @author Matt Coley
 */
class ArrayElementValue(tag: Char, array: List<ElementValue>) : ElementValue(tag) {
    /**
     * @return Array contents.
     */
    /**
     * @param array Array contents.
     */
    var array: List<ElementValue>

    /**
     * @param tag   ASCII tag representation, must be `c`.
     * @param array Array contents.
     */
    init {
        require(tag == '[') { "Array element value must have '[' tag" }
        this.array = array
    }

    override fun cpAccesses(): MutableSet<Int> {
        val set: MutableSet<Int> = TreeSet()
        for (value in array) set.addAll(value.cpAccesses())
        return set
    }

    override fun computeLength(): Int {
        // u1: tag
        // u2: num_elements
        // ??: elements
        return 3 + array.stream().mapToInt { obj: ElementValue -> obj.computeLength() }
            .sum()
    }
}