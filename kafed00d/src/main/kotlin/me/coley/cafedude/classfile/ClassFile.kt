package me.coley.cafedude.classfile

import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.classfile.behavior.AttributeHolder
import me.coley.cafedude.classfile.behavior.CpAccessor
import me.coley.cafedude.classfile.constant.ConstPoolEntry
import me.coley.cafedude.classfile.constant.CpClass
import me.coley.cafedude.classfile.constant.CpUtf8
import me.coley.cafedude.io.AttributeContext
import java.util.*

/**
 * @property versionMinor     Class minor version.
 * @property versionMajor     Class major version.
 * @property pool             Pool entries.
 * @property access           Class access flags.
 * @property classIndex       Index in pool for the current class.
 * @property superIndex       Index in pool for the super class.
 * @property interfaceIndices Indices in pool for interfaces.
 * @property fields           Fields.
 * @property methods          Methods.
 * @property attributes       Attributes.
 * @property name             Class name
 * @property superName        Super class name
 */
class ClassFile(
    val versionMinor: Int,
    val versionMajor: Int,
    val pool: ConstPool,
    val access: Int,
    val classIndex: Int,
    val superIndex: Int,
    val interfaceIndices: List<Int>,
    val fields: List<Field>,
    val methods: List<Method>,
    override val attributes: List<Attribute>,
) : AttributeHolder, CpAccessor {
    val name: String
        get() = getClassName(classIndex)


    val superName: String
        get() = getClassName(superIndex)

    /**
     * @param classIndex CP index pointing to a class.
     * @return Name of class.
     */
    private fun getClassName(classIndex: Int): String {
        val cpClass = getCp(classIndex) as CpClass
        val cpClassName = getCp(cpClass.index) as CpUtf8
        return cpClassName.text
    }

    /**
     * @param index CP index, which is indexed starting at `1`.
     * @return Constant pool value at index.
     */
    fun getCp(index: Int): ConstPoolEntry {
        return pool[index]
    }


    override val holderType: AttributeContext
        get() = AttributeContext.CLASS

    override fun cpAccesses(): MutableSet<Int> {
        val set: MutableSet<Int> = TreeSet()
        set.add(classIndex)
        set.add(superIndex)
        set.addAll(interfaceIndices)
        for (attribute in attributes) set.addAll(attribute.cpAccesses())
        for (field in fields) set.addAll(field.cpAccesses())
        for (method in methods) set.addAll(method.cpAccesses())
        return set
    }
}