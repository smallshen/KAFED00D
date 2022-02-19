package me.coley.cafedude.instruction

/**
 * @param dflt    Default branch offset.
 * @param keys    Lookup keys in a sorted order.
 * @param offsets Branch offsets.
 */
data class LookupSwitchInstruction(
    var default: Int,
    var keys: List<Int>,
    var offsets: List<Int>,
) : Instruction(Opcodes.LOOKUPSWITCH) {
    /**
     * 4(Opcode + padding) + 4(count) + 4(default) + (offsets.size * (4(key) + 4(offset)))
     */
    override val size: Int = 12 + (offsets.size * 8)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LookupSwitchInstruction) return false
        if (!super.equals(other)) return false
        if (default != other.default) return false
        return if (keys != other.keys) false else offsets == other.offsets
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + default
        result = 31 * result + keys.hashCode()
        result = 31 * result + offsets.hashCode()
        return result
    }
}