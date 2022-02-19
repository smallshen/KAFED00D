package me.coley.cafedude.classfile.annotation

import java.util.*

/**
 * Nested annotation element value.
 *
 * @author Matt Coley
 */
class AnnotationElementValue(tag: Char, annotation: Annotation) : ElementValue(tag) {
    /**
     * @return Nested annotation declaration.
     */
    /**
     * @param annotation Nested annotation declaration.
     */
    var annotation: Annotation

    /**
     * @param tag        ASCII tag representation, must be `c`.
     * @param annotation Nested annotation declaration.
     */
    init {
        require(tag == '@') { "Annotation element value must have '@' tag" }
        this.annotation = annotation
    }


    override fun cpAccesses(): MutableSet<Int> {
        val set: MutableSet<Int> = TreeSet()
        set.addAll(annotation.cpAccesses())
        return set
    }

    override fun computeLength(): Int {
        // u1: tag
        // ??: annotation
        return 1 + annotation.computeLength()
    }
}