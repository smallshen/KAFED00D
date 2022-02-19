package me.coley.cafedude.classfile

import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.io.AttributeContext

/**
 * @param attributes Attributes of the method.
 * @param access     Method access flags.
 * @param nameIndex  Index of name UTF in pool.
 * @param typeIndex  Index of descriptor UTF in pool.
 */
class Method(
    attributes: List<Attribute>,
    access: Int,
    nameIndex: Int,
    typeIndex: Int,
) : ClassMember(attributes, access, nameIndex, typeIndex) {
    override val holderType: AttributeContext = AttributeContext.METHOD
}