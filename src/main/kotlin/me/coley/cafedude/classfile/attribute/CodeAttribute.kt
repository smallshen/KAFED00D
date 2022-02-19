package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.AttributeHolder
import me.coley.cafedude.classfile.behavior.CpAccessor
import me.coley.cafedude.io.AttributeContext

/**
 * Method code attribute.
 *
 * @author Matt Coley
 */
class CodeAttribute
/**
 * @param nameIndex      Name index in constant pool.
 * @param maxStack       Maximum number of values on the stack in the method.
 * @param maxLocals      Maximum number of local variables used in the method.
 * @param code           Instruction code data.
 * @param exceptionTable Exception table entries.
 * @param attributes     List of other attributes.
 */(
    nameIndex: Int,
    /**
     * @param maxStack New maximum number of values on the stack in the method.
     */
    var maxStack: Int,
    /**
     * @param maxLocals New maximum number of local variables used in the method.
     */
    var maxLocals: Int,
    /**
     * @param code New instruction code data.
     */
    var code: ByteArray,
    /**
     * @param exceptionTable New exception table entries.
     */
    var exceptionTable: List<ExceptionTableEntry>,
    private var attributes: List<Attribute>,
) : Attribute(nameIndex), AttributeHolder {
    /**
     * @return Exception table entries.
     */
    /**
     * @return Instruction code data.
     */
    /**
     * @return Maximum number of values on the stack in the method.
     */
    /**
     * @return Maximum number of local variables used in the method.
     */

    override fun getAttributes(): List<Attribute> {
        return attributes
    }

    override fun setAttributes(attributes: List<Attribute>) {
        this.attributes = attributes
    }

    override fun getHolderType(): AttributeContext {
        return AttributeContext.ATTRIBUTE
    }

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        for (attribute in getAttributes()) set.addAll(attribute.cpAccesses())
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
     * Exception table entry representation.
     *
     * @author Matt Coley
     */
    class ExceptionTableEntry
    /**
     * @param startPc        Instruction offset for start of try-catch range.
     * @param endPc          Instruction offset for end of try-catch range.
     * @param handlerPc      Instruction offset for start of catch handler range.
     * @param catchTypeIndex Index in constant pool of class type to catch.
     */(
        /**
         * @param startPc New instruction offset for start of try-catch range.
         */
        var startPc: Int,
        /**
         * @param endPc New instruction offset for end of try-catch range.
         */
        var endPc: Int,
        /**
         * @param handlerPc New instruction offset for start of catch handler range.
         */
        var handlerPc: Int,
        /**
         * @param catchTypeIndex Index in constant pool of class type to catch.
         */
        var catchTypeIndex: Int,
    ) : CpAccessor {
        /**
         * @return Instruction offset for start of try-catch range.
         */
        /**
         * @return Instruction offset for end of try-catch range.
         */
        /**
         * @return Instruction offset for start of catch handler range.
         */
        /**
         * @return Index in constant pool of class type to catch.
         */

        override fun cpAccesses(): MutableSet<Int> {
            return mutableSetOf(catchTypeIndex)
        }
    }
}