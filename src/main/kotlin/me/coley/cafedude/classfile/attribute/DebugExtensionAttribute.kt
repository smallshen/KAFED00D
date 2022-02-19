package me.coley.cafedude.classfile.attribute

/**
 * Source debug extension attribute. The contained data has no internal value to the JVM.
 *
 * @author Matt Coley
 */
class DebugExtensionAttribute
/**
 * @param nameIndex      Name index in constant pool.
 * @param debugExtension Extension data stored in attribute.
 */(
    nameIndex: Int,
    /**
     * @param debugExtension New extension data stored in attribute.
     */
    var debugExtension: ByteArray,
) : Attribute(nameIndex) {
    /**
     * @return Extension data stored in attribute.
     */

    override fun computeInternalLength(): Int {
        return debugExtension.size
    }
}