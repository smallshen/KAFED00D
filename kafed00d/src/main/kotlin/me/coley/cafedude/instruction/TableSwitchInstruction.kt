package me.coley.cafedude.instruction

/**
 * @param dflt    Default branch offset.
 * @param low     Minimmum value.
 * @param high    Maximum value.
 * @param offsets Branch offsets.
 */
data class TableSwitchInstruction(
    var default: Int,
    var low: Int,
    var high: Int,
    var offsets: List<Int>,
) : Instruction(Opcodes.TABLESWITCH) {
    /**
     * 4(Opcode + padding) + 4(default) + 4(low) + 4(high) + (offsets.size * 4(index))
     */
    override val size: Int = 16 + (offsets.size * 4)
}