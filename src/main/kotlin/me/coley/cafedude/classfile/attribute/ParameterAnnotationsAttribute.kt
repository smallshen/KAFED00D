package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.annotation.Annotation

/**
 * Annotation collection attribute on method parameters. Represents either:
 *
 *  * `RuntimeInvisibleParameterAnnotations`>
 *  * `RuntimeVisibleParameterAnnotations`>
 *
 *
 * @author Matt Coley
 */
class ParameterAnnotationsAttribute
/**
 * @param nameIndex            Name index in constant pool.
 * @param parameterAnnotations Map of parameter indices to their list of attributes.
 */(
    nameIndex: Int,
    /**
     * @param parameterAnnotations Map of parameter indices to their list of attributes.
     */
    var parameterAnnotations: Map<Int, List<Annotation>>,
) : Attribute(nameIndex) {
    /**
     * @return Map of parameter indices to their list of attributes.
     */

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        for (list in parameterAnnotations.values) for (annotation in list) set.addAll(annotation.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        var length = 1 // u1: num_parameters
        for (annotations in parameterAnnotations.values) {
            // u2: num_annotations + annotations
            length += 2 + annotations.stream().mapToInt { obj: Annotation -> obj.computeLength() }.sum()
        }
        return length
    }
}