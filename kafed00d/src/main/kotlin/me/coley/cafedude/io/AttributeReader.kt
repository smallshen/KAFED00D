package me.coley.cafedude.io

import me.coley.cafedude.Constants
import me.coley.cafedude.Constants.StackMapTable
import me.coley.cafedude.classfile.attribute.*
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
    private val byteStream: IndexableByteStream

    // Attribute info
    private val expectedContentLength: Int
    private val nameIndex: Int

    /**
     * @param reader  Parent class reader.
     * @param builder Class being build/read into.
     * @param is      Parent stream.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    init {
        // Extract name/lengtjh
        nameIndex = inputStream.readUnsignedShort()
        expectedContentLength = inputStream.readInt()
        // Create local stream
        val subsection = ByteArray(expectedContentLength)
        inputStream.readFully(subsection)
        this.byteStream = IndexableByteStream(subsection)
    }

    /**
     * @param context Where the attribute is applied to.
     * @return Attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    fun readAttribute(context: AttributeContext): Attribute? {
        return try {
            val attribute = read(context) ?: return null
            val read = byteStream.index
            if (read != expectedContentLength) {
                val name = (builder.pool[nameIndex] as CpUtf8).text
                logger.debug("Invalid '{}' on {}, claimed to be {} bytes, but was {}",
                    name,
                    context.name,
                    expectedContentLength,
                    read)
                return null
            }
            attribute
        } catch (ex: IOException) {
            if (reader.dropEofAttributes) {
                val name = (builder.pool[nameIndex] as CpUtf8).text
                logger.debug("Invalid '{}' on {}, EOF thrown when parsing attribute, expected {} bytes",
                    name,
                    context.name,
                    expectedContentLength)
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
            val introducedAt = AttributeVersions.getIntroducedVersion(name)
            if (introducedAt > builder.versionMajor) {
                logger.debug("Found '{}' on {} in class version {}, min supported is {}",
                    name,
                    context.name,
                    builder.versionMajor,
                    introducedAt)
                return null
            }
        }
        when (name) {
            Constants.Attributes.CODE -> return readCode()
            Constants.Attributes.CONSTANT_VALUE -> return readConstantValue()
            Constants.Attributes.DEPRECATED -> return DeprecatedAttribute(nameIndex)
            Constants.Attributes.ENCLOSING_METHOD -> return readEnclosingMethod()
            Constants.Attributes.EXCEPTIONS -> return readExceptions()
            Constants.Attributes.INNER_CLASSES -> return readInnerClasses()
            Constants.Attributes.NEST_HOST -> return readNestHost()
            Constants.Attributes.NEST_MEMBERS -> return readNestMembers()
            Constants.Attributes.SOURCE_DEBUG_EXTENSION -> return readSourceDebugExtension()
            Constants.Attributes.RUNTIME_INVISIBLE_ANNOTATIONS, Constants.Attributes.RUNTIME_VISIBLE_ANNOTATIONS -> return readAnnotations(
                context)
            Constants.Attributes.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS, Constants.Attributes.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS -> return readParameterAnnotations(
                context)
            Constants.Attributes.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS, Constants.Attributes.RUNTIME_VISIBLE_TYPE_ANNOTATIONS -> return readTypeAnnotations(
                context)
            Constants.Attributes.ANNOTATION_DEFAULT -> return readAnnotationDefault(context)
            Constants.Attributes.SYNTHETIC -> return readSynthetic()
            Constants.Attributes.BOOTSTRAP_METHODS -> return readBoostrapMethods()
            Constants.Attributes.SIGNATURE -> return readSignature()
            Constants.Attributes.SOURCE_FILE -> return readSourceFile()
            Constants.Attributes.MODULE -> return readModule()
            Constants.Attributes.STACK_MAP_TABLE -> return readStackMapTable()
            Constants.Attributes.LINE_NUMBER_TABLE -> return readLineNumbers()
            Constants.Attributes.LOCAL_VARIABLE_TABLE -> return readLocalVariables()
            Constants.Attributes.LOCAL_VARIABLE_TYPE_TABLE -> return readLocalVariableTypess()
            Constants.Attributes.PERMITTED_SUBCLASSES -> return readPermittedClasses()
            Constants.Attributes.RECORD -> return readRecord()
            Constants.Attributes.CHARACTER_RANGE_TABLE, Constants.Attributes.COMPILATION_ID, Constants.Attributes.METHOD_PARAMETERS, Constants.Attributes.MODULE_HASHES, Constants.Attributes.MODULE_MAIN_CLASS, Constants.Attributes.MODULE_PACKAGES, Constants.Attributes.MODULE_RESOLUTION, Constants.Attributes.MODULE_TARGET, Constants.Attributes.SOURCE_ID -> {}
            else -> {}
        }
        // No known/unhandled attribute length is less than 2.
        // So if that is given, we likely have an intentionally malformed attribute.
        if (expectedContentLength < 2) {
            logger.debug("Invalid attribute, its content length <= 1")
            byteStream.skipBytes(expectedContentLength)
            return null
        }
        // Default handling, skip remaining bytes
        byteStream.skipBytes(expectedContentLength)
        return DefaultAttribute(nameIndex, byteStream.buffer)
    }

    /**
     * @return Record attribute indicating the current class is a record, and details components of the record.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readRecord(): RecordAttribute {
        val components: MutableList<RecordAttribute.RecordComponent> = ArrayList()
        val count = byteStream.readUnsignedShort()
        for (i in 0 until count) {
            val nameIndex = byteStream.readUnsignedShort()
            val descIndex = byteStream.readUnsignedShort()
            val numAttributes = byteStream.readUnsignedShort()
            val attributes: MutableList<Attribute> = ArrayList()
            for (x in 0 until numAttributes) {
                val attr = AttributeReader(reader, builder, byteStream).readAttribute(AttributeContext.ATTRIBUTE)
                if (attr != null) attributes.add(attr)
            }
            components.add(RecordAttribute.RecordComponent(nameIndex, descIndex, attributes))
        }
        return RecordAttribute(nameIndex, components)
    }

    /**
     * @return Permitted classes authorized to extend/implement the current class.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readPermittedClasses(): PermittedClassesAttribute {
        val entries: MutableList<Int> = ArrayList()
        val count = byteStream.readUnsignedShort()
        for (i in 0 until count) {
            val index = byteStream.readUnsignedShort()
            entries.add(index)
        }
        return PermittedClassesAttribute(nameIndex, entries)
    }

    /**
     * @return Variable type table.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readLocalVariableTypess(): LocalVariableTypeTableAttribute {
        val entries: MutableList<VarTypeEntry> = ArrayList()
        val count = byteStream.readUnsignedShort()
        for (i in 0 until count) {
            val startPc = byteStream.readUnsignedShort()
            val length = byteStream.readUnsignedShort()
            val name = byteStream.readUnsignedShort()
            val sig = byteStream.readUnsignedShort()
            val index = byteStream.readUnsignedShort()
            entries.add(VarTypeEntry(startPc, length, name, sig, index))
        }
        return LocalVariableTypeTableAttribute(nameIndex, entries)
    }

    /**
     * @return Variable table.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readLocalVariables(): LocalVariableTableAttribute {
        val entries: MutableList<VarEntry> = ArrayList()
        val count = byteStream.readUnsignedShort()
        for (i in 0 until count) {
            val startPc = byteStream.readUnsignedShort()
            val length = byteStream.readUnsignedShort()
            val name = byteStream.readUnsignedShort()
            val desc = byteStream.readUnsignedShort()
            val index = byteStream.readUnsignedShort()
            entries.add(VarEntry(startPc, length, name, desc, index))
        }
        return LocalVariableTableAttribute(nameIndex, entries)
    }

    /**
     * @return Line number table.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readLineNumbers(): LineNumberTableAttribute {
        val entries: MutableList<LineEntry> = ArrayList()
        val count = byteStream.readUnsignedShort()
        for (i in 0 until count) {
            val offset = byteStream.readUnsignedShort()
            val line = byteStream.readUnsignedShort()
            entries.add(LineEntry(offset, line))
        }
        return LineNumberTableAttribute(nameIndex, entries)
    }

    /**
     * @return ModuleAttribute attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readModule(): ModuleAttribute {
        val moduleIndex = byteStream.readUnsignedShort()
        val flags = byteStream.readUnsignedShort()
        val versionIndex = byteStream.readUnsignedShort()
        val requires: MutableList<ModuleAttribute.Requires> = ArrayList()
        var count = byteStream.readUnsignedShort()
        for (i in 0 until count) {
            val reqIndex = byteStream.readUnsignedShort()
            val reqFlags = byteStream.readUnsignedShort()
            val reqVersion = byteStream.readUnsignedShort()
            requires.add(ModuleAttribute.Requires(reqIndex, reqFlags, reqVersion))
        }
        val exports: MutableList<ModuleAttribute.Exports> = ArrayList()
        count = byteStream.readUnsignedShort()
        for (i in 0 until count) {
            val expIndex = byteStream.readUnsignedShort()
            val expFlags = byteStream.readUnsignedShort()
            val expCount = byteStream.readUnsignedShort()
            val indices: MutableList<Int> = ArrayList()
            for (j in 0 until expCount) {
                indices.add(byteStream.readUnsignedShort())
            }
            exports.add(ModuleAttribute.Exports(expIndex, expFlags, indices))
        }
        val opens: MutableList<ModuleAttribute.Opens> = ArrayList()
        count = byteStream.readUnsignedShort()
        for (i in 0 until count) {
            val openIndex = byteStream.readUnsignedShort()
            val openFlags = byteStream.readUnsignedShort()
            val openCount = byteStream.readUnsignedShort()
            val indices: MutableList<Int> = ArrayList()
            for (j in 0 until openCount) {
                indices.add(byteStream.readUnsignedShort())
            }
            opens.add(ModuleAttribute.Opens(openIndex, openFlags, indices))
        }
        val uses: MutableList<Int> = ArrayList()
        count = byteStream.readUnsignedShort()
        for (i in 0 until count) {
            uses.add(byteStream.readUnsignedShort())
        }
        val provides: MutableList<ModuleAttribute.Provides> = ArrayList()
        count = byteStream.readUnsignedShort()
        for (i in 0 until count) {
            val prvIndex = byteStream.readUnsignedShort()
            val prvCount = byteStream.readUnsignedShort()
            val indices: MutableList<Int> = ArrayList()
            for (j in 0 until prvCount) {
                indices.add(byteStream.readUnsignedShort())
            }
            provides.add(ModuleAttribute.Provides(prvIndex, indices))
        }
        return ModuleAttribute(nameIndex, moduleIndex, flags, versionIndex, requires, exports, opens, uses, provides)
    }

    /**
     * @return Signature attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readSignature(): SignatureAttribute {
        val signatureIndex = byteStream.readUnsignedShort()
        return SignatureAttribute(nameIndex, signatureIndex)
    }

    /**
     * @return Source file name attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readSourceFile(): SourceFileAttribute {
        val sourceFileNameIndex = byteStream.readUnsignedShort()
        return SourceFileAttribute(nameIndex, sourceFileNameIndex)
    }

    /**
     * @return Enclosing method attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readEnclosingMethod(): EnclosingMethodAttribute {
        val classIndex = byteStream.readUnsignedShort()
        val methodIndex = byteStream.readUnsignedShort()
        return EnclosingMethodAttribute(nameIndex, classIndex, methodIndex)
    }

    /**
     * @return Exceptions attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readExceptions(): ExceptionsAttribute {
        val numberOfExceptionIndices = byteStream.readUnsignedShort()
        val exceptionIndexTable: MutableList<Int> = ArrayList()
        for (i in 0 until numberOfExceptionIndices) {
            exceptionIndexTable.add(byteStream.readUnsignedShort())
        }
        return ExceptionsAttribute(nameIndex, exceptionIndexTable)
    }

    /**
     * @return Inner classes attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readInnerClasses(): InnerClassesAttribute {
        val numberOfInnerClasses = byteStream.readUnsignedShort()
        val innerClasses: MutableList<InnerClassesAttribute.InnerClass> = ArrayList()
        for (i in 0 until numberOfInnerClasses) {
            val innerClassInfoIndex = byteStream.readUnsignedShort()
            val outerClassInfoIndex = byteStream.readUnsignedShort()
            val innerNameIndex = byteStream.readUnsignedShort()
            val innerClassAccessFlags = byteStream.readUnsignedShort()
            innerClasses.add(InnerClassesAttribute.InnerClass(innerClassInfoIndex,
                outerClassInfoIndex,
                innerNameIndex,
                innerClassAccessFlags))
        }
        return InnerClassesAttribute(nameIndex, innerClasses)
    }

    /**
     * @return Nest host attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readNestHost(): NestHostAttribute? {
        if (expectedContentLength != 2) {
            logger.debug("Found NestHost with illegal content length: {} != 2", expectedContentLength)
            return null
        }
        val hostClassIndex = byteStream.readUnsignedShort()
        return NestHostAttribute(nameIndex, hostClassIndex)
    }

    /**
     * @return Nest members attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readNestMembers(): NestMembersAttribute {
        val count = byteStream.readUnsignedShort()
        val memberClassIndices: MutableList<Int> = ArrayList()
        for (i in 0 until count) {
            val classIndex = byteStream.readUnsignedShort()
            memberClassIndices.add(classIndex)
        }
        return NestMembersAttribute(nameIndex, memberClassIndices)
    }

    /**
     * @return Source debug attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readSourceDebugExtension(): DebugExtensionAttribute? {
        val debugExtension = ByteArray(expectedContentLength)
        byteStream.readFully(debugExtension)
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
     * @param context Location the annotation is defined in.
     * @return Annotations attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readAnnotations(context: AttributeContext): AnnotationsAttribute? {
        return AnnotationReader(reader,
            builder.pool,
            byteStream,
            expectedContentLength,
            nameIndex,
            context).readAnnotations()
    }

    /**
     * @param context Location the annotation is defined in.
     * @return ParameterAnnotations attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readParameterAnnotations(context: AttributeContext): ParameterAnnotationsAttribute? {
        return AnnotationReader(reader,
            builder.pool,
            byteStream,
            expectedContentLength,
            nameIndex,
            context).readParameterAnnotations()
    }

    /**
     * @param context Location the annotation is defined in.
     * @return TypeAnnotation attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readTypeAnnotations(context: AttributeContext): AnnotationsAttribute? {
        return AnnotationReader(reader,
            builder.pool,
            byteStream,
            expectedContentLength,
            nameIndex,
            context).readTypeAnnotations()
    }

    /**
     * @param context Location the annotation is defined in.
     * @return AnnotationDefault attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readAnnotationDefault(context: AttributeContext): AnnotationDefaultAttribute? {
        return AnnotationReader(reader,
            builder.pool,
            byteStream,
            expectedContentLength,
            nameIndex,
            context).readAnnotationDefault()
    }

    /**
     * @return Synthetic attribute.
     */
    private fun readSynthetic(): SyntheticAttribute {
        return SyntheticAttribute(nameIndex)
    }

    /**
     * @return Bootstrap methods attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readBoostrapMethods(): BootstrapMethodsAttribute {
        val bootstrapMethods: MutableList<BootstrapMethodsAttribute.BootstrapMethod> = ArrayList()
        val bsmCount = byteStream.readUnsignedShort()
        for (i in 0 until bsmCount) {
            val methodRef = byteStream.readUnsignedShort()
            val argCount = byteStream.readUnsignedShort()
            val args: MutableList<Int> = ArrayList()
            for (j in 0 until argCount) {
                args.add(byteStream.readUnsignedShort())
            }
            bootstrapMethods.add(BootstrapMethodsAttribute.BootstrapMethod(methodRef, args))
        }
        return BootstrapMethodsAttribute(nameIndex, bootstrapMethods)
    }

    /**
     * @return Code attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readCode(): CodeAttribute {
        val maxStack: Int
        val maxLocals: Int
        val codeLength: Int
        val exceptions: MutableList<ExceptionTableEntry> = ArrayList()
        val attributes: MutableList<Attribute> = ArrayList()
        // Parse depending on class format version
        if (builder.isOakVersion) {
            // Pre-java oak parsing (half-size data types)
            maxStack = byteStream.readUnsignedByte()
            maxLocals = byteStream.readUnsignedByte()
            codeLength = byteStream.readUnsignedShort()
        } else {
            // Modern parsing
            maxStack = byteStream.readUnsignedShort()
            maxLocals = byteStream.readUnsignedShort()
            codeLength = byteStream.readInt()
        }
        // Read instructions
        val code = ByteArray(codeLength)
        byteStream.readFully(code)
        // Read exceptions
        val numExceptions = byteStream.readUnsignedShort()
        for (i in 0 until numExceptions) exceptions.add(readCodeException())
        // Read attributes
        val numAttributes = byteStream.readUnsignedShort()
        for (i in 0 until numAttributes) {
            val attr = AttributeReader(reader, builder, byteStream).readAttribute(AttributeContext.ATTRIBUTE)
            if (attr != null) attributes.add(attr)
        }
        return CodeAttribute(nameIndex, maxStack, maxLocals, code, exceptions, attributes)
    }

    /**
     * @return Exception table entry for code attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readCodeException(): ExceptionTableEntry {
        return ExceptionTableEntry(byteStream.readUnsignedShort(),
            byteStream.readUnsignedShort(),
            byteStream.readUnsignedShort(),
            byteStream.readUnsignedShort())
    }

    /**
     * @return Constant value attribute.
     * @throws IOException When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readConstantValue(): ConstantValueAttribute {
        val valueIndex = byteStream.readUnsignedShort()
        return ConstantValueAttribute(nameIndex, valueIndex)
    }

    @Throws(IOException::class)
    private fun readStackMapTable(): StackMapTableAttribute {
        val numEntries = byteStream.readUnsignedShort()
        val frames: MutableList<StackMapTableAttribute.StackMapFrame> = ArrayList(numEntries)
        for (i in 0 until numEntries) {
            // u1: frame_type
            val frameType = byteStream.readUnsignedByte()
            if (frameType <= StackMapTable.SAME_FRAME_MAX) {
                // same_frame
                // The offset_delta is the frame_type
                frames.add(StackMapTableAttribute.SameFrame(frameType))
            } else if (frameType <= StackMapTable.SAME_LOCALS_ONE_STACK_ITEM_MAX) {
                // same_locals_1_stack_item_frame
                // The offset_delta is frame_type - 64
                // verification_type_info stack
                val stack = readVerificationTypeInfo()
                frames.add(SameLocalsOneStackItem(frameType - 64, stack))
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
                require(frameType >= StackMapTable.SAME_LOCALS_ONE_STACK_ITEM_EXTENDED_MIN) {
                    // Tags in the range [128-246] are reserved for future use.
                    "Unknown stackframe tag $frameType"
                }
            if (frameType <= StackMapTable.SAME_LOCALS_ONE_STACK_ITEM_EXTENDED_MAX) {
                // same_locals_1_stack_item_frame_extended
                // u2: offset_delta
                val offsetDelta = byteStream.readUnsignedShort()
                // verification_type_info stack
                val stack = readVerificationTypeInfo()
                frames.add(SameLocalsOneStackItemExtended(offsetDelta, stack))
            } else if (frameType <= StackMapTable.CHOP_FRAME_MAX) {
                // chop_frame
                // This frame type indicates that the frame has the same local
                // variables as the previous frame except that the last k local
                // variables are absent, and that the operand stack is empty. The
                // value of k is given by the formula 251 - frame_type.
                val k = 251 - frameType
                // u2: offset_delta
                val offsetDelta = byteStream.readUnsignedShort()
                frames.add(StackMapTableAttribute.ChopFrame(offsetDelta, k))
            } else if (frameType < 252) {
                // same_frame_extended
                // u2: offset_delta
                val offsetDelta = byteStream.readUnsignedShort()
                frames.add(SameFrameExtended(offsetDelta))
            } else if (frameType <= StackMapTable.APPEND_FRAME_MAX) {
                // append_frame
                // u2: offset_delta
                val offsetDelta = byteStream.readUnsignedShort()
                // verification_type_info locals[frame_type - 251]
                val numLocals = frameType - 251
                val locals: MutableList<StackMapTableAttribute.TypeInfo> = ArrayList(numLocals)
                for (j in 0 until numLocals) {
                    locals.add(readVerificationTypeInfo())
                }
                frames.add(StackMapTableAttribute.AppendFrame(offsetDelta, locals))
            } else if (frameType <= StackMapTable.FULL_FRAME_MAX) {
                // full_frame
                // u2: offset_delta
                val offsetDelta = byteStream.readUnsignedShort()
                // verification_type_info locals[u2 number_of_locals]
                val numLocals = byteStream.readUnsignedShort()
                val locals: MutableList<StackMapTableAttribute.TypeInfo> = ArrayList(numLocals)
                for (j in 0 until numLocals) {
                    locals.add(readVerificationTypeInfo())
                }
                // verification_type_info stack[u2 number_of_stack_items]
                val numStackItems = byteStream.readUnsignedShort()
                val stack: MutableList<StackMapTableAttribute.TypeInfo> = ArrayList(numStackItems)
                for (j in 0 until numStackItems) {
                    stack.add(readVerificationTypeInfo())
                }
                frames.add(StackMapTableAttribute.FullFrame(offsetDelta, locals, stack))
            } else {
                throw IllegalArgumentException("Unknown frame type $frameType")
            }
        }
        return StackMapTableAttribute(nameIndex, frames)
    }

    @Throws(IOException::class)
    private fun readVerificationTypeInfo(): StackMapTableAttribute.TypeInfo {
        // u1 tag
        val tag = byteStream.readUnsignedByte()
        return when (tag) {
            StackMapTable.ITEM_TOP -> TopVariableInfo()
            StackMapTable.ITEM_INTEGER -> IntegerVariableInfo()
            StackMapTable.ITEM_FLOAT -> FloatVariableInfo()
            StackMapTable.ITEM_DOUBLE -> DoubleVariableInfo()
            StackMapTable.ITEM_LONG -> LongVariableInfo()
            StackMapTable.ITEM_NULL -> NullVariableInfo()
            StackMapTable.ITEM_UNINITIALIZED_THIS -> UninitializedThisVariableInfo()
            StackMapTable.ITEM_OBJECT -> {
                // u2 cpool_index
                val cpoolIndex = byteStream.readUnsignedShort()
                ObjectVariableInfo(cpoolIndex)
            }
            StackMapTable.ITEM_UNINITIALIZED -> {
                // u2 offset
                val offset = byteStream.readUnsignedShort()
                UninitializedVariableInfo(offset)
            }
            else -> throw IllegalArgumentException("Unknown verification type tag $tag")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AttributeReader::class.java)
    }
}