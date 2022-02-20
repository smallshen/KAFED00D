package me.coley.cafedude.classfile.attribute

/**
 * Source file attribute.
 *
 *
 * @property nameIndex           Name index in constant pool.
 * @property sourceFileNameIndex UTF8 index in constant pool of the source file name.
 */
class SourceFileAttribute(nameIndex: Int, val sourceFileNameIndex: Int) : Attribute(nameIndex) {
    /**
     * @return UTF8 index in constant pool of the source file name.
     */

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.add(sourceFileNameIndex)
        return set
    }

    override fun computeInternalLength(): Int {
        // U2: sourceFileNameIndex
        return 2
    }
}