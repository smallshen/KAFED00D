package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Dynamic value pool entry. Points to a [NameType][CpNameType] constant
 * and a bootstrap method index in the class's bootstrap-methods attribute.
 *
 * @author Matt Coley
 */
class CpDynamic
/**
 * @param bsmIndex      Index in the class's bootstrap method attribute-table.
 * @param nameTypeIndex Index of [CpNameType] in pool.
 */(
    /**
     * @param bsmIndex New index in the class's bootstrap method attribute-table.
     */
    var bsmIndex: Int,
    /**
     * @param nameTypeIndex New index of [CpNameType] in pool.
     */
    var nameTypeIndex: Int,
) : ConstPoolEntry(Constants.ConstantPool.DYNAMIC)