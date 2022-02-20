package me.coley.cafedude.instruction

/**
 * Instruction that does not have any operands.
 *
 * @author xDark
 */
data class BasicInstruction(override val opcode: Int) : Instruction(opcode) {

    /**
     * Single byte, the opcode
     */
    override val length: Int = 1

}