package me.coley.cafedude.classfile.attribute

/**
 * Deprecation marker attribute.
 *
 * @author Matt Coley
 */
class DeprecatedAttribute
/**
 * @param nameIndex Name index in constant pool.
 */
    (nameIndex: Int) : Attribute(nameIndex) {
    override fun computeInternalLength(): Int {
        return 0
    }
}