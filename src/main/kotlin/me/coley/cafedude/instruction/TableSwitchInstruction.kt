package me.coley.cafedude.instruction

/**
 * Table Switch instruction.
 *
 * @author xDark
 */
data class TableSwitchInstruction
/**
 * @param dflt    Default branch offset.
 * @param low     Minimmum value.
 * @param high    Maximum value.
 * @param offsets Branch offsets.
 */(
    /**
     * Sets default branch offset.
     *
     * @param dflt New offset.
     */
    var default: Int,
    /**
     * Sets minimum value.
     *
     * @param low New value.
     */
    var low: Int,
    /**
     * Sets maximum value.
     *
     * @param high New value.
     */
    var high: Int,
    /**
     * Sets branch offsets.
     *
     * @param offsets New offsets.
     */
    var offsets: List<Int>,
) : BasicInstruction(Opcodes.TABLESWITCH) {
    /**
     * @return default branch offset.
     */
    /**
     * @return minimum value.
     */
    /**
     * @return maximum value.
     */
    /**
     * @return branch offsets.
     */


}