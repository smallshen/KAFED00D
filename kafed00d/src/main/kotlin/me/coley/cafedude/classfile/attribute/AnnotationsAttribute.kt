package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.annotation.Annotation

/**
 * Annotation collection attribute. Represents either:
 *
 *  * `RuntimeInvisibleAnnotations`>
 *  * `RuntimeVisibleAnnotations`>
 *
 *
 * @author Matt Coley
 */
class AnnotationsAttribute
/**
 * @param nameIndex
 * Name index in constant pool.
 * @param annotations
 * List of annotations.
 */(
    nameIndex: Int,
    /**
     * @param annotations
     * List of annotations.
     */
    var annotations: List<Annotation>,
) : Attribute(nameIndex) {
    /**
     * @return List of annotations.
     */

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        for (annotation in annotations) set.addAll(annotation.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        // u2 num_annotations + annotations
        return 2 + annotations.stream().mapToInt { obj: Annotation -> obj.computeLength() }.sum()
    }
}