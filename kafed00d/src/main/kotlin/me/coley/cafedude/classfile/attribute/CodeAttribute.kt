package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.AttributeHolder
import me.coley.cafedude.classfile.behavior.CpAccessor
import me.coley.cafedude.io.AttributeContext

/**
 * @property nameIndex      Name index in constant pool.
 * @property maxStack       Maximum number of values on the stack in the method.
 * @property maxLocals      Maximum number of local variables used in the method.
 * @property code           Instruction code data.
 * @property exceptionTable Exception table entries.
 * @property attributes     List of other attributes.
 */
class CodeAttribute(
    nameIndex: Int,
    var maxStack: Int,
    var maxLocals: Int,
    var code: ByteArray,
    var exceptionTable: List<ExceptionTableEntry>,
    override val attributes: List<Attribute>,
) : Attribute(nameIndex), AttributeHolder {

    override val holderType: AttributeContext = AttributeContext.ATTRIBUTE

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        for (attribute in attributes) set.addAll(attribute.cpAccesses())
        for (ex in exceptionTable) set.addAll(ex.cpAccesses())
        // TODO: Instructions
        return set
    }

    override fun computeInternalLength(): Int {
        // u2: max_stack
        // u2: max_locals
        var len = 4
        // u4: code_length
        // u1 * X: CODE
        len += 4
        len += code.size
        // u2: exception_table_length
        // u2 * 4 * X: EXCEPTIONS
        len += 2
        len += 8 * exceptionTable.size
        // u2: attributes_count
        // ??: ATTRIBS
        len += 2
        for (attribute in attributes) len += attribute.computeCompleteLength()
        return len
    }

    /**
     * @property startPc        Instruction offset for start of try-catch range.
     * @property endPc          Instruction offset for end of try-catch range.
     * @property handlerPc      Instruction offset for start of catch handler range.
     * @property catchTypeIndex Index in constant pool of class type to catch.
     */
    class ExceptionTableEntry(
        var startPc: Int,
        var endPc: Int,
        var handlerPc: Int,
        var catchTypeIndex: Int,
    ) : CpAccessor {
        override fun cpAccesses(): MutableSet<Int> {
            return mutableSetOf(catchTypeIndex)
        }
    }
}