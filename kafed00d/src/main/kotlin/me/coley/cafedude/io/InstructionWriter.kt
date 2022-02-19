package me.coley.cafedude.io

import me.coley.cafedude.instruction.*
import me.coley.cafedude.instruction.Opcodes.*
import java.nio.ByteBuffer

object InstructionWriter {
    fun write(instructions: List<Instruction>): ByteArray {
        val buffer = ByteBuffer.allocate(instructions.sumOf { it.size })
        instructions.forEach { insn ->
            buffer.put(insn.opcode and 0xff)
            when (insn) {
                is BasicInstruction -> {}
                is BiIntOperandInstruction -> {
                    when (insn.opcode) {
                        IINC -> {
                            buffer.put(insn.firstOperand and 0xff)
                            buffer.put(insn.secondOperand)
                        }

                        MULTIANEWARRAY -> {
                            buffer.putShort((insn.firstOperand and 0xff).toShort())
                            buffer.put(insn.secondOperand and 0xff)
                        }
                    }
                }
                is IntOperandInstruction -> {
                    when (insn.opcode) {
                        GOTO_W, JSR_W -> buffer.putInt(insn.operand)
                        JSR, GOTO, SIPUSH, IFNULL, IFNONNULL,
                        IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT,
                        IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
                        IF_ACMPEQ, IF_ACMPNE -> buffer.putShort(insn.operand.toShort())

                        BIPUSH -> buffer.put(insn.operand)

                        RET, LDC, NEWARRAY -> buffer.put(insn.operand and 0xff)

                        NEW, ANEWARRAY,
                        LDC_W, LDC2_W,
                        INVOKEDYNAMIC,
                        CHECKCAST, INSTANCEOF,
                        INVOKEVIRTUAL, INVOKESPECIAL,
                        INVOKESTATIC, INVOKEINTERFACE,
                        GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD -> buffer.putShort((insn.operand and 0xff).toShort())

                        IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE -> buffer.putShort(insn.operand.toShort())

                        ISTORE, LSTORE, FSTORE, DSTORE, ASTORE -> buffer.put(insn.operand)
                    }
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
                is WideIntInstruction -> {
                    buffer.put(insn.type and 0xff)
                    buffer.putShort((insn.operand and 0xff).toShort())
                }
                is WideBiIntInstruction -> {
                    buffer.put(insn.type and 0xff)
                    buffer.putShort((insn.firstOperand and 0xff).toShort())
                    buffer.putShort(insn.secondOperand.toShort())
                }
            }

        }
        return buffer.array()
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun ByteBuffer.put(int: Int) = put(int.toByte())