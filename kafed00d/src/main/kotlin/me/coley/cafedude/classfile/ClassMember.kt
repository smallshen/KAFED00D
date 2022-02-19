package me.coley.cafedude.classfile

import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.classfile.behavior.AttributeHolder
import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Base class member.
 *
 * @author Matt Coley
 */
abstract class ClassMember
/**
 * @param attributes Attributes of the member.
 * @param access     Member access flags.
 * @param nameIndex  Index of name UTF in pool.
 * @param typeIndex  Index of descriptor UTF in pool.
 */(
    override var attributes: List<Attribute>,
    /**
     * @param access New  member access flags.
     */
    var access: Int,
    /**
     * @param nameIndex New index of name UTF in pool.
     */
    var nameIndex: Int,
    /**
     * @param typeIndex New index of descriptor UTF in pool.
     */
    var typeIndex: Int,
) : AttributeHolder, CpAccessor {
    /**
     * @return Member access flags.
     */
    /**
     * @return Index of name UTF in pool.
     */
    /**
     * @return Index of descriptor UTF in pool.
     */

    override fun cpAccesses(): MutableSet<Int> {
        val set: MutableSet<Int> = TreeSet()
        set.add(nameIndex)
        set.add(typeIndex)
        for (attribute in attributes) set.addAll(attribute.cpAccesses())
        return set
    }
}