package me.coley.cafedude.classfile.attribute

/**
 * Deprecation marker attribute.
 *
 * @property nameIndex Name index in constant pool.
 */
class DeprecatedAttribute(nameIndex: Int) : Attribute(nameIndex) {
    override fun computeInternalLength(): Int {
        return 0
    }
}