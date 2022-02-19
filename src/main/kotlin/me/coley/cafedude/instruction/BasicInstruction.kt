package me.coley.cafedude.instruction

/**
 * Instruction that does not have any operands.
 *
 * @author xDark
 */
open class BasicInstruction(opcode: Int) : Instruction(opcode) {
    override fun toString(): String {
        return "insn($opcode)"
    }
}