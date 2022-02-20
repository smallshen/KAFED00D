package me.coley.cafedude.classfile.behavior

import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.io.AttributeContext

/**
 * @property attributes All attributes applied to the current object.
 * @property holderType The type of the holder.
 */
interface AttributeHolder {

    val attributes: List<Attribute>

    val holderType: AttributeContext
}