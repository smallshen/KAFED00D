package me.coley.cafedude.instruction

import me.coley.cafedude.instruction.Opcodes.WIDE

sealed class WideInstruction(val type: Int) : Instruction(WIDE)

// wide int
class WideIntInstruction(type: Int, val operand: Int) : WideInstruction(type) {
    override val size: Int = 1 + 1 + 2
}

// wide bi int
class WideBiIntInstruction(type: Int, val firstOperand: Int, val secondOperand: Int) : WideInstruction(type) {
    override val size: Int = 1 + 1 + 4
}