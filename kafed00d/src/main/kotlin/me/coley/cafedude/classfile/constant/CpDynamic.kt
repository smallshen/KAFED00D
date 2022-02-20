package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Dynamic value pool entry. Points to a [NameType][CpNameType] constant
 * and a bootstrap method index in the class's bootstrap-methods attribute.
 *
 * @property bsmIndex      Index in the class's bootstrap method attribute-table.
 * @property nameTypeIndex Index of [CpNameType] in pool.
 */
data class CpDynamic(val bsmIndex: Int, val nameTypeIndex: Int) : ConstPoolEntry(Constants.ConstantPool.DYNAMIC)