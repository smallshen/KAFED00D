package me.coley.cafedude.io

import me.coley.cafedude.Constants.Attributes.*
import me.coley.cafedude.Constants.StackMapTable.*
import me.coley.cafedude.classfile.attribute.*
import me.coley.cafedude.classfile.attribute.AttributeVersions.getIntroducedVersion
import me.coley.cafedude.classfile.attribute.CodeAttribute.ExceptionTableEntry
import me.coley.cafedude.classfile.attribute.LineNumberTableAttribute.LineEntry
import me.coley.cafedude.classfile.attribute.LocalVariableTableAttribute.VarEntry
import me.coley.cafedude.classfile.attribute.LocalVariableTypeTableAttribute.VarTypeEntry
import me.coley.cafedude.classfile.attribute.StackMapTableAttribute.*
import me.coley.cafedude.classfile.constant.CpUtf8
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException

/**
 * Attribute reader for all attributes.
 * <br></br>
 * Annotations delegate to [AnnotationReader] due to complexity.
 *
 * @author Matt Coley
 */
class AttributeReader(
    private val reader: ClassFileReader,
    private val builder: ClassBuilder,
    inputStream: DataInputStream,
) {
    private val stream: IndexableByteStream

    // Attribute info
    private val expectedContentLength: Int
    private val nameIndex: Int

    /**
     * @param reader
     * Parent class reader.
     * @param builder
     * Class being build/read into.
     * @param is
     * Parent stream.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    init {
        // Extract name/lengtjh
        nameIndex = inputStream.readUnsignedShort()
        expectedContentLength = inputStream.readInt()
        // Create local stream
        val subsection = ByteArray(expectedContentLength)
        inputStream.readFully(subsection)
        this.stream = IndexableByteStream(subsection)
    }

    /**
     * @param context
     * Where the attribute is applied to.
     *
     * @return Attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    fun readAttribute(context: AttributeContext): Attribute? {
        return try {
            val attribute = read(context) ?: return null
            val read = stream.index
            if (read != expectedContentLength) {
                val name = (builder.pool[nameIndex] as CpUtf8).text
                logger.debug("Invalid '{}' on {}, claimed to be {} bytes, but was {}",
                    name, context.name, expectedContentLength, read)
                return null
            }
            attribute
        } catch (ex: Exception) {
            if (reader.dropEofAttributes) {
                if (nameIndex < builder.pool.size) {
                    val name = (builder.pool[nameIndex] as CpUtf8).text
                    logger.debug("Invalid '{}' on {}, EOF thrown when parsing attribute, expected {} bytes",
                        name, context.name, expectedContentLength)
                } else {
                    logger.debug("Invalid attribute on {}, invalid attribute name index", context.name)
                }
                null
            } else throw ex
        }
    }

    @Throws(IOException::class)
    private fun read(context: AttributeContext): Attribute? {
        val pool = builder.pool
        val name = pool.getUtf(nameIndex)
        // Check for illegally inserted attributes from future versions
        if (reader.dropForwardVersioned) {
            val introducedAt = getIntroducedVersion(name)
            if (introducedAt > builder.versionMajor) {
                logger.debug("Found '{}' on {} in class version {}, min supported is {}",
                    name, context.name, builder.versionMajor, introducedAt)
                return null
            }
        }
        when (name) {
            CODE -> return readCode()
            CONSTANT_VALUE -> return readConstantValue()
            DEPRECATED -> return DeprecatedAttribute(nameIndex)
            ENCLOSING_METHOD -> return readEnclosingMethod()
            EXCEPTIONS -> return readExceptions()
            INNER_CLASSES -> return readInnerClasses()
            NEST_HOST -> return readNestHost()
            NEST_MEMBERS -> return readNestMembers()
            SOURCE_DEBUG_EXTENSION -> return readSourceDebugExtension()
            RUNTIME_INVISIBLE_ANNOTATIONS, RUNTIME_VISIBLE_ANNOTATIONS -> return readAnnotations(
                context)
            RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS, RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS -> return readParameterAnnotations(
                context)
            RUNTIME_INVISIBLE_TYPE_ANNOTATIONS, RUNTIME_VISIBLE_TYPE_ANNOTATIONS -> return readTypeAnnotations(
                context)
            ANNOTATION_DEFAULT -> return readAnnotationDefault(context)
            SYNTHETIC -> return readSynthetic()
            BOOTSTRAP_METHODS -> return readBoostrapMethods()
            SIGNATURE -> return readSignature()
            SOURCE_FILE -> return readSourceFile()
            MODULE -> return readModule()
            STACK_MAP_TABLE -> return readStackMapTable()
            LINE_NUMBER_TABLE -> return readLineNumbers()
            LOCAL_VARIABLE_TABLE -> return readLocalVariables()
            LOCAL_VARIABLE_TYPE_TABLE -> return readLocalVariableTypess()
            PERMITTED_SUBCLASSES -> return readPermittedClasses()
            RECORD -> return readRecord()
            CHARACTER_RANGE_TABLE, COMPILATION_ID, METHOD_PARAMETERS, MODULE_HASHES, MODULE_MAIN_CLASS, MODULE_PACKAGES, MODULE_RESOLUTION, MODULE_TARGET, SOURCE_ID -> {}
            else -> {}
        }
        // No known/unhandled attribute length is less than 2.
        // So if that is given, we likely have an intentionally malformed attribute.
        if (expectedContentLength < 2) {
            logger.debug("Invalid attribute, its content length <= 1")
            stream.skipBytes(expectedContentLength)
            return null
        }
        // Default handling, skip remaining bytes
        stream.skipBytes(expectedContentLength)
        return DefaultAttribute(nameIndex, stream.buffer)
    }

    /**
     * @return Record attribute indicating the current class is a record, and details components of the record.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readRecord(): RecordAttribute {
        val components: MutableList<RecordAttribute.RecordComponent> = ArrayList()
        val count = stream.readUnsignedShort()
        for (i in 0 until count) {
            val nameIndex = stream.readUnsignedShort()
            val descIndex = stream.readUnsignedShort()
            val numAttributes = stream.readUnsignedShort()
            val attributes: MutableList<Attribute> = ArrayList()
            for (x in 0 until numAttributes) {
                val attr = AttributeReader(
                    reader,
                    builder, stream).readAttribute(AttributeContext.ATTRIBUTE)
                if (attr != null) attributes.add(attr)
            }
            components.add(RecordAttribute.RecordComponent(nameIndex, descIndex, attributes))
        }
        return RecordAttribute(nameIndex, components)
    }

    /**
     * @return Permitted classes authorized to extend/implement the current class.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readPermittedClasses(): PermittedClassesAttribute {
        val entries: MutableList<Int> = ArrayList()
        val count = stream.readUnsignedShort()
        for (i in 0 until count) {
            val index = stream.readUnsignedShort()
            entries.add(index)
        }
        return PermittedClassesAttribute(nameIndex, entries)
    }

    /**
     * @return Variable type table.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readLocalVariableTypess(): LocalVariableTypeTableAttribute {
        val entries: MutableList<VarTypeEntry> = ArrayList()
        val count = stream.readUnsignedShort()
        for (i in 0 until count) {
            val startPc = stream.readUnsignedShort()
            val length = stream.readUnsignedShort()
            val name = stream.readUnsignedShort()
            val sig = stream.readUnsignedShort()
            val index = stream.readUnsignedShort()
            entries.add(VarTypeEntry(startPc, length, name, sig, index))
        }
        return LocalVariableTypeTableAttribute(nameIndex, entries)
    }

    /**
     * @return Variable table.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readLocalVariables(): LocalVariableTableAttribute {
        val entries: MutableList<VarEntry> = ArrayList()
        val count = stream.readUnsignedShort()
        for (i in 0 until count) {
            val startPc = stream.readUnsignedShort()
            val length = stream.readUnsignedShort()
            val name = stream.readUnsignedShort()
            val desc = stream.readUnsignedShort()
            val index = stream.readUnsignedShort()
            entries.add(VarEntry(startPc, length, name, desc, index))
        }
        return LocalVariableTableAttribute(nameIndex, entries)
    }

    /**
     * @return Line number table.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readLineNumbers(): LineNumberTableAttribute {
        val entries: MutableList<LineEntry> = ArrayList()
        val count = stream.readUnsignedShort()
        for (i in 0 until count) {
            val offset = stream.readUnsignedShort()
            val line = stream.readUnsignedShort()
            entries.add(LineEntry(offset, line))
        }
        return LineNumberTableAttribute(nameIndex, entries)
    }

    /**
     * @return ModuleAttribute attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readModule(): ModuleAttribute {
        val moduleIndex = stream.readUnsignedShort()
        val flags = stream.readUnsignedShort()
        val versionIndex = stream.readUnsignedShort()
        val requires: MutableList<ModuleAttribute.Requires> = ArrayList()
        var count = stream.readUnsignedShort()
        for (i in 0 until count) {
            val reqIndex = stream.readUnsignedShort()
            val reqFlags = stream.readUnsignedShort()
            val reqVersion = stream.readUnsignedShort()
            requires.add(ModuleAttribute.Requires(reqIndex, reqFlags, reqVersion))
        }
        val exports: MutableList<ModuleAttribute.Exports> = ArrayList()
        count = stream.readUnsignedShort()
        for (i in 0 until count) {
            val expIndex = stream.readUnsignedShort()
            val expFlags = stream.readUnsignedShort()
            val expCount = stream.readUnsignedShort()
            val indices: MutableList<Int> = ArrayList()
            for (j in 0 until expCount) {
                indices.add(stream.readUnsignedShort())
            }
            exports.add(ModuleAttribute.Exports(expIndex, expFlags, indices))
        }
        val opens: MutableList<ModuleAttribute.Opens> = ArrayList()
        count = stream.readUnsignedShort()
        for (i in 0 until count) {
            val openIndex = stream.readUnsignedShort()
            val openFlags = stream.readUnsignedShort()
            val openCount = stream.readUnsignedShort()
            val indices: MutableList<Int> = ArrayList()
            for (j in 0 until openCount) {
                indices.add(stream.readUnsignedShort())
            }
            opens.add(ModuleAttribute.Opens(openIndex, openFlags, indices))
        }
        val uses: MutableList<Int> = ArrayList()
        count = stream.readUnsignedShort()
        for (i in 0 until count) {
            uses.add(stream.readUnsignedShort())
        }
        val provides: MutableList<ModuleAttribute.Provides> = ArrayList()
        count = stream.readUnsignedShort()
        for (i in 0 until count) {
            val prvIndex = stream.readUnsignedShort()
            val prvCount = stream.readUnsignedShort()
            val indices: MutableList<Int> = ArrayList()
            for (j in 0 until prvCount) {
                indices.add(stream.readUnsignedShort())
            }
            provides.add(ModuleAttribute.Provides(prvIndex, indices))
        }
        return ModuleAttribute(nameIndex, moduleIndex, flags, versionIndex,
            requires, exports, opens, uses, provides)
    }

    /**
     * @return Signature attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readSignature(): SignatureAttribute {
        val signatureIndex = stream.readUnsignedShort()
        return SignatureAttribute(nameIndex, signatureIndex)
    }

    /**
     * @return Source file name attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readSourceFile(): SourceFileAttribute {
        val sourceFileNameIndex = stream.readUnsignedShort()
        return SourceFileAttribute(nameIndex, sourceFileNameIndex)
    }

    /**
     * @return Enclosing method attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readEnclosingMethod(): EnclosingMethodAttribute {
        val classIndex = stream.readUnsignedShort()
        val methodIndex = stream.readUnsignedShort()
        return EnclosingMethodAttribute(nameIndex, classIndex, methodIndex)
    }

    /**
     * @return Exceptions attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readExceptions(): ExceptionsAttribute {
        val numberOfExceptionIndices = stream.readUnsignedShort()
        val exceptionIndexTable: MutableList<Int> = ArrayList()
        for (i in 0 until numberOfExceptionIndices) {
            exceptionIndexTable.add(stream.readUnsignedShort())
        }
        return ExceptionsAttribute(nameIndex, exceptionIndexTable)
    }

    /**
     * @return Inner classes attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readInnerClasses(): InnerClassesAttribute {
        val numberOfInnerClasses = stream.readUnsignedShort()
        val innerClasses: MutableList<InnerClassesAttribute.InnerClass> = ArrayList()
        for (i in 0 until numberOfInnerClasses) {
            val innerClassInfoIndex = stream.readUnsignedShort()
            val outerClassInfoIndex = stream.readUnsignedShort()
            val innerNameIndex = stream.readUnsignedShort()
            val innerClassAccessFlags = stream.readUnsignedShort()
            innerClasses.add(InnerClassesAttribute.InnerClass(innerClassInfoIndex, outerClassInfoIndex,
                innerNameIndex, innerClassAccessFlags))
        }
        return InnerClassesAttribute(nameIndex, innerClasses)
    }

    /**
     * @return Nest host attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readNestHost(): NestHostAttribute? {
        if (expectedContentLength != 2) {
            logger.debug("Found NestHost with illegal content length: {} != 2", expectedContentLength)
            return null
        }
        val hostClassIndex = stream.readUnsignedShort()
        return NestHostAttribute(nameIndex, hostClassIndex)
    }

    /**
     * @return Nest members attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readNestMembers(): NestMembersAttribute {
        val count = stream.readUnsignedShort()
        val memberClassIndices: MutableList<Int> = ArrayList()
        for (i in 0 until count) {
            val classIndex = stream.readUnsignedShort()
            memberClassIndices.add(classIndex)
        }
        return NestMembersAttribute(nameIndex, memberClassIndices)
    }

    /**
     * @return Source debug attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readSourceDebugExtension(): DebugExtensionAttribute? {
        val debugExtension = ByteArray(expectedContentLength)
        stream.readFully(debugExtension)
        // Validate data represents UTF text
        try {
            DataInputStream(ByteArrayInputStream(debugExtension)).readUTF()
        } catch (t: Throwable) {
            logger.debug("Invalid SourceDebugExtension, not a valid UTF")
            return null
        }
        return DebugExtensionAttribute(nameIndex, debugExtension)
    }

    /**
     * @param context
     * Location the annotation is defined in.
     *
     * @return Annotations attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readAnnotations(context: AttributeContext): AnnotationsAttribute? {
        return AnnotationReader(reader, builder.pool, stream, expectedContentLength, nameIndex, context)
            .readAnnotations()
    }

    /**
     * @param context
     * Location the annotation is defined in.
     *
     * @return ParameterAnnotations attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readParameterAnnotations(context: AttributeContext): ParameterAnnotationsAttribute? {
        return AnnotationReader(reader, builder.pool, stream, expectedContentLength, nameIndex, context)
            .readParameterAnnotations()
    }

    /**
     * @param context
     * Location the annotation is defined in.
     *
     * @return TypeAnnotation attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readTypeAnnotations(context: AttributeContext): AnnotationsAttribute? {
        return AnnotationReader(reader, builder.pool, stream, expectedContentLength, nameIndex, context)
            .readTypeAnnotations()
    }

    /**
     * @param context
     * Location the annotation is defined in.
     *
     * @return AnnotationDefault attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readAnnotationDefault(context: AttributeContext): AnnotationDefaultAttribute? {
        return AnnotationReader(reader, builder.pool, stream, expectedContentLength, nameIndex, context)
            .readAnnotationDefault()
    }

    /**
     * @return Synthetic attribute.
     */
    private fun readSynthetic(): SyntheticAttribute {
        return SyntheticAttribute(nameIndex)
    }

    /**
     * @return Bootstrap methods attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readBoostrapMethods(): BootstrapMethodsAttribute {
        val bootstrapMethods: MutableList<BootstrapMethodsAttribute.BootstrapMethod> = ArrayList()
        val bsmCount = stream.readUnsignedShort()
        for (i in 0 until bsmCount) {
            val methodRef = stream.readUnsignedShort()
            val argCount = stream.readUnsignedShort()
            val args: MutableList<Int> = ArrayList()
            for (j in 0 until argCount) {
                args.add(stream.readUnsignedShort())
            }
            bootstrapMethods.add(BootstrapMethodsAttribute.BootstrapMethod(methodRef, args))
        }
        return BootstrapMethodsAttribute(nameIndex, bootstrapMethods)
    }

    /**
     * @return Code attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readCode(): CodeAttribute {
        var maxStack = -1
        var maxLocals = -1
        var codeLength = -1
        var code: ByteArray? = null
        val exceptions: MutableList<ExceptionTableEntry> = ArrayList()
        val attributes: MutableList<Attribute> = ArrayList()
        // Parse depending on class format version
        if (builder.isOakVersion) {
            // Pre-java oak parsing (half-size data types)
            maxStack = stream.readUnsignedByte()
            maxLocals = stream.readUnsignedByte()
            codeLength = stream.readUnsignedShort()
        } else {
            // Modern parsing
            maxStack = stream.readUnsignedShort()
            maxLocals = stream.readUnsignedShort()
            codeLength = stream.readInt()
        }
        // Read instructions
        code = ByteArray(codeLength)
        stream.readFully(code)
        // Read exceptions
        val numExceptions = stream.readUnsignedShort()
        repeat(numExceptions) { exceptions.add(readCodeException()) }
        // Read attributes
        val numAttributes = stream.readUnsignedShort()
        repeat(numAttributes) {
            val attr = AttributeReader(reader, builder, stream).readAttribute(AttributeContext.ATTRIBUTE)
            if (attr != null) attributes.add(attr)
        }
        return CodeAttribute(nameIndex, maxStack, maxLocals, code, exceptions, attributes)
    }

    /**
     * @return Exception table entry for code attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readCodeException(): ExceptionTableEntry {
        return ExceptionTableEntry(
            stream.readUnsignedShort(),
            stream.readUnsignedShort(),
            stream.readUnsignedShort(),
            stream.readUnsignedShort()
        )
    }

    /**
     * @return Constant value attribute.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readConstantValue(): ConstantValueAttribute {
        val valueIndex = stream.readUnsignedShort()
        return ConstantValueAttribute(nameIndex, valueIndex)
    }

    @Throws(IOException::class)
    private fun readStackMapTable(): StackMapTableAttribute {
        val numEntries = stream.readUnsignedShort()
        val frames: MutableList<StackMapFrame> = ArrayList(numEntries)
        for (i in 0 until numEntries) {
            // u1: frame_type
            val frameType = stream.readUnsignedByte()
            if (frameType <= SAME_FRAME_MAX) {
                // same_frame
                // The offset_delta is the frame_type
                frames.add(SameFrame(frameType))
            } else if (frameType <= SAME_LOCALS_ONE_STACK_ITEM_MAX) {
                // same_locals_1_stack_item_frame
                // The offset_delta is frame_type - 64
                // verification_type_info stack
                val stack = readVerificationTypeInfo()
                frames.add(SameLocalsOneStackItem(
                    frameType - 64,
                    stack
                ))
            } else // full_frame
            // u2: offset_delta
            // verification_type_info locals[u2 number_of_locals]
            // verification_type_info stack[u2 number_of_stack_items]
// append_frame
            // u2: offset_delta
            // verification_type_info locals[frame_type - 251]
// same_frame_extended
            // u2: offset_delta
// chop_frame
            // This frame type indicates that the frame has the same local
            // variables as the previous frame except that the last k local
            // variables are absent, and that the operand stack is empty. The
            // value of k is given by the formula 251 - frame_type.
            // u2: offset_delta
// same_locals_1_stack_item_frame_extended
            // u2: offset_delta
            // verification_type_info stack
                require(frameType >= SAME_LOCALS_ONE_STACK_ITEM_EXTENDED_MIN) {
                    // Tags in the range [128-246] are reserved for future use.
                    "Unknown stackframe tag $frameType"
                }
            if (frameType <= SAME_LOCALS_ONE_STACK_ITEM_EXTENDED_MAX) {
                // same_locals_1_stack_item_frame_extended
                // u2: offset_delta
                val offsetDelta = stream.readUnsignedShort()
                // verification_type_info stack
                val stack = readVerificationTypeInfo()
                frames.add(
                    SameLocalsOneStackItemExtended(
                        offsetDelta,
                        stack
                    )
                )
            } else if (frameType <= CHOP_FRAME_MAX) {
                // chop_frame
                // This frame type indicates that the frame has the same local
                // variables as the previous frame except that the last k local
                // variables are absent, and that the operand stack is empty. The
                // value of k is given by the formula 251 - frame_type.
                val k = 251 - frameType
                // u2: offset_delta
                val offsetDelta = stream.readUnsignedShort()
                frames.add(ChopFrame(offsetDelta, k))
            } else if (frameType < 252) {
                // same_frame_extended
                // u2: offset_delta
                val offsetDelta = stream.readUnsignedShort()
                frames.add(SameFrameExtended(
                    offsetDelta
                ))
            } else if (frameType <= APPEND_FRAME_MAX) {
                // append_frame
                // u2: offset_delta
                val offsetDelta = stream.readUnsignedShort()
                // verification_type_info locals[frame_type - 251]
                val numLocals = frameType - 251
                val locals: MutableList<TypeInfo> = ArrayList(numLocals)
                for (j in 0 until numLocals) {
                    locals.add(readVerificationTypeInfo())
                }
                frames.add(AppendFrame(
                    offsetDelta, locals
                ))
            } else if (frameType <= FULL_FRAME_MAX) {
                // full_frame
                // u2: offset_delta
                val offsetDelta = stream.readUnsignedShort()
                // verification_type_info locals[u2 number_of_locals]
                val numLocals = stream.readUnsignedShort()
                val locals: MutableList<TypeInfo> = ArrayList(numLocals)
                for (j in 0 until numLocals) {
                    locals.add(readVerificationTypeInfo())
                }
                // verification_type_info stack[u2 number_of_stack_items]
                val numStackItems = stream.readUnsignedShort()
                val stack: MutableList<TypeInfo> = ArrayList(numStackItems)
                repeat(numStackItems) {
                    stack.add(readVerificationTypeInfo())
                }
                frames.add(FullFrame(
                    offsetDelta, locals, stack
                ))
            } else {
                throw IllegalArgumentException("Unknown frame type $frameType")
            }
        }
        return StackMapTableAttribute(nameIndex, frames)
    }

    @Throws(IOException::class)
    private fun readVerificationTypeInfo(): TypeInfo {
        // u1 tag
        return when (val tag = stream.readUnsignedByte()) {
            ITEM_TOP -> TopVariableInfo()
            ITEM_INTEGER -> IntegerVariableInfo()
            ITEM_FLOAT -> FloatVariableInfo()
            ITEM_DOUBLE -> DoubleVariableInfo()
            ITEM_LONG -> LongVariableInfo()
            ITEM_NULL -> NullVariableInfo()
            ITEM_UNINITIALIZED_THIS -> UninitializedThisVariableInfo()
            ITEM_OBJECT -> ObjectVariableInfo(stream.readUnsignedShort())
            ITEM_UNINITIALIZED -> UninitializedVariableInfo(stream.readUnsignedShort())
            else -> throw IllegalArgumentException("Unknown verification type tag $tag")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AttributeReader::class.java)
    }
}