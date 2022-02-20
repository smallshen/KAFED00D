@file:Suppress("NOTHING_TO_INLINE")

package me.coley.cafedude.io

import me.coley.cafedude.classfile.attribute.CodeAttribute
import me.coley.cafedude.instruction.*
import me.coley.cafedude.instruction.Opcodes.*
import java.nio.ByteBuffer

/**
 * Reads code attribute into meaningful
 * instructions.
 *
 * @author xDark
 */
object InstructionReader {
    /**
     * @param attribute Code attribute.
     * @return a list of instructions.
     */
    fun read(attribute: CodeAttribute): List<Instruction> {
        val instructions: MutableList<Instruction> = mutableListOf()
        val buffer = ByteBuffer.wrap(attribute.code)
        while (buffer.hasRemaining()) {
            instructions.add(
                when (val opcode: Int = buffer.get().toInt() and 0xff) {

                    ATHROW -> BasicInstruction(ATHROW)
                    ARRAYLENGTH -> BasicInstruction(ARRAYLENGTH)

                    IALOAD, LALOAD, FALOAD,
                    DALOAD, AALOAD, BALOAD, CALOAD,
                    SALOAD -> BasicInstruction(opcode)

                    ISTORE_0, ISTORE_1, ISTORE_2,
                    ISTORE_3, LSTORE_0, LSTORE_1,
                    LSTORE_2, LSTORE_3, FSTORE_0,
                    FSTORE_1, FSTORE_2, FSTORE_3,
                    DSTORE_0, DSTORE_1, DSTORE_2,
                    DSTORE_3, ASTORE_0, ASTORE_1,
                    ASTORE_2, ASTORE_3 -> BasicInstruction(opcode)

                    MONITORENTER, MONITOREXIT -> BasicInstruction(opcode)

                    LCMP, FCMPL, FCMPG, DCMPL, DCMPG -> BasicInstruction(opcode)
                    ILOAD_0, ILOAD_1, ILOAD_2, ILOAD_3,
                    LLOAD_0, LLOAD_1, LLOAD_2, LLOAD_3,
                    FLOAD_0, FLOAD_1, FLOAD_2, FLOAD_3,
                    DLOAD_0, DLOAD_1, DLOAD_2, DLOAD_3,
                    ALOAD_0, ALOAD_1, ALOAD_2, ALOAD_3 -> BasicInstruction(opcode)


                    NOP, ACONST_NULL, ICONST_M1, ICONST_0,
                    ICONST_1, ICONST_2, ICONST_3, ICONST_4,
                    ICONST_5, LCONST_0, LCONST_1, FCONST_0,
                    FCONST_1, FCONST_2, DCONST_0, DCONST_1 -> BasicInstruction(opcode)


                    IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL,
                    LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM,
                    FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR,
                    LSHR, IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR -> BasicInstruction(opcode)

                    IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN -> BasicInstruction(opcode)
                    POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP -> BasicInstruction(opcode)
                    IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE -> BasicInstruction(opcode)
                    I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S -> BasicInstruction(opcode)


                    GOTO_W, JSR_W -> IntOperandInstruction(opcode, buffer.int)

                    JSR -> IntOperandInstruction(JSR, buffer.short.toInt())
                    GOTO -> IntOperandInstruction(GOTO, buffer.short.toInt())
                    SIPUSH -> IntOperandInstruction(SIPUSH, buffer.short.toInt())
                    IFNULL, IFNONNULL -> IntOperandInstruction(opcode, buffer.short.toInt())
                    IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT,
                    IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
                    IF_ACMPEQ, IF_ACMPNE -> IntOperandInstruction(opcode, buffer.short.toInt())


                    BIPUSH -> IntOperandInstruction(BIPUSH, buffer.get().toInt())

                    RET -> IntOperandInstruction(RET, buffer.get() and 0xff)
                    LDC -> IntOperandInstruction(LDC, buffer.get() and 0xff)
                    NEWARRAY -> IntOperandInstruction(NEWARRAY, buffer.get() and 0xff)


                    NEW -> IntOperandInstruction(NEW, buffer.short and 0xff)
                    ANEWARRAY -> IntOperandInstruction(ANEWARRAY, buffer.short and 0xff)
                    LDC_W, LDC2_W -> IntOperandInstruction(opcode, buffer.short and 0xff)

                    INVOKEDYNAMIC -> {
                        val index: Int = buffer.short and 0xff
                        check((buffer.get() or buffer.get()) == 0) { "InvokeDynamic padding bytes are non-zero" }
                        IntOperandInstruction(INVOKEDYNAMIC, index)
                    }

                    CHECKCAST, INSTANCEOF -> IntOperandInstruction(opcode, buffer.short and 0xff)

                    INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE -> IntOperandInstruction(
                        opcode,
                        buffer.short and 0xff
                    )
                    GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD -> IntOperandInstruction(opcode, buffer.short and 0xff)

                    IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE -> IntOperandInstruction(opcode, buffer.short.toInt())
                    ISTORE, LSTORE, FSTORE, DSTORE, ASTORE -> IntOperandInstruction(opcode, buffer.get().toInt())
                    ILOAD, LLOAD, FLOAD, DLOAD, ALOAD -> IntOperandInstruction(opcode, buffer.get() and 0xff)


                    IINC -> BiIntOperandInstruction(IINC, buffer.get() and 0xff, buffer.get().toInt())
                    MULTIANEWARRAY -> BiIntOperandInstruction(
                        MULTIANEWARRAY,
                        buffer.short and 0xff,
                        buffer.get() and 0xff
                    )

                    WIDE -> when (val type: Int = buffer.get() and 0xff) {
                        ILOAD, FLOAD, ALOAD, LLOAD, DLOAD, ISTORE, FSTORE, DSTORE, RET -> WideIntInstruction(
                            type,
                            buffer.short and 0xff
                        )
                        IINC -> WideBiIntInstruction(IINC, buffer.short and 0xff, buffer.short.toInt())
                        else -> throw IllegalStateException("Illegal wide instruction type: $type")
                    }

                    TABLESWITCH -> {
                        val pos = buffer.position()
                        // Skip padding.
                        // the tableswitch and lookupswitch 32-bit offsets will be 4-byte aligned.
                        buffer.position(pos + 3)
                        val dflt = buffer.int
                        val low = buffer.int
                        val high = buffer.int
                        val count = high - low + 1
                        val offsets: MutableList<Int> = ArrayList(count)
                        var i = 0
                        while (i < count) {
                            offsets.add(buffer.int)
                            i++
                        }
                        TableSwitchInstruction(dflt, low, high, offsets)
                    }
                    LOOKUPSWITCH -> {
                        val pos = buffer.position()
                        // Skip padding.
                        // the tableswitch and lookupswitch 32-bit offsets will be 4-byte aligned.
                        buffer.position(pos + 3)
                        val dflt = buffer.int
                        val keyCount = buffer.int
                        val keys: MutableList<Int> = ArrayList(keyCount)
                        val offsets: MutableList<Int> = ArrayList(keyCount)
                        var i = 0
                        while (i < keyCount) {
                            keys.add(buffer.int)
                            offsets.add(buffer.int)
                            i++
                        }
                        LookupSwitchInstruction(dflt, keys, offsets)
                    }
                    else -> throw IllegalStateException("Unknown instruction: $opcode")
                }

            )
        }
        return instructions
    }
}

private inline infix fun Byte.and(i: Int) = toInt() and i
private inline infix fun Short.and(i: Int) = toInt() and i
private inline infix fun Byte.or(i: Byte) = toInt() or i.toInt()