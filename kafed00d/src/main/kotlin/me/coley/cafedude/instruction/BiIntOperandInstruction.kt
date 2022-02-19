package me.coley.cafedude.instruction

import me.coley.cafedude.instruction.Opcodes.MULTIANEWARRAY

/**
 * Instruction with two int operands.
 *
 * @author xDark
 */
class BiIntOperandInstruction(
    opcode: Int,
    var firstOperand: Int,
    var secondOperand: Int,
) : Instruction(opcode) {

    /**
     * 1(Opcode: byte) + 1(First operand: byte) + 1(Second operand: byte)
     */
    override val size: Int
        get() = 1 + when (opcode) {
            MULTIANEWARRAY -> 3
            else -> 2
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BiIntOperandInstruction) return false
        if (!super.equals(other)) return false
        return if (firstOperand != other.firstOperand) false else secondOperand == other.secondOperand
    }


    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + firstOperand
        result = 31 * result + secondOperand
        result = 31 * result + size
        return result
    }

    override fun toString(): String {
        return "insn($opcode: $firstOperand, $secondOperand)"
    }
}