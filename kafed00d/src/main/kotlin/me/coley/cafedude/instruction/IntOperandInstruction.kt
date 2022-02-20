package me.coley.cafedude.instruction

import me.coley.cafedude.instruction.Opcodes.*

/**
 * @property opcode  Instruction opcode.
 * @property operand Instruction operand.
 */
data class IntOperandInstruction(override val opcode: Int, val operand: Int) : Instruction(opcode) {

    /**
     * Opcode byte + operand byte
     */
    override val length: Int
        get() = 1 + when (opcode) {

            GOTO_W, JSR_W -> 4


            JSR, GOTO, SIPUSH, IFNULL, IFNONNULL,
            IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT,
            IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
            IF_ACMPEQ, IF_ACMPNE,
            NEW, ANEWARRAY,
            LDC_W, LDC2_W,
            INVOKEDYNAMIC,
            CHECKCAST, INSTANCEOF,
            INVOKEVIRTUAL, INVOKESPECIAL,
            INVOKESTATIC, INVOKEINTERFACE,
            GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD,
            IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE -> 2

            else -> 1
        }


    override fun toString(): String {
        return "insn($opcode: $operand)"
    }
}