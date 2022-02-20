package me.coley.cafedude.classfile.attribute

/**
 * Line numbers attribute.
 *
 * @property nameIndex Name index in constant pool.
 * @property entries   Line number table entries.
 */
class LineNumberTableAttribute(nameIndex: Int, val entries: List<LineEntry>) : Attribute(nameIndex) {

    override fun computeInternalLength(): Int {
        // u2: line_number_table_length
        // u2 * 2 * X
        return 2 + 4 * entries.size
    }

    /**
     * Line number table entry.
     *
     * @property startPc Start offset in bytecode.
     * @property line    The line number at offset.
     */
    class LineEntry(val startPc: Int, val line: Int)
}