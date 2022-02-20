package me.coley.cafedude.classfile.attribute

/**
 * Signature attribute, for generic types.
 *
 *
 * @property nameIndex      Name index in constant pool.
 * @property signatureIndex UTF8 index in constant pool of the signature.
 */
class SignatureAttribute(nameIndex: Int, val signatureIndex: Int) : Attribute(nameIndex) {

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