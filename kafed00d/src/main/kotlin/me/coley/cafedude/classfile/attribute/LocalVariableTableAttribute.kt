package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Variable table attribute.
 *
 * @property nameIndex Name index in constant pool.
 * @property entries   Variable table entries.
 */
class LocalVariableTableAttribute(nameIndex: Int, val entries: List<VarEntry>) : Attribute(nameIndex) {


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
     *
     * @property startPc   Bytecode offset var starts at.
     * @property length    Bytecode length var spans across.
     * @property nameIndex CP UTF8 name index.
     * @property descIndex CP UTF8 desc index.
     * @property index     Variable index.
     */
    data class VarEntry(
        val startPc: Int,
        val length: Int,
        val nameIndex: Int,
        val descIndex: Int,
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