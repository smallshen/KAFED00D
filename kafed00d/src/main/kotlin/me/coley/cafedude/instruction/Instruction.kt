package me.coley.cafedude.instruction

/**
 * @property opcode Instruction opcode.
 */
sealed class Instruction(open val opcode: Int) {

    /**
     * Size of instruction bytes
     */
    abstract val length: Int

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is Instruction) return false
//        return opcode == other.opcode
//    }
//
//    override fun hashCode(): Int {
//        return opcode
//    }
}