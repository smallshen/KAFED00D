package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Long pool entry.
 *
 * @author Matt Coley
 */
class CpLong(
    /**
     * @param value New constant value.
     */
    var value: Long,
) : ConstPoolEntry(Constants.ConstantPool.LONG) {
    /**
     * @return Constant value.
     */

    override val isWide: Boolean
        get() = true
}