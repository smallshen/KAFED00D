package me.coley.cafedude.io

import me.coley.cafedude.Constants
import me.coley.cafedude.InvalidClassException
import me.coley.cafedude.classfile.ClassFile
import me.coley.cafedude.classfile.Field
import me.coley.cafedude.classfile.Method
import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.classfile.constant.*
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

/**
 * Class file format writer.
 *
 * @author Matt Coley
 * @see ClassFile Parsed class representation.
 *
 * @see ClassFileWriter Class file format writer.
 */
class ClassFileWriter {
    lateinit var out: DataOutputStream
    lateinit var attributeWriter: AttributeWriter

    /**
     * @param clazz Parsed class file.
     * @return Bytecode of class.
     * @throws InvalidClassException When the class cannot be written.
     */
    @Throws(InvalidClassException::class)
    fun write(clazz: ClassFile): ByteArray {
        val baos = ByteArrayOutputStream()
        try {
            DataOutputStream(baos).use { out ->
                this.out = out
                attributeWriter = AttributeWriter(clazz)
                // Write magic header
                out.writeInt(-0x35014542)
                // Version
                out.writeShort(clazz.versionMinor)
                out.writeShort(clazz.versionMajor)
                // Constant pool
                out.writeShort(clazz.pool.size + 1)
                for (entry in clazz.pool) writeCpEntry(entry)
                // Flags
                out.writeShort(clazz.access)
                // This/super classes
                out.writeShort(clazz.classIndex)
                out.writeShort(clazz.superIndex)
                // Interfaces
                out.writeShort(clazz.interfaceIndices.size)
                for (interfaceIdx in clazz.interfaceIndices) out.writeShort(interfaceIdx)
                // Fields
                out.writeShort(clazz.fields.size)
                for (field in clazz.fields) writeField(field)
                // Methods
                out.writeShort(clazz.methods.size)
                for (method in clazz.methods) writeMethod(method)
                // Attributes
                out.writeShort(clazz.attributes.size)
                for (attribute in clazz.attributes) writeAttribute(attribute)
                return baos.toByteArray()
            }
        } catch (ex: IOException) {
            throw InvalidClassException(ex)
        }
    }

    /**
     * @param entry Constant pool entry to write.
     * @throws IOException           When the stream cannot be written to.
     * @throws InvalidClassException When the class has unexpected data.
     */
    @Throws(IOException::class, InvalidClassException::class)
    private fun writeCpEntry(entry: ConstPoolEntry) {
        val tag = entry.tag
        out.writeByte(tag)
        when (tag) {
            Constants.ConstantPool.UTF8 -> out.writeUTF((entry as CpUtf8).text)
            Constants.ConstantPool.INTEGER -> out.writeInt((entry as CpInt).value)
            Constants.ConstantPool.FLOAT -> out.writeFloat((entry as CpFloat).value)
            Constants.ConstantPool.LONG -> out.writeLong((entry as CpLong).value)
            Constants.ConstantPool.DOUBLE -> out.writeDouble((entry as CpDouble).value)
            Constants.ConstantPool.STRING -> out.writeShort((entry as CpString).index)
            Constants.ConstantPool.CLASS -> out.writeShort((entry as CpClass).index)
            Constants.ConstantPool.FIELD_REF, Constants.ConstantPool.METHOD_REF, Constants.ConstantPool.INTERFACE_METHOD_REF -> {
                out.writeShort((entry as ConstRef).classIndex)
                out.writeShort(entry.nameTypeIndex)
            }
            Constants.ConstantPool.NAME_TYPE -> {
                out.writeShort((entry as CpNameType).nameIndex)
                out.writeShort(entry.typeIndex)
            }
            Constants.ConstantPool.DYNAMIC -> {
                out.writeShort((entry as CpDynamic).bsmIndex)
                out.writeShort(entry.nameTypeIndex)
            }
            Constants.ConstantPool.METHOD_HANDLE -> {
                out.writeByte((entry as CpMethodHandle).kind.toInt())
                out.writeShort(entry.referenceIndex)
            }
            Constants.ConstantPool.METHOD_TYPE -> out.writeShort((entry as CpMethodType).index)
            Constants.ConstantPool.INVOKE_DYNAMIC -> {
                out.writeShort((entry as CpInvokeDynamic).bsmIndex)
                out.writeShort(entry.nameTypeIndex)
            }
            Constants.ConstantPool.MODULE -> out.writeShort((entry as CpModule).index)
            Constants.ConstantPool.PACKAGE -> out.writeShort((entry as CpPackage).index)
            else -> throw InvalidClassException("Unknown constant-pool tag: $tag")
        }
    }

    /**
     * @param attribute Attribute to write.
     * @throws IOException           When the stream cannot be written to.
     * @throws InvalidClassException When the attribute name points to a non-utf8
     * constant.
     */
    @Throws(IOException::class, InvalidClassException::class)
    private fun writeAttribute(attribute: Attribute) {
        out.write(attributeWriter.writeAttribute(attribute))
    }

    /**
     * @param field Field to write.
     * @throws IOException           When the stream cannot be written to.
     * @throws InvalidClassException When an attached attribute is invalid.
     */
    @Throws(IOException::class, InvalidClassException::class)
    private fun writeField(field: Field) {
        out.writeShort(field.access)
        out.writeShort(field.nameIndex)
        out.writeShort(field.typeIndex)
        out.writeShort(field.attributes.size)
        for (attribute in field.attributes) writeAttribute(attribute)
    }

    /**
     * @param method Method to write.
     * @throws IOException           When the stream cannot be written to.
     * @throws InvalidClassException When an attached attribute is invalid.
     */
    @Throws(IOException::class, InvalidClassException::class)
    private fun writeMethod(method: Method) {
        out.writeShort(method.access)
        out.writeShort(method.nameIndex)
        out.writeShort(method.typeIndex)
        out.writeShort(method.attributes.size)
        for (attribute in method.attributes) writeAttribute(attribute)
    }
}