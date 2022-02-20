package me.coley.cafedude.classfile.attribute

/**
 * Nest members attribute, lists classes allowed declaring membership of the nest hosted by current class.
 *
 *
 * @property nameIndex          Name index in constant pool.
 * @property memberClassIndices Class indices in constant pool of class that are allowed to declare
 * nest membership of the nest hosted by the current class.
 */
class NestMembersAttribute(nameIndex: Int, val memberClassIndices: List<Int>) : Attribute(nameIndex) {

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.addAll(memberClassIndices)
        return set
    }

    override fun computeInternalLength(): Int {
        // U2: classCount
        // ??: count * 2
        return 2 + memberClassIndices.size * 2
    }
}