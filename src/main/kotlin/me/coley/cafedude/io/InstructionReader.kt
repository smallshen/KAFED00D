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
class InstructionReader {
    /**
     * @param attribute Code attribute.
     * @return a list of instructions.
     */
    fun read(attribute: CodeAttribute): List<Instruction> {
        val instructions: MutableList<Instruction> = ArrayList()
        val buffer = ByteBuffer.wrap(attribute.code)
        while (buffer.hasRemaining()) {
            instructions.add(
                when (val opcode: Int = buffer.get().toInt() and 0xff) {
                    in NOP..DCONST_1 -> BasicInstruction(opcode)
                    BIPUSH -> IntOperandInstruction(opcode, buffer.get().toInt())
                    SIPUSH -> IntOperandInstruction(opcode, buffer.short.toInt())
                    LDC -> IntOperandInstruction(LDC, buffer.get() and 0xff)
                    LDC_W, LDC2_W -> IntOperandInstruction(opcode, buffer.short and 0xff)
                    in ILOAD..ALOAD -> IntOperandInstruction(opcode, buffer.get() and 0xff)
                    in ILOAD_0..ALOAD_3 -> BasicInstruction(opcode)
                    in IALOAD..SALOAD -> BasicInstruction(opcode)
                    ISTORE, LSTORE, FSTORE, DSTORE, ASTORE -> IntOperandInstruction(opcode, buffer.get().toInt())
                    in ISTORE_0..ASTORE_3 -> BasicInstruction(opcode)
                    IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE -> BasicInstruction(opcode)
                    POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP -> BasicInstruction(opcode)
                    in IADD..LXOR -> BasicInstruction(opcode)
                    IINC -> BiIntOperandInstruction(IINC, buffer.get() and 0xff, buffer.get().toInt())
                    in I2L..I2S -> BasicInstruction(opcode)
                    LCMP, FCMPL, FCMPG, DCMPL, DCMPG -> BasicInstruction(opcode)
                    IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE -> IntOperandInstruction(opcode, buffer.short.toInt())
                    in IF_ICMPEQ..IF_ACMPNE -> IntOperandInstruction(opcode, buffer.short.toInt())
                    GOTO -> IntOperandInstruction(GOTO, buffer.short.toInt())
                    JSR -> IntOperandInstruction(JSR, buffer.short.toInt())
                    RET -> IntOperandInstruction(RET, buffer.get() and 0xff)
                    TABLESWITCH -> {
                        val pos = buffer.position()
                        // Skip padding.
                        buffer.position(pos + (4 - pos and 3))
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
                        buffer.position(pos + (4 - pos and 3))
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
                    IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN -> BasicInstruction(opcode)
                    GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD -> IntOperandInstruction(opcode, buffer.short and 0xff)
                    INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE -> IntOperandInstruction(opcode,
                        buffer.short and 0xff)
                    INVOKEDYNAMIC -> {
                        val index: Int = buffer.short and 0xff
                        check(buffer.get() or buffer.get() == 0) {
                            // TODO: should we silently ignore, or throw?
                            "InvokeDynamic padding bytes are non-zero"
                        }
                        IntOperandInstruction(INVOKEDYNAMIC, index)
                    }
                    NEW -> IntOperandInstruction(NEW, buffer.short and 0xff)
                    NEWARRAY -> IntOperandInstruction(NEWARRAY, buffer.get() and 0xff)
                    ANEWARRAY -> IntOperandInstruction(ANEWARRAY, buffer.short and 0xff)
                    ARRAYLENGTH -> BasicInstruction(ARRAYLENGTH)
                    ATHROW -> BasicInstruction(ATHROW)
                    CHECKCAST, INSTANCEOF -> IntOperandInstruction(opcode, buffer.short and 0xff)
                    MONITORENTER, MONITOREXIT -> BasicInstruction(opcode)
                    WIDE -> when (val type: Int = buffer.get() and 0xff) {
                        ILOAD, FLOAD, ALOAD, LLOAD, DLOAD, ISTORE, FSTORE, DSTORE, RET -> IntOperandInstruction(
                            opcode,
                            buffer.short and 0xff
                        )
                        IINC -> BiIntOperandInstruction(IINC, buffer.short and 0xff, buffer.short.toInt())
                        else -> throw IllegalStateException("Illegal wide instruction type: $type")
                    }
                    MULTIANEWARRAY -> BiIntOperandInstruction(
                        MULTIANEWARRAY,
                        buffer.short and 0xff,
                        buffer.get() and 0xff
                    )
                    IFNULL, IFNONNULL -> IntOperandInstruction(opcode, buffer.short.toInt())
                    GOTO_W, JSR_W -> IntOperandInstruction(opcode, buffer.int)
                    else -> throw IllegalStateException("Unknown instruction: $opcode")
                }

            )
        }
        return instructions
    }
}

inline infix fun Byte.and(i: Int) = toInt() and i
inline infix fun Short.and(i: Int) = toInt() and i
inline infix fun Byte.or(i: Byte) = toInt() or i.toInt()