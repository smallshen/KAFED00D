package me.coley.cafedude.classfile.attribute

/**
 * Nest host attribute, points to host class.
 *
 * @author Matt Coley
 */
class NestHostAttribute
/**
 * @param nameIndex
 * Name index in constant pool.
 * @param hostClassIndex
 * Class index in constant pool of class that is the nest host of the current class.
 */(
    nameIndex: Int,
    /**
     * @param hostClassIndex
     * New class index in constant pool of class that is the nest host of the current class.
     */
    var hostClassIndex: Int,
) : Attribute(nameIndex) {
    /**
     * @return Class index in constant pool of class that is the nest host of the current class.
     */

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