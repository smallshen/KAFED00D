package me.coley.cafedude.classfile.constant

import me.coley.cafedude.Constants

/**
 * Invoke-dynamic value pool entry. Points to a [NameType][CpNameType] constant
 * and a bootstrap method index in the class's bootstrap-methods attribute.
 *
 * @author Matt Coley
 */
data class CpInvokeDynamic(val bsmIndex: Int, val nameTypeIndex: Int) : ConstPoolEntry(Constants.ConstantPool.INVOKE_DYNAMIC)