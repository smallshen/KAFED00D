package me.coley.cafedude.classfile.attribute

/**
 * Nest host attribute, points to host class.
 *
 *
 * @property nameIndex
 * Name index in constant pool.
 * @property hostClassIndex
 * Class index in constant pool of class that is the nest host of the current class.
 */
class NestHostAttribute(nameIndex: Int, val hostClassIndex: Int) : Attribute(nameIndex) {

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.add(hostClassIndex)
        return set
    }

    override fun computeInternalLength(): Int {
        // U2: hostClassIndex
        return 2
    }
}