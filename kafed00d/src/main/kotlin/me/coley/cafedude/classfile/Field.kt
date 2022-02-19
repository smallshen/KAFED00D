package me.coley.cafedude.classfile

import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.io.AttributeContext

/**
 * Field class member.
 *
 * @author Matt Coley
 */
class Field
/**
 * @param attributes Attributes of the field.
 * @param access     Field access flags.
 * @param nameIndex  Index of name UTF in pool.
 * @param typeIndex  Index of descriptor UTF in pool.
 */
    (attributes: List<Attribute>, access: Int, nameIndex: Int, typeIndex: Int) :
    ClassMember(attributes, access, nameIndex, typeIndex) {
    override val holderType: AttributeContext
        get() = AttributeContext.FIELD
}