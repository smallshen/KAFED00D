package me.coley.cafedude.instruction

/**
 * @property default    Default branch offset.
 * @property low     Minimum value.
 * @property high    Maximum value.
 * @property offsets Branch offsets.
 */
data class TableSwitchInstruction(
    val default: Int,
    val low: Int,
    val high: Int,
    val offsets: List<Int>,
) : Instruction(Opcodes.TABLESWITCH) {
    /**
     * 4(Opcode + padding) + 4(default) + 4(low) + 4(high) + (offsets.size * 4(index))
     */
    override val length: Int = 16 + (offsets.size * 4)
}