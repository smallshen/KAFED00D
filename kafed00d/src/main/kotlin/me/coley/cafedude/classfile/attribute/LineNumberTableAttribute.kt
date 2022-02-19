package me.coley.cafedude.classfile.attribute

/**
 * Line numbers attribute.
 *
 * @author Matt Coley
 */
class LineNumberTableAttribute
/**
 * @param nameIndex Name index in constant pool.
 * @param entries   Line number table entries.
 */(
    nameIndex: Int,
    /**
     * @param entries New table entries.
     */
    var entries: List<LineEntry>,
) : Attribute(nameIndex) {
    /**
     * @return Table entries.
     */

    override fun computeInternalLength(): Int {
        // u2: line_number_table_length
        // u2 * 2 * X
        return 2 + 4 * entries.size
    }

    /**
     * Line number table entry.
     */
    class LineEntry
    /**
     * @param startPc Start offset in bytecode.
     * @param line    Line number at offset.
     */(
        /**
         * @return Start offset in bytecode.
         */
        val startPc: Int,
        /**
         * @return Line number at offset.
         */
        val line: Int,
    )
}