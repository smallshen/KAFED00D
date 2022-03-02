package me.coley.cafedude.io

import me.coley.cafedude.instruction.*
import me.coley.cafedude.instruction.Opcodes.*
import java.nio.ByteBuffer

object InstructionWriter {
    fun write(instructions: List<Instruction>): ByteArray {
        val buffer = ByteBuffer.allocate(instructions.sumOf { it.length })
        instructions.forEach { insn ->
            buffer.put(insn.opcode)
            when (insn) {
                is BasicInstruction -> {}
                is BiIntOperandInstruction -> {
                    when (insn.opcode) {
                        IINC -> {
                            buffer.put(insn.firstOperand)
                            buffer.put(insn.secondOperand)
                        }

                        MULTIANEWARRAY -> {
                            buffer.putShort((insn.firstOperand and 0xff).toShort())
                            buffer.put(insn.secondOperand)
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

                        RET, LDC, NEWARRAY -> buffer.put(insn.operand)

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


                    buffer.position(pos + (4 - pos and 3))
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


                    buffer.position(pos + (4 - pos and 3))
                    buffer.putInt(default)
                    buffer.putInt(low)
                    buffer.putInt(high)
                    offsets.forEach { buffer.putInt(it) }
                }
                is WideIntInstruction -> {
                    buffer.put(insn.type)
                    buffer.putShort(insn.operand.toShort())
                }
                is WideBiIntInstruction -> {
                    buffer.put(insn.type)
                    buffer.putShort(insn.firstOperand.toShort())
                    buffer.putShort(insn.secondOperand.toShort())
                }
            }

        }
        require(buffer.remaining() == 0) { "Buffer isn't full" }
        return buffer.array()
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun ByteBuffer.put(int: Int) = put(int.toByte())