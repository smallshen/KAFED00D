package me.coley.cafedude.io

import me.coley.cafedude.Constants
import me.coley.cafedude.classfile.ClassFile
import me.coley.cafedude.classfile.ConstPool
import me.coley.cafedude.classfile.Field
import me.coley.cafedude.classfile.Method
import me.coley.cafedude.classfile.attribute.Attribute

/**
 * Builder for a [ClassFile].
 *
 * @author Matt Coley
 */
class ClassBuilder {
    /**
     * @return Class's constant pool.
     */
    val pool = ConstPool()
    private val attributes: MutableList<Attribute> = mutableListOf()
    private val interfaces: MutableList<Int> = mutableListOf()
    private val fields: MutableList<Field> = mutableListOf()
    private val methods: MutableList<Method> = mutableListOf()
    /**
     * @return Major version.
     */
    /**
     * @param versionMajor Major version.
     */
    var versionMajor = 0
    /**
     * @return Minor version.
     */
    /**
     * @param versionMinor Minor version.
     */
    var versionMinor = 0
    /**
     * @return Access flags.
     */
    /**
     * @param access Access flags.
     */
    var access = 0
    var classIndex = 0
    var superIndex = 0

    /**
     * @return `true` when the version pattern indicates a pre-java Oak class.
     */
    val isOakVersion: Boolean
        get() = versionMajor == Constants.JAVA1 && versionMinor <= 2 || versionMajor < Constants.JAVA1

    /**
     * @return `true` when the access flags indicate the class is an annotation.
     */
    val isAnnotation: Boolean
        get() = access and Constants.ACC_ANNOTATION != 0


    /**
     * @param interfaceIndex CP index of an interface type for the class.
     */
    fun addInterface(interfaceIndex: Int) {
        interfaces.add(interfaceIndex)
    }

    /**
     * @param field Field to add.
     */
    fun addField(field: Field) {
        fields.add(field)
    }

    /**
     * @param method Method to add.
     */
    fun addMethod(method: Method) {
        methods.add(method)
    }

    /**
     * @param attribute Attribute to add.
     */
    fun addAttribute(attribute: Attribute) {
        attributes.add(attribute)
    }

    /**
     * @return Build it!
     */
    fun build(): ClassFile {
        return ClassFile(
            versionMinor, versionMajor,
            pool,
            access,
            classIndex, superIndex,
            interfaces,
            fields,
            methods,
            attributes
        )
    }
}