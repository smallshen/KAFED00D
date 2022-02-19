package me.coley.cafedude.io

import me.coley.cafedude.instruction.*
import java.nio.ByteBuffer

class InstructionWriter {
    fun write(instructions: List<Instruction>) {
        val buffer = ByteBuffer.allocate(instructions.size)
        instructions.forEach { insn ->
            buffer.put(insn.opcode and 0xff)
            when (insn) {
                is BasicInstruction -> {}
                is BiIntOperandInstruction -> {
                    buffer.put(insn.firstOperand and 0xff)
                    buffer.put(insn.secondOperand)
                }
                is IntOperandInstruction -> {
                    buffer.putShort(insn.operand.toShort())
                }
                is LookupSwitchInstruction -> {
                    val (default, keys, offsets) = insn
                    val pos = buffer.position()

                    // TODO: check it, might be wrong
                    // the tableswitch and lookupswitch 32-bit offsets will be 4-byte aligned.
                    buffer.position(pos + 3)
                    buffer.putInt(default)
//                    buffer.putInt(keys.size)
                    buffer.putInt(keys.count())
                    keys.forEachIndexed { index, k ->
                        val offset = offsets[index]
                        buffer.putInt(k)
                        buffer.putInt(offset)
                    }
                }
                is TableSwitchInstruction -> {
                    val (default, low, high, offsets) = insn
                    val pos = buffer.position()

                    // TODO: check it, might be wrong
                    // the tableswitch and lookupswitch 32-bit offsets will be 4-byte aligned.
                    buffer.position(pos + 3)
                    buffer.putInt(default)
                    buffer.putInt(low)
                    buffer.putInt(high)
                    offsets.forEach { buffer.putInt(it) }
                }
            }

        }
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun ByteBuffer.put(int: Int) = put(int.toByte())