package me.coley.cafedude.classfile.annotation


import java.util.*

/**
 * Class element value.
 *
 * @author Matt Coley
 */
class ClassElementValue(tag: Char, classIndex: Int) : ElementValue(tag) {
    /**
     * @return Index of class constant.
     */
    /**
     * @param classIndex Index of class constant.
     */
    var classIndex: Int

    /**
     * @param tag        ASCII tag representation, must be `c`.
     * @param classIndex Index of class constant.
     */
    init {
        require(tag == 'c') { "Class element value must have 'c' tag" }
        this.classIndex = classIndex
    }


    override fun cpAccesses(): Set<Int> {
        val set: MutableSet<Int> = TreeSet()
        set.add(classIndex)
        return set
    }

    override fun computeLength(): Int {
        // u1: tag
        // u2: class_index
        return 3
    }
}