package me.coley.cafedude.instruction

/**
 * Instruction that does not have any operands.
 *
 * @author xDark
 */
open class BasicInstruction(opcode: Int) : Instruction(opcode) {

    /**
     * Single byte, the opcode
     */
    override val size: Int = 1


    override fun toString(): String {
        return "insn($opcode)"
    }
}