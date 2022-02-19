package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Variable table attribute.
 *
 * @author Matt Coley
 */
class LocalVariableTableAttribute
/**
 * @param nameIndex Name index in constant pool.
 * @param entries   Variable table entries.
 */(
    nameIndex: Int,
    /**
     * @param entries New table entries.
     */
    var entries: List<VarEntry>,
) : Attribute(nameIndex) {
    /**
     * @return Table entries.
     */

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        for (entry in entries) set.addAll(entry.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        // u2: line_number_table_length
        // entry[
        //   u2 start_pc;
        //   u2 length;
        //   u2 name_index;
        //   u2 descriptor_index;
        //   u2 index;
        // ]
        return 2 + 10 * entries.size
    }

    /**
     * Variable table entry.
     */
    class VarEntry
    /**
     * @param startPc   Bytecode offset var starts at.
     * @param length    Bytecode length var spans across.
     * @param nameIndex CP UTF8 name index.
     * @param descIndex CP UTF8 desc index.
     * @param index     Variable index.
     */(
        /**
         * @return Bytecode offset var starts at.
         */
        val startPc: Int,
        /**
         * @return Bytecode length var spans across.
         */
        val length: Int,
        /**
         * @return CP UTF8 name index.
         */
        val nameIndex: Int,
        /**
         * @return CP UTF8 desc index.
         */
        val descIndex: Int,
        /**
         * @return Variable index.
         */
        val index: Int,
    ) : CpAccessor {

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(nameIndex)
            set.add(descIndex)
            return set
        }
    }
}