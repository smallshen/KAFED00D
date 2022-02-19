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
 * Class file format.
 *
 * @author Matt Coley
 */
class ClassFile
/**
 * @param versionMinor     Class minor version.
 * @param versionMajor     Class major version.
 * @param pool             Pool entries.
 * @param access           Class access flags.
 * @param classIndex       Index in pool for the current class.
 * @param superIndex       Index in pool for the super class.
 * @param interfaceIndices Indices in pool for interfaces.
 * @param fields           Fields.
 * @param methods          Methods.
 * @param attributes       Attributes.
 */(
    /**
     * @param versionMinor Class minor version.
     */
    var versionMinor: Int,
    /**
     * @param versionMajor Class major version.
     */
    var versionMajor: Int,
    /**
     * @return Pool entries.
     */
    val pool: ConstPool,
    /**
     * @param access Class access flags.
     */
    var access: Int,
    /**
     * @param classIndex Index in pool for the current class.
     */
    var classIndex: Int,
    /**
     * @param superIndex Index in pool for the super class.
     */
    var superIndex: Int,
    /**
     * @param interfaceIndices New indices in pool for interfaces.
     */
    var interfaceIndices: List<Int>,
    /**
     * @param fields New list of fields.
     */
    var fields: List<Field>,
    /**
     * @param methods New list of methods.
     */
    var methods: List<Method>,
    override var attributes: List<Attribute>,
) : AttributeHolder, CpAccessor {
    /**
     * @return Indices in pool for interfaces.
     */
    /**
     * @return Fields.
     */
    /**
     * @return Methods.
     */
    /**
     * @return Class access flags.
     */
    /**
     * @return Class minor version.
     */
    /**
     * @return Class major version.
     */
    /**
     * @return Index in pool for the current class.
     */
    /**
     * @return Index in pool for the super class.
     */

    /**
     * @return Class name.
     */
    val name: String
        get() = getClassName(classIndex)

    /**
     * @return Parent class name.
     */
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

    /**
     * @param index CP index, which is indexed starting at `1`.
     * @param entry New constant pool value at index.
     */
    fun setCp(index: Int, entry: ConstPoolEntry?) {
        pool[index] = entry
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