package me.coley.cafedude.instruction

/**
 * Instruction with a single int operand.
 *
 * @author xDark
 */
class IntOperandInstruction
/**
 * @param opcode  Instruction opcode.
 * @param operand Instruction operand.
 */(
    opcode: Int,
    /**
     * Sets instruction operand.
     *
     * @param operand New operand.
     */
    var operand: Int,
) : Instruction(opcode) {
    /**
     * @return instruction operand.
     */

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IntOperandInstruction) return false
        if (!super.equals(other)) return false
        return operand == other.operand
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + operand
        return result
    }

    override fun toString(): String {
        return "insn($opcode: $operand)"
    }
}