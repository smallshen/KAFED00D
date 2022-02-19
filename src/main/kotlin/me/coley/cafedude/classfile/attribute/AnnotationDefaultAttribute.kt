package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.annotation.ElementValue

/**
 * Represents the default value of a annotation field *(Which are technically methods, but I digress)*.
 *
 * @author Matt Coley
 */
class AnnotationDefaultAttribute
/**
 * @param nameIndex    Name index in constant pool.
 * @param elementValue Value of the annotation type element represented by the `method_info` structure
 * enclosing this attribute.
 */(
    nameIndex: Int,
    /**
     * @return Value of the annotation type element represented by the `method_info` structure
     * enclosing this attribute.
     */
    val elementValue: ElementValue,
) : Attribute(nameIndex) {

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.addAll(elementValue.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        return elementValue.computeLength()
    }
}