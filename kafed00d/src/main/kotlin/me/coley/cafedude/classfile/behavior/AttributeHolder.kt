package me.coley.cafedude.classfile.behavior

import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.io.AttributeContext

/**
 * Applied to a data type that have attributes attached to them.
 *
 * @author Matt Coley
 */
interface AttributeHolder {
    /**
     * @return All attributes applied to the current object.
     */
    /**
     * @param attributes New list of attributes.
     */
    var attributes: List<Attribute>

    /**
     * @return The type of the holder.
     */
    val holderType: AttributeContext
}