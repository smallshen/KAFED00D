package me.coley.cafedude.io

import me.coley.cafedude.classfile.ConstPool
import me.coley.cafedude.classfile.annotation.*
import me.coley.cafedude.classfile.annotation.Annotation
import me.coley.cafedude.classfile.annotation.TargetInfo.*
import me.coley.cafedude.classfile.attribute.AnnotationDefaultAttribute
import me.coley.cafedude.classfile.attribute.AnnotationsAttribute
import me.coley.cafedude.classfile.attribute.ParameterAnnotationsAttribute
import me.coley.cafedude.classfile.constant.CpUtf8
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException

/**
 * Annotation reader for all annotation attributes.
 *
 * @author Matt Coley
 */
class AnnotationReader(
    private val reader: ClassFileReader, private val cp: ConstPool, inputStream: DataInputStream, length: Int,
    nameIndex: Int, context: AttributeContext,
) {
    private val inputStream: DataInputStream
    private val context: AttributeContext
    private val nameIndex: Int
    private val maxCpIndex: Int


    init {
        val data = ByteArray(length)
        inputStream.readFully(data)
        this.inputStream = DataInputStream(ByteArrayInputStream(data))
        this.nameIndex = nameIndex
        this.context = context
        maxCpIndex = cp.size
    }

    /**
     * Reads an [AnnotationDefaultAttribute] attribute.
     *
     * @return The annotation default attribute read. `null` when malformed.
     */
    fun readAnnotationDefault(): AnnotationDefaultAttribute? {
        return try {
            AnnotationDefaultAttribute(nameIndex, readElementValue())
        } catch (t: Throwable) {
            logger.debug("Illegally formatted AnnotationDefault", t)
            null
        }
    }

    /**
     * Reads an attribute containing multiple annotations. Used for:
     *
     *  * `RuntimeInvisibleAnnotations`
     *  * `RuntimeVisibleAnnotations`
     *
     *
     * @return The type annotation attribute read. `null` if the annotation was malformed.
     */
    fun readAnnotations(): AnnotationsAttribute? {
        return try {
            // Skip if obvious junk
            val numAnnotations = inputStream.readUnsignedShort()
            if (numAnnotations == 0) {
                logger.debug("Annotations attribute has 0 items, skipping")
                return null
            }
            // Read each annotation
            val usedAnnotationTypes: MutableSet<String> = HashSet()
            val annotations: MutableList<Annotation> = ArrayList()
            for (i in 0 until numAnnotations) {
                val annotation = readAnnotation()
                if (reader.dropDupeAnnotations) {
                    // Only add if the type hasn't been used before
                    val type = cp.getUtf(annotation.typeIndex)
                    if (!usedAnnotationTypes.contains(type)) {
                        annotations.add(annotation)
                        usedAnnotationTypes.add(type)
                    }
                } else {
                    // Add unconditionally
                    annotations.add(annotation)
                }
            }
            // Didn't throw exception, its valid
            AnnotationsAttribute(nameIndex, annotations)
        } catch (t: Throwable) {
            logger.debug("Illegally formatted Annotations", t)
            null
        }
    }

    /**
     * Reads a parameter annotation.
     *
     * @return The type annotation attribute read. `null` if the annotation was malformed.
     */
    fun readParameterAnnotations(): ParameterAnnotationsAttribute? {
        return try {
            // Skip if obvious junk
            val numParameters = inputStream.readUnsignedByte()
            if (numParameters == 0) {
                logger.debug("ParameterAnnotations attribute has 0 items, skipping")
                return null
            }
            // Each parameter has its own number of annotations to parse
            val parameterAnnotations: MutableMap<Int, List<Annotation>> = LinkedHashMap()
            for (p in 0 until numParameters) {
                val annotations: MutableList<Annotation> = ArrayList()
                val numAnnotations = inputStream.readUnsignedShort()
                for (i in 0 until numAnnotations) annotations.add(readAnnotation())
                parameterAnnotations[p] = annotations
            }
            // Didn't crash, its valid
            ParameterAnnotationsAttribute(nameIndex, parameterAnnotations)
        } catch (t: Throwable) {
            logger.debug("Illegally formatted ParameterAnnotations", t)
            null
        }
    }

    /**
     * Reads a collection of type annotations *(TypeParameterAnnotations)*.
     *
     * @return The type annotation attribute read. `null` if the annotation was malformed.
     */
    fun readTypeAnnotations(): AnnotationsAttribute? {
        return try {
            // Skip if obvious junk
            val numAnnotations = inputStream.readUnsignedShort()
            if (numAnnotations == 0) {
                logger.debug("TypeAnnotations attribute has 0 items, skipping")
                return null
            }
            // Read each type annotation
            val annotations: MutableList<Annotation> = ArrayList()
            for (i in 0 until numAnnotations) annotations.add(readTypeAnnotation())
            // Didn't throw exception, its valid
            AnnotationsAttribute(nameIndex, annotations)
        } catch (t: Throwable) {
            logger.debug("Illegally formatted TypeAnnotations", t)
            null
        }
    }

    /**
     * Common annotation structure reading.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readAnnotation(): Annotation {
        val typeIndex = inputStream.readUnsignedShort()
        // Validate the type points to an entry in the constant pool that is valid UTF8 item
        if (typeIndex >= maxCpIndex) {
            logger.warn(
                "Illegally formatted Annotation item, out of CP bounds, type_index={} >= {}",
                typeIndex,
                maxCpIndex
            )
            throw IllegalArgumentException("Annotation type_index out of CP bounds!")
        }
        if (!cp.isIndexOfType(typeIndex, CpUtf8::class.java)) {
            logger.warn("Illegally formatted Annotation item, type_index={} != CP_UTF8", typeIndex)
            throw IllegalArgumentException("Annotation type_index does not point to CP_UTF8!")
        }
        val values = readElementPairs()
        return Annotation(typeIndex, values)
    }

    /**
     * Common type annotation structure reading.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readTypeAnnotation(): TypeAnnotation {
        // Read target type (lets us know where the type annotation is located)
        val targetType = inputStream.readUnsignedByte()
        val expectedLocation = AttributeContext.fromAnnotationTargetType(targetType)
        // Skip if context is not expected location.
        require(context == expectedLocation) { "Annotation location does not match allowed locations for its type" }
        // Parse target info union
        val targetInfoType = TargetInfoType.fromTargetType(targetType)
        val info: TargetInfo
        info = when (targetInfoType) {
            TargetInfoType.TYPE_PARAMETER_TARGET -> {
                val typeParameterIndex = inputStream.readUnsignedByte()
                TypeParameterTargetInfo(targetType, typeParameterIndex)
            }
            TargetInfoType.SUPERTYPE_TARGET -> {
                val superTypeIndex = inputStream.readUnsignedShort()
                SuperTypeTargetInfo(targetType, superTypeIndex)
            }
            TargetInfoType.TYPE_PARAMETER_BOUND_TARGET -> {
                val typeParameterIndex = inputStream.readUnsignedByte()
                val boundIndex = inputStream.readUnsignedByte()
                TypeParameterBoundTargetInfo(targetType, typeParameterIndex, boundIndex)
            }
            TargetInfoType.EMPTY_TARGET -> {
                EmptyTargetInfo(targetType)
            }
            TargetInfoType.FORMAL_PARAMETER_TARGET -> {
                val formalParameterIndex = inputStream.readUnsignedByte()
                FormalParameterTargetInfo(targetType, formalParameterIndex)
            }
            TargetInfoType.THROWS_TARGET -> {
                val throwsTypeIndex = inputStream.readUnsignedShort()
                ThrowsTargetInfo(targetType, throwsTypeIndex)
            }
            TargetInfoType.LOCALVAR_TARGET -> {
                val variables: MutableList<LocalVarTargetInfo.Variable> = ArrayList()
                val tableLength = inputStream.readUnsignedShort()
                var i = 0
                while (i < tableLength) {
                    val startPc = inputStream.readUnsignedShort()
                    val length = inputStream.readUnsignedShort()
                    val index = inputStream.readUnsignedShort()
                    variables.add(LocalVarTargetInfo.Variable(startPc, length, index))
                    i++
                }
                LocalVarTargetInfo(targetType, variables)
            }
            TargetInfoType.CATCH_TARGET -> {
                val exceptionTableIndex = inputStream.readUnsignedShort()
                CatchTargetInfo(targetType, exceptionTableIndex)
            }
            TargetInfoType.OFFSET_TARGET -> {
                val offset = inputStream.readUnsignedShort()
                OffsetTargetInfo(targetType, offset)
            }
            TargetInfoType.TYPE_ARGUMENT_TARGET -> {
                val offset = inputStream.readUnsignedShort()
                val typeArgumentIndex = inputStream.readUnsignedByte()
                TypeArgumentTargetInfo(targetType, offset, typeArgumentIndex)
            }
            else -> throw IllegalArgumentException("Invalid type argument target")
        }
        // Parse type path
        val typePath = readTypePath()
        // Parse the stuff that populates a normal annotation
        val typeIndex = inputStream.readUnsignedShort()
        val values = readElementPairs()
        return TypeAnnotation(typeIndex, values, info, typePath)
    }

    /**
     * @return Read type path.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readTypePath(): TypePath {
        val length = inputStream.readUnsignedByte()
        val elements: MutableList<TypePathElement> = ArrayList()
        for (i in 0 until length) {
            val kind = inputStream.readUnsignedByte()
            val index = inputStream.readUnsignedByte()
            elements.add(TypePathElement(TypePathKind.fromValue(kind), index))
        }
        return TypePath(elements)
    }

    /**
     * @return The annotation field pairs *(`NameIndex` --> `Value`)*.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readElementPairs(): Map<Int, ElementValue> {
        var numPairs = inputStream.readUnsignedShort()
        val values: MutableMap<Int, ElementValue> = LinkedHashMap()
        while (numPairs > 0) {
            val nameIndex = inputStream.readUnsignedShort()
            val value = readElementValue()
            require(!values.containsKey(nameIndex)) { "Element pairs already has field by name index: $nameIndex" }
            values[nameIndex] = value
            numPairs--
        }
        return values
    }

    /**
     * @return The annotation field *(Technically method)* value.
     *
     * @throws IOException
     * When the stream is unexpectedly closed or ends.
     */
    @Throws(IOException::class)
    private fun readElementValue(): ElementValue {
        val tag = inputStream.readUnsignedByte().toChar()
        when (tag) {
            'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z' -> {
                val index = inputStream.readUnsignedShort()
                return PrimitiveElementValue(tag, index)
            }
            's' -> {
                val utfIndex = inputStream.readUnsignedShort()
                return Utf8ElementValue(tag, utfIndex)
            }
            'e' -> {
                val typeNameIndex = inputStream.readUnsignedShort()
                val constNameIndex = inputStream.readUnsignedShort()
                return EnumElementValue(tag, typeNameIndex, constNameIndex)
            }
            'c' -> {
                val classInfoIndex = inputStream.readUnsignedShort()
                return ClassElementValue(tag, classInfoIndex)
            }
            '@' -> {
                val nestedAnnotation = readAnnotation()
                return AnnotationElementValue(tag, nestedAnnotation)
            }
            '[' -> {
                val numElements = inputStream.readUnsignedShort()
                val arrayValues: MutableList<ElementValue> = ArrayList()
                var i = 0
                while (i < numElements) {
                    arrayValues.add(readElementValue())
                    i++
                }
                return ArrayElementValue(tag, arrayValues)
            }
            else -> logger.debug("Unknown element_value tag: ({}) '{}'", tag.code, tag)
        }
        throw IllegalArgumentException("Unrecognized tag for annotation element value: $tag")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AnnotationReader::class.java)
    }
}