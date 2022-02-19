package me.coley.cafedude.io

import me.coley.cafedude.classfile.annotation.*
import me.coley.cafedude.classfile.annotation.Annotation
import java.io.DataOutputStream
import kotlin.Throws
import java.io.IOException
import me.coley.cafedude.classfile.attribute.AnnotationDefaultAttribute
import me.coley.cafedude.classfile.attribute.AnnotationsAttribute
import me.coley.cafedude.classfile.attribute.ParameterAnnotationsAttribute
import me.coley.cafedude.classfile.annotation.TargetInfo.TypeParameterTargetInfo
import me.coley.cafedude.classfile.annotation.TargetInfo.SuperTypeTargetInfo
import me.coley.cafedude.classfile.annotation.TargetInfo.TypeParameterBoundTargetInfo
import me.coley.cafedude.classfile.annotation.TargetInfo.FormalParameterTargetInfo
import me.coley.cafedude.classfile.annotation.TargetInfo.ThrowsTargetInfo
import me.coley.cafedude.classfile.annotation.TargetInfo.LocalVarTargetInfo
import me.coley.cafedude.classfile.annotation.TargetInfo.CatchTargetInfo
import me.coley.cafedude.classfile.annotation.TargetInfo.OffsetTargetInfo
import me.coley.cafedude.classfile.annotation.TargetInfo.TypeArgumentTargetInfo
import java.lang.IllegalArgumentException

/**
 * Annotation writer for all annotation attributes.
 *
 * @author Matt Coley
 */
class AnnotationWriter(private val out: DataOutputStream) {
    /**
     * Writes an [AnnotationDefaultAttribute] attribute.
     *
     * @param annoDefault Default value attribute to write.
     * @throws IOException When the stream cannot be written to.
     */
    @Throws(IOException::class)
    fun writeAnnotationDefault(annoDefault: AnnotationDefaultAttribute) {
        writeElementValue(annoDefault.elementValue)
    }

    /**
     * Writes an attribute containing multiple annotations. Used for:
     *
     *  * `RuntimeInvisibleAnnotations`
     *  * `RuntimeVisibleAnnotations`
     *
     *
     * @param annos Annotations to write.
     * @throws IOException When the stream cannot be written to.
     */
    @Throws(IOException::class)
    fun writeAnnotations(annos: AnnotationsAttribute) {
        out.writeShort(annos.annotations.size)
        for (annotation in annos.annotations) {
            writeAnnotation(annotation)
        }
    }

    /**
     * Writes an attribute containing multiple type annotations. Used for:
     *
     *  * `TypeParameterAnnotations`
     *
     *
     * @param annos Annotations to write.
     * @throws IOException When the stream cannot be written to.
     */
    @Throws(IOException::class)
    fun writeTypeAnnotations(annos: AnnotationsAttribute) {
        out.writeShort(annos.annotations.size)
        for (annotation in annos.annotations) {
            writeTypeAnnotation(annotation as TypeAnnotation)
        }
    }

    /**
     * Writes a [ParameterAnnotationsAttribute] attribute.
     *
     * @param annos Annotations to write.
     * @throws IOException When the stream cannot be written to.
     */
    @Throws(IOException::class)
    fun writeParameterAnnotations(annos: ParameterAnnotationsAttribute) {
        out.writeByte(annos.parameterAnnotations.size)
        for ((_, annotations) in annos.parameterAnnotations) {
            out.writeShort(annotations.size)
            for (annotation in annotations) {
                writeAnnotation(annotation)
            }
        }
    }

    /**
     * Common annotation structure writing.
     *
     * @param annotation Annotation to write.
     * @throws IOException When the stream cannot be written to.
     */
    @Throws(IOException::class)
    private fun writeAnnotation(annotation: Annotation) {
        out.writeShort(annotation.typeIndex)
        writeElementPairs(annotation.values)
    }

    /**
     * Common type annotation structure writing.
     *
     * @throws IOException When the stream cannot be written to.
     */
    @Throws(IOException::class)
    private fun writeTypeAnnotation(annotation: TypeAnnotation) {
        // Write target info union
        val info = annotation.targetInfo
        out.writeByte(info.targetType)
        when (info.targetTypeKind) {
            TargetInfoType.TYPE_PARAMETER_TARGET -> {
                val typeParameterTargetInfo = info as TypeParameterTargetInfo
                out.writeByte(typeParameterTargetInfo.typeParameterIndex)
            }
            TargetInfoType.SUPERTYPE_TARGET -> {
                val superTypeTargetInfo = info as SuperTypeTargetInfo
                out.writeShort(superTypeTargetInfo.superTypeIndex)
            }
            TargetInfoType.TYPE_PARAMETER_BOUND_TARGET -> {
                val typeParameterBoundTargetInfo = info as TypeParameterBoundTargetInfo
                out.writeByte(typeParameterBoundTargetInfo.typeParameterIndex)
                out.writeByte(typeParameterBoundTargetInfo.boundIndex)
            }
            TargetInfoType.EMPTY_TARGET -> {}
            TargetInfoType.FORMAL_PARAMETER_TARGET -> {
                val formalParameterTargetInfo = info as FormalParameterTargetInfo
                out.writeByte(formalParameterTargetInfo.formalParameterIndex)
            }
            TargetInfoType.THROWS_TARGET -> {
                val throwsTargetInfo = info as ThrowsTargetInfo
                out.writeShort(throwsTargetInfo.throwsTypeIndex)
            }
            TargetInfoType.LOCALVAR_TARGET -> {
                val localVarTargetInfo = info as LocalVarTargetInfo
                out.writeShort(localVarTargetInfo.variableTable.size)
                for (variable in localVarTargetInfo.variableTable) {
                    out.writeShort(variable.startPc)
                    out.writeShort(variable.length)
                    out.writeShort(variable.index)
                }
            }
            TargetInfoType.CATCH_TARGET -> {
                val catchTargetInfo = info as CatchTargetInfo
                out.writeShort(catchTargetInfo.exceptionTableIndex)
            }
            TargetInfoType.OFFSET_TARGET -> {
                val offsetTargetInfo = info as OffsetTargetInfo
                out.writeShort(offsetTargetInfo.offset)
            }
            TargetInfoType.TYPE_ARGUMENT_TARGET -> {
                val typeArgumentTargetInfo = info as TypeArgumentTargetInfo
                out.writeShort(typeArgumentTargetInfo.offset)
                out.writeByte(typeArgumentTargetInfo.typeArgumentIndex)
            }
            else -> throw IllegalArgumentException("Invalid type argument target")
        }
        // Write type path
        writeTypePath(annotation.typePath)
        // Write the rest of the normal annotation
        writeAnnotation(annotation)
    }

    /**
     * @param typePath Type path to write
     * @throws IOException When the stream cannot be written to.
     */
    @Throws(IOException::class)
    private fun writeTypePath(typePath: TypePath) {
        out.writeByte(typePath.path.size)
        for (element in typePath.path) {
            out.writeByte(element.kind.value)
            out.writeByte(element.argIndex)
        }
    }

    /**
     * @param values The annotation field pairs *(`NameIndex` --> `Value`)* to write.
     * @throws IOException When the stream cannot be written to.
     */
    @Throws(IOException::class)
    private fun writeElementPairs(values: Map<Int, ElementValue>) {
        out.writeShort(values.size)
        for ((nameIndex, value) in values) {
            out.writeShort(nameIndex)
            writeElementValue(value)
        }
    }

    /**
     * @param elementValue Annotation field *(Technically method)* value to write.
     * @throws IOException When the stream cannot be written to.
     */
    @Throws(IOException::class)
    private fun writeElementValue(elementValue: ElementValue) {
        out.writeByte(elementValue.tag.code)
        when (elementValue.tag) {
            'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z' -> {
                val primitiveElementValue = elementValue as PrimitiveElementValue
                out.writeShort(primitiveElementValue.valueIndex)
            }
            's' -> {
                val utf8ElementValue = elementValue as Utf8ElementValue
                out.writeShort(utf8ElementValue.utfIndex)
            }
            'e' -> {
                val enumElementValue = elementValue as EnumElementValue
                out.writeShort(enumElementValue.typeIndex)
                out.writeShort(enumElementValue.nameIndex)
            }
            'c' -> {
                val classElementValue = elementValue as ClassElementValue
                out.writeShort(classElementValue.classIndex)
            }
            '@' -> {
                val annotationElementValue = elementValue as AnnotationElementValue
                writeAnnotation(annotationElementValue.annotation)
            }
            '[' -> {
                val arrayElementValue = elementValue as ArrayElementValue
                out.writeShort(arrayElementValue.array.size)
                for (arrayValue in arrayElementValue.array) {
                    writeElementValue(arrayValue)
                }
            }
            else -> {}
        }
    }
}