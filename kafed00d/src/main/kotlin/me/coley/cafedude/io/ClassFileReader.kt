package me.coley.cafedude.io

import me.coley.cafedude.Constants
import me.coley.cafedude.InvalidClassException
import me.coley.cafedude.classfile.ClassFile
import me.coley.cafedude.classfile.Field
import me.coley.cafedude.classfile.Method
import me.coley.cafedude.classfile.attribute.Attribute
import me.coley.cafedude.classfile.constant.*
import org.slf4j.LoggerFactory
import java.io.IOException

/**
 * Class file format parser.
 *
 * @author Matt Coley
 * @see ClassFile Parsed class representation.
 *
 * @see ClassFileWriter Class file format writer.
 */
class ClassFileReader {
    lateinit var byteStream: IndexableByteStream

    // config
    var dropForwardVersioned = true
    var dropEofAttributes = true
    var dropDupeAnnotations = true

    /**
     * @param code Class bytecode to read.
     * @return Parsed class file.
     * @throws InvalidClassException When some class reading exception occurs.
     */
    @Throws(InvalidClassException::class)
    fun read(code: ByteArray): ClassFile {
        val builder = ClassBuilder()
        try {
            try {
                IndexableByteStream(code).use { stream ->
                    this.byteStream = stream
                    // Read magic header
                    if (stream.readInt() != -0x35014542) throw InvalidClassException("Does not start with 0xCAFEBABE")
                    // Version
                    builder.versionMinor = stream.readUnsignedShort()
                    builder.versionMajor = stream.readUnsignedShort()
                    // Constant pool
                    val numConstants = stream.readUnsignedShort()
                    run {
                        var i = 1
                        while (i < numConstants) {
                            val entry = readPoolEntry()
                            builder.pool.add(entry)
                            if (entry.isWide) {
                                i++
                            }
                            i++
                        }
                    }
                    // Flags
                    builder.access = stream.readUnsignedShort()
                    // This/super classes
                    builder.classIndex = stream.readUnsignedShort()
                    builder.superIndex = stream.readUnsignedShort()
                    // Interfaces
                    val numInterfaces = stream.readUnsignedShort()
                    for (i in 0 until numInterfaces) builder.addInterface(stream.readUnsignedShort())
                    // Fields
                    val numFields = stream.readUnsignedShort()
                    for (i in 0 until numFields) builder.addField(readField(builder))
                    // Methods
                    val numMethods = stream.readUnsignedShort()
                    for (i in 0 until numMethods) builder.addMethod(readMethod(builder))
                    // Attributes
                    val numAttributes = stream.readUnsignedShort()
                    for (i in 0 until numAttributes) {
                        val attr = AttributeReader(this, builder, stream).readAttribute(AttributeContext.CLASS)
                        if (attr != null) builder.addAttribute(attr)
                    }
                    return builder.build()
                }
            } catch (ex: IOException) {
                logger.debug("IO error reading class", ex)
                throw InvalidClassException(ex)
            }
        } catch (t: Throwable) {
            logger.debug("Error reading class", t)
            throw InvalidClassException(t)
        }
    }

    /**
     * @return Constant pool entry.
     * @throws IOException           When the stream is unexpectedly closed or ends.
     * @throws InvalidClassException An unknown attribute is present.
     */
    @Throws(IOException::class, InvalidClassException::class)
    private fun readPoolEntry(): ConstPoolEntry {
        return when (val tag = byteStream.readUnsignedByte()) {
            Constants.ConstantPool.UTF8 -> CpUtf8(byteStream.readUTF())
            Constants.ConstantPool.INTEGER -> CpInt(byteStream.readInt())
            Constants.ConstantPool.FLOAT -> CpFloat(byteStream.readFloat())
            Constants.ConstantPool.LONG -> CpLong(byteStream.readLong())
            Constants.ConstantPool.DOUBLE -> CpDouble(byteStream.readDouble())
            Constants.ConstantPool.STRING -> CpString(byteStream.readUnsignedShort())
            Constants.ConstantPool.CLASS -> CpClass(byteStream.readUnsignedShort())
            Constants.ConstantPool.FIELD_REF -> CpFieldRef(byteStream.readUnsignedShort(),
                byteStream.readUnsignedShort())
            Constants.ConstantPool.METHOD_REF -> CpMethodRef(byteStream.readUnsignedShort(),
                byteStream.readUnsignedShort())
            Constants.ConstantPool.INTERFACE_METHOD_REF -> CpInterfaceMethodRef(byteStream.readUnsignedShort(),
                byteStream.readUnsignedShort())
            Constants.ConstantPool.NAME_TYPE -> CpNameType(byteStream.readUnsignedShort(),
                byteStream.readUnsignedShort())
            Constants.ConstantPool.DYNAMIC -> CpDynamic(byteStream.readUnsignedShort(),
                byteStream.readUnsignedShort())
            Constants.ConstantPool.METHOD_HANDLE -> CpMethodHandle(byteStream.readByte(),
                byteStream.readUnsignedShort())
            Constants.ConstantPool.METHOD_TYPE -> CpMethodType(byteStream.readUnsignedShort())
            Constants.ConstantPool.INVOKE_DYNAMIC -> CpInvokeDynamic(byteStream.readUnsignedShort(),
                byteStream.readUnsignedShort())
            Constants.ConstantPool.MODULE -> CpModule(byteStream.readUnsignedShort())
            Constants.ConstantPool.PACKAGE -> CpPackage(byteStream.readUnsignedShort())
            else -> throw InvalidClassException("Unknown constant-pool tag: $tag")
        }
    }

    /**
     * @param builder Class being built/read.
     * @return Field member.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readField(builder: ClassBuilder): Field {
        val access = byteStream.readUnsignedShort()
        val nameIndex = byteStream.readUnsignedShort()
        val typeIndex = byteStream.readUnsignedShort()
        val numAttributes = byteStream.readUnsignedShort()
        val attributes: MutableList<Attribute> = ArrayList()
        for (i in 0 until numAttributes) {
            val attr = AttributeReader(this, builder, byteStream).readAttribute(AttributeContext.FIELD)
            if (attr != null) attributes.add(attr)
        }
        return Field(attributes, access, nameIndex, typeIndex)
    }

    /**
     * @param builder Class being built/read.
     * @return Method member.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readMethod(builder: ClassBuilder): Method {
        val access = byteStream.readUnsignedShort()
        val nameIndex = byteStream.readUnsignedShort()
        val typeIndex = byteStream.readUnsignedShort()
        val numAttributes = byteStream.readUnsignedShort()
        val attributes: MutableList<Attribute> = ArrayList()
        for (i in 0 until numAttributes) {
            val attr = AttributeReader(this, builder, byteStream).readAttribute(AttributeContext.METHOD)
            if (attr != null) attributes.add(attr)
        }
        return Method(attributes, access, nameIndex, typeIndex)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ClassFileReader::class.java)
    }
}