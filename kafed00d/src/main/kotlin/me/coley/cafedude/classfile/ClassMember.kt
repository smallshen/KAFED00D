package me.coley.cafedude.classfile

import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.classfile.behavior.AttributeHolder
import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * @property attributes Attributes of the member.
 * @property access     Member access flags.
 * @property nameIndex  Index of name UTF in pool.
 * @property typeIndex  Index of descriptor UTF in pool.
 */
abstract class ClassMember(
    override val attributes: List<Attribute>,
    val access: Int,
    val nameIndex: Int,
    val typeIndex: Int,
) : AttributeHolder, CpAccessor {
    override fun cpAccesses(): MutableSet<Int> {
        val set: MutableSet<Int> = TreeSet()
        set.add(nameIndex)
        set.add(typeIndex)
        for (attribute in attributes) set.addAll(attribute.cpAccesses())
        return set
    }
}