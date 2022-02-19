package me.coley.cafedude.classfile.attribute

/**
 * An attribute implementation that is used as a default for any unhandled attribute type.
 *
 * @author Matt Coley
 */
class DefaultAttribute
/**
 * @param nameIndex Name index in constant pool.
 * @param data      Literal data stored in attribute.
 */(
    nameIndex: Int,
    /**
     * @param data New literal data stored in attribute.
     */
    var data: ByteArray,
) : Attribute(nameIndex) {
    /**
     * @return Literal data stored in attribute.
     */

    override fun computeInternalLength(): Int {
        return data.size
    }
}