package me.coley.cafedude.instruction

import me.coley.cafedude.instruction.Opcodes.MULTIANEWARRAY

/**
 * Instruction with two int operands.
 *
 * @author xDark
 */
data class BiIntOperandInstruction(
    override val opcode: Int,
    val firstOperand: Int,
    val secondOperand: Int,
) : Instruction(opcode) {

    /**
     * 1(Opcode: byte) + 1(First operand: byte) + 1(Second operand: byte)
     */
    override val length: Int
        get() = 1 + when (opcode) {
            MULTIANEWARRAY -> 3
            else -> 2
        }


    override fun toString(): String {
        return "insn($opcode: $firstOperand, $secondOperand)"
    }
}