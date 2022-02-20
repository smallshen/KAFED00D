package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Variable generic/type table attribute.
 *
 * @property nameIndex Name index in constant pool.
 * @property entries   Variable type table entries.
 */
class LocalVariableTypeTableAttribute
    (
    nameIndex: Int,
    /**
     * @param entries New ta+ble entries.
     */
    val entries: List<VarTypeEntry>,
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
        //   u2 signature_index;
        //   u2 index;
        // ]
        return 2 + 10 * entries.size
    }

    /**
     * Variable table entry.
     *
     * @property startPc        Bytecode offset var starts at.
     * @property length         Bytecode length var spans across.
     * @property nameIndex      CP UTF8 name index.
     * @property signatureIndex CP UTF8 signature index.
     * @property index          Variable index.
     */
    data class VarTypeEntry(
        val startPc: Int,
        val length: Int,
        val nameIndex: Int,
        val signatureIndex: Int,
        val index: Int
    ) : CpAccessor {

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(nameIndex)
            set.add(signatureIndex)
            return set
        }
    }
}