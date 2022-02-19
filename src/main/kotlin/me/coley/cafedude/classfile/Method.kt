package me.coley.cafedude.classfile

import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.io.AttributeContext

/**
 * Method class member.
 *
 * @author Matt Coley
 */
class Method
/**
 * @param attributes Attributes of the method.
 * @param access     Method access flags.
 * @param nameIndex  Index of name UTF in pool.
 * @param typeIndex  Index of descriptor UTF in pool.
 */
    (attributes: List<Attribute>, access: Int, nameIndex: Int, typeIndex: Int) :
    ClassMember(attributes, access, nameIndex, typeIndex) {
    override val holderType: AttributeContext
        get() = AttributeContext.METHOD
}