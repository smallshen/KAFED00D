package me.coley.cafedude.classfile.attribute

/**
 * Synthetic marker attribute.
 *
 * @author Matt Coley
 */
class SyntheticAttribute
/**
 * @param nameIndex Name index in constant pool.
 */
    (nameIndex: Int) : Attribute(nameIndex) {
    override fun computeInternalLength(): Int {
        return 0
    }
}