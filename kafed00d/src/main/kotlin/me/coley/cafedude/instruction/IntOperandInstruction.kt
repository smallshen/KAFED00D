package me.coley.cafedude.instruction

import me.coley.cafedude.instruction.Opcodes.*

/**
 * @param opcode  Instruction opcode.
 * @property operand Instruction operand.
 */
class IntOperandInstruction(opcode: Int, var operand: Int) : Instruction(opcode) {

    /**
     * Opcode byte + operand byte
     */
    override val size: Int
        get() = 1 + when (opcode) {
            SIPUSH, LDC_W, LDC2_W, IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE,
            GOTO, JSR,
            in IF_ICMPEQ..IF_ACMPNE -> 2

            else -> 1
        }


    override fun toString(): String {
        return "insn($opcode: $operand)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IntOperandInstruction

        if (operand != other.operand) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + operand
        result = 31 * result + size
        return result
    }
}