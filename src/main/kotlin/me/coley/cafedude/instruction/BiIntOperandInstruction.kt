package me.coley.cafedude.instruction

/**
 * Instruction with two int operands.
 *
 * @author xDark
 */
class BiIntOperandInstruction(
    opcode: Int,
    /**
     * Sets the first instruction operand.
     *
     * @param firstOperand New operand.
     */
    var firstOperand: Int,
    /**
     * Sets the second instruction operand.
     *
     * @param secondOperand New operand.
     */
    var secondOperand: Int,
) : BasicInstruction(opcode) {
    /**
     * @return first instruction operand.
     */
    /**
     * @return first instruction operand.
     */

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
        return result
    }

    override fun toString(): String {
        return "insn($opcode: $firstOperand, $secondOperand)"
    }
}