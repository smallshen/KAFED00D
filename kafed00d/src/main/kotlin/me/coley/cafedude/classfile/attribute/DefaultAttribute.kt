package me.coley.cafedude.classfile.attribute

/**
 * An attribute implementation that is used as a default for any unhandled attribute type.
 *
 * @property nameIndex Name index in constant pool.
 * @property data      Literal data stored in attribute.
 */
class DefaultAttribute(nameIndex: Int, val data: ByteArray) : Attribute(nameIndex) {

    override fun computeInternalLength(): Int {
        return data.size
    }
}