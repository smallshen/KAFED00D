package me.coley.cafedude.classfile.attribute

/**
 * Checked exceptions attribute.
 *
 * @property nameIndex           Name index in constant pool.
 * @property exceptionIndexTable Indices into the constant pool representing all checked exceptions
 * that may be thrown by this method.
 */
class ExceptionsAttribute(nameIndex: Int, val exceptionIndexTable: List<Int>) : Attribute(nameIndex) {

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.addAll(exceptionIndexTable)
        return set
    }

    override fun computeInternalLength(): Int {
        // Multiplying with two, as each index has two bytes.
        return 2 + exceptionIndexTable.size * 2
    }
}