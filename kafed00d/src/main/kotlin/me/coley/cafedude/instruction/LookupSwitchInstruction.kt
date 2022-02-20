package me.coley.cafedude.instruction

/**
 * @property default    Default branch offset.
 * @property keys    Lookup keys in a sorted order.
 * @property offsets Branch offsets.
 */
data class LookupSwitchInstruction(
    val default: Int,
    val keys: List<Int>,
    val offsets: List<Int>,
) : Instruction(Opcodes.LOOKUPSWITCH) {
    /**
     * 4(Opcode + padding) + 4(count) + 4(default) + (offsets.size * (4(key) + 4(offset)))
     */
    override val length: Int = 12 + (offsets.size * 8)


}