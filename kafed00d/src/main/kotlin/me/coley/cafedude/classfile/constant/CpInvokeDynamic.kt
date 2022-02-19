package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Invoke-dynamic value pool entry. Points to a [NameType][CpNameType] constant
 * and a bootstrap method index in the class's bootstrap-methods attribute.
 *
 * @author Matt Coley
 */
class CpInvokeDynamic(
    /**
     * @param bsmIndex New index in the class's bootstrap method attribute-table.
     */
    var bsmIndex: Int,
    /**
     * @param nameTypeIndex New index of [CpNameType] in pool.
     */
    var nameTypeIndex: Int,
) : ConstPoolEntry(Constants.ConstantPool.INVOKE_DYNAMIC) {
    /**
     * @return Index in the class's bootstrap method attribute-table.
     */
    /**
     * @return Index of [CpNameType] in pool.
     */

}