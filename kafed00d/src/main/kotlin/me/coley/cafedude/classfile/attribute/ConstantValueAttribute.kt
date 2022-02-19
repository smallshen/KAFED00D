package me.coley.cafedude.classfile.attribute

/**
 * Constant value attribute
 *
 * @author JCWasmx86
 */
class ConstantValueAttribute
/**
 * @param nameIndex          Name index in constant pool.
 * @param constantValueIndex Index in the constant pool representing the value of this attribute.
 */(
    nameIndex: Int,
    /**
     * @property constantValueIndex Index in the constant pool representing the value of this attribute.
     */
    var constantValueIndex: Int,
) : Attribute(nameIndex) {
    /**
     * @return Index in the constant pool representing the value of this attribute.
     */

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.add(constantValueIndex)
        return set
    }

    override fun computeInternalLength(): Int {
        return 2
    }
}