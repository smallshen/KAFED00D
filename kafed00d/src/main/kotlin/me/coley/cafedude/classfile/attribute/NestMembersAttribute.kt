package me.coley.cafedude.classfile.attribute

/**
 * Nest members attribute, lists classes allowed to declare membership of the nest hosted by current class.
 *
 * @author Matt Coley
 */
class NestMembersAttribute
/**
 * @param nameIndex          Name index in constant pool.
 * @param memberClassIndices Class indices in constant pool of class that are allowed to declare
 * nest membership of the nest hosted by the current class.
 */(
    nameIndex: Int,
    /**
     * @param memberClassIndices New class indices in constant pool of class that are allowed to declare
     * nest membership of the nest hosted by the current class.
     */
    var memberClassIndices: List<Int>,
) : Attribute(nameIndex) {
    /**
     * @return Class indices in constant pool of class that are allowed to declare
     * nest membership of the nest hosted by the current class.
     */

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