package me.coley.cafedude.classfile.attribute

/**
 * Source debug extension attribute. The contained data has no internal value to the JVM.
 *
 * @property nameIndex      Name index in constant pool.
 * @property debugExtension Extension data stored in attribute.
 */
class SourceDebugExtensionAttribute(nameIndex: Int, val debugExtension: ByteArray) : Attribute(nameIndex) {

    override fun computeInternalLength(): Int {
        return debugExtension.size
    }
}