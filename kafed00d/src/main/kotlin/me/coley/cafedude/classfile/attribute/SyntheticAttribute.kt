package me.coley.cafedude.classfile.attribute

/**
 * Synthetic marker attribute.
 *
 *
 * @property nameIndex Name index in constant pool.
 */
class SyntheticAttribute(nameIndex: Int) : Attribute(nameIndex) {
    override fun computeInternalLength(): Int {
        return 0
    }
}