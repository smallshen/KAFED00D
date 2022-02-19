package me.coley.cafedude.instruction

/**
 * Node that represents bytecode instruction.
 *
 * @author xDark
 */
sealed class Instruction
/**
 * @param opcode Instruction opcode.
 */ protected constructor(
    /**
     * Sets instruction opcode.
     *
     * @param opcode New opcode.
     */
    var opcode: Int,
) {
    /**
     * @return instruction opcode.
     */

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Instruction) return false
        return opcode == other.opcode
    }

    override fun hashCode(): Int {
        return opcode
    }
}