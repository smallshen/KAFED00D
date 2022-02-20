package me.coley.cafedude.classfile.attribute

/**
 * Constant value attribute
 *
 * @property nameIndex          Name index in constant pool.
 * @property constantValueIndex Index in the constant pool representing the value of this attribute.
 */
class ConstantValueAttribute(
    nameIndex: Int,
    val constantValueIndex: Int,
) : Attribute(nameIndex) {

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.add(constantValueIndex)
        return set
    }

    override fun computeInternalLength(): Int {
        return 2
    }
}