package me.coley.cafedude.classfile

import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.io.AttributeContext

/**
 * @param attributes Attributes of the field.
 * @param access     Field access flags.
 * @property nameIndex  Index of name UTF in pool.
 * @param typeIndex  Index of descriptor UTF in pool.
 */
class Field(
    attributes: List<Attribute>,
    access: Int,
    nameIndex: Int,
    typeIndex: Int
) : ClassMember(attributes, access, nameIndex, typeIndex) {
    override val holderType: AttributeContext = AttributeContext.FIELD
}