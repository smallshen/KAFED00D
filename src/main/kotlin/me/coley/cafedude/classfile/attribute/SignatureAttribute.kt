package me.coley.cafedude.classfile.attribute

/**
 * Signature attribute, for generic types.
 *
 * @author Matt Coley
 */
class SignatureAttribute
/**
 * @param nameIndex      Name index in constant pool.
 * @param signatureIndex UTF8 index in constant pool of the signature.
 */(
    nameIndex: Int,
    /**
     * @param signatureIndex UTF8 index in constant pool of the signature.
     */
    var signatureIndex: Int,
) : Attribute(nameIndex) {
    /**
     * @return UTF8 index in constant pool of the signature.
     */

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.add(signatureIndex)
        return set
    }

    override fun computeInternalLength(): Int {
        // U2: signatureIndex
        return 2
    }
}