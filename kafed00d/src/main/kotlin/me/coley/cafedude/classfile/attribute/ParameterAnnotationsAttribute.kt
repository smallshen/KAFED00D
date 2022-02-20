package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.annotation.Annotation

/**
 * Annotation collection attribute on method parameters. Represents either:
 *
 *  * `RuntimeInvisibleParameterAnnotations`>
 *  * `RuntimeVisibleParameterAnnotations`>
 *
 *
 * @property nameIndex            Name index in constant pool.
 * @property parameterAnnotations Map of parameter indices to their list of attributes.
 */
class ParameterAnnotationsAttribute(
    nameIndex: Int,
    val parameterAnnotations: Map<Int, List<Annotation>>,
) : Attribute(nameIndex) {
    
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