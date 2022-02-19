package me.coley.cafedude.classfile.attribute

/**
 * Checked exceptions attribute.
 *
 * @author JCWasmx86
 */
class ExceptionsAttribute
/**
 * @param nameIndex           Name index in constant pool.
 * @param exceptionIndexTable Indices into the constant pool representing all checked exceptions
 * that may be thrown by this method.
 */(
    nameIndex: Int,
    /**
     * @param exceptionIndexTable Indices into the constant pool representing all checked exceptions
     * that may be thrown by this method.
     */
    var exceptionIndexTable: List<Int>,
) : Attribute(nameIndex) {
    /**
     * @return Exception index table.
     */

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