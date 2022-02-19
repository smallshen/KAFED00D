package me.coley.cafedude.instruction

/**
 * Lookup Switch instruction.
 *
 * @author xDark
 */
data class LookupSwitchInstruction
/**
 * @param dflt    Default branch offset.
 * @param keys    Lookup keys in a sorted order.
 * @param offsets Branch offsets.
 */(
    /**
     * Sets default branch offset.
     *
     * @param dflt New offset.
     */
    var default: Int,
    /**
     * Sets lookup keys.
     *
     * @param keys New keys.
     */
    var keys: List<Int>,
    /**
     * Sets branch offsets.
     *
     * @param offsets New offsets.
     */
    var offsets: List<Int>,
) : Instruction(Opcodes.LOOKUPSWITCH) {
    /**
     * @return default branch offset.
     */
    /**
     * @return lookup keys.
     */
    /**
     * @return branch offsets.
     */

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