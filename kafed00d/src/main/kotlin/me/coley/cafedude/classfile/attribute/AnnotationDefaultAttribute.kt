package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.annotation.ElementValue

/**
 * Represents the default value of a annotation field *(Which are technically methods, but I digress)*.
 *
 * @property nameIndex    Name index in constant pool.
 * @property elementValue Value of the annotation type element represented by the `method_info` structure
 * enclosing this attribute.
 */
class AnnotationDefaultAttribute(nameIndex: Int, val elementValue: ElementValue) : Attribute(nameIndex) {

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.addAll(elementValue.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        return elementValue.computeLength()
    }
}