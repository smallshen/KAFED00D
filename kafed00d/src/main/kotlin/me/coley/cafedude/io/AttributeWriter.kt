package me.coley.cafedude.io

import me.coley.cafedude.Constants
import me.coley.cafedude.InvalidClassException
import me.coley.cafedude.classfile.ClassFile
import me.coley.cafedude.classfile.attribute.*
import me.coley.cafedude.classfile.attribute.StackMapTableAttribute.*
import me.coley.cafedude.classfile.constant.CpUtf8
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

/**
 * Attribute writer for all attributes.
 * <br></br>
 * Annotations delegate to [AnnotationWriter] due to complexity.
 *
 * @author Matt Coley
 */
class AttributeWriter
/**
 * @param clazz
 * Class to pull info from.
 */(private val clazz: ClassFile) {
    /**
     * Writes the attribute to a `byte[]`.
     *
     * @param attribute
     * Attribute to write.
     *
     * @return Content written.
     *
     * @throws IOException
     * When the stream cannot be written to.
     * @throws InvalidClassException
     * When the class cannot be written.
     */
    @Throws(IOException::class, InvalidClassException::class)
    fun writeAttribute(attribute: Attribute): ByteArray {
        val baos = ByteArrayOutputStream()
        val out = DataOutputStream(baos)
        if (attribute is DefaultAttribute) {
            out.writeShort(attribute.nameIndex)
            out.writeInt(attribute.data.size)
            out.write(attribute.data)
        } else {
            val cpName = clazz.getCp(attribute.nameIndex) as? CpUtf8
                ?: throw InvalidClassException("Attribute name index does not point to CP_UTF8")
            // Write common attribute bits
            out.writeShort(attribute.nameIndex)
            out.writeInt(attribute.computeInternalLength())
            // Write specific bits.
            // Note: Unlike reading, writing is quite streamline and doesn't require many variable declarations
            //   so I don't think its super necessary to break these into separate methods.
            when (cpName.text) {
                Constants.Attributes.BOOTSTRAP_METHODS -> {
                    val bsms = attribute as BootstrapMethodsAttribute
                    out.writeShort(bsms.bootstrapMethods.size)
                    for (bsm in bsms.bootstrapMethods) {
                        out.writeShort(bsm.bsmMethodRef)
                        out.writeShort(bsm.args.size)
                        for (arg in bsm.args) {
                            out.writeShort(arg)
                        }
                    }
                }
                Constants.Attributes.CHARACTER_RANGE_TABLE -> {}
                Constants.Attributes.CODE -> {
                    val code = attribute as CodeAttribute
                    out.writeShort(code.maxStack)
                    out.writeShort(code.maxLocals)
                    out.writeInt(code.code.size)
                    out.write(code.code)
                    out.writeShort(code.exceptionTable.size)
                    for (tableEntry in code.exceptionTable) {
                        out.writeShort(tableEntry.startPc)
                        out.writeShort(tableEntry.endPc)
                        out.writeShort(tableEntry.handlerPc)
                        out.writeShort(tableEntry.catchTypeIndex)
                    }
                    out.writeShort(code.attributes.size)
                    for (subAttribute in code.attributes) out.write(writeAttribute(subAttribute))
                }
                Constants.Attributes.CONSTANT_VALUE -> out.writeShort((attribute as ConstantValueAttribute).constantValueIndex)
                Constants.Attributes.COMPILATION_ID -> {}
                Constants.Attributes.DEPRECATED, Constants.Attributes.SYNTHETIC -> {}
                Constants.Attributes.ENCLOSING_METHOD -> {
                    val enclosingMethodAttribute = attribute as EnclosingMethodAttribute
                    out.writeShort(enclosingMethodAttribute.classIndex)
                    out.writeShort(enclosingMethodAttribute.methodIndex)
                }
                Constants.Attributes.EXCEPTIONS -> {
                    val exceptionsAttribute = attribute as ExceptionsAttribute
                    out.writeShort(exceptionsAttribute.exceptionIndexTable.size)
                    for (index in exceptionsAttribute.exceptionIndexTable) {
                        out.writeShort(index)
                    }
                }
                Constants.Attributes.INNER_CLASSES -> {
                    val innerClassesAttribute = attribute as InnerClassesAttribute
                    out.writeShort(innerClassesAttribute.innerClasses.size)
                    for (ic in innerClassesAttribute.innerClasses) {
                        out.writeShort(ic.innerClassInfoIndex)
                        out.writeShort(ic.outerClassInfoIndex)
                        out.writeShort(ic.innerNameIndex)
                        out.writeShort(ic.innerClassAccessFlags)
                    }
                }
                Constants.Attributes.LINE_NUMBER_TABLE -> {
                    val lineNumbers = attribute as LineNumberTableAttribute
                    out.writeShort(lineNumbers.entries.size)
                    for (entry in lineNumbers.entries) {
                        out.writeShort(entry.startPc)
                        out.writeShort(entry.line)
                    }
                }
                Constants.Attributes.LOCAL_VARIABLE_TABLE -> {
                    val varTable = attribute as LocalVariableTableAttribute
                    out.writeShort(varTable.entries.size)
                    for (entry in varTable.entries) {
                        out.writeShort(entry.startPc)
                        out.writeShort(entry.length)
                        out.writeShort(entry.nameIndex)
                        out.writeShort(entry.descIndex)
                        out.writeShort(entry.index)
                    }
                }
                Constants.Attributes.LOCAL_VARIABLE_TYPE_TABLE -> {
                    val typeTable = attribute as LocalVariableTypeTableAttribute
                    out.writeShort(typeTable.entries.size)
                    for (entry in typeTable.entries) {
                        out.writeShort(entry.startPc)
                        out.writeShort(entry.length)
                        out.writeShort(entry.nameIndex)
                        out.writeShort(entry.signatureIndex)
                        out.writeShort(entry.index)
                    }
                }
                Constants.Attributes.METHOD_PARAMETERS -> {}
                Constants.Attributes.MODULE -> {
                    val moduleAttribute = attribute as ModuleAttribute
                    out.writeShort(moduleAttribute.moduleIndex)
                    out.writeShort(moduleAttribute.flags)
                    out.writeShort(moduleAttribute.versionIndex)
                    // requires
                    out.writeShort(moduleAttribute.requires.size)
                    for (requires in moduleAttribute.requires) {
                        out.writeShort(requires.index)
                        out.writeShort(requires.flags)
                        out.writeShort(requires.versionIndex)
                    }
                    // exports
                    out.writeShort(moduleAttribute.exports.size)
                    for (exports in moduleAttribute.exports) {
                        out.writeShort(exports.index)
                        out.writeShort(exports.flags)
                        out.writeShort(exports.toIndices.size)
                        for (i in exports.toIndices) out.writeShort(i)
                    }
                    // opens
                    out.writeShort(moduleAttribute.opens.size)
                    for (opens in moduleAttribute.opens) {
                        out.writeShort(opens.index)
                        out.writeShort(opens.flags)
                        out.writeShort(opens.toIndices.size)
                        for (i in opens.toIndices) out.writeShort(i)
                    }
                    // uses
                    out.writeShort(moduleAttribute.uses.size)
                    for (i in moduleAttribute.uses) out.writeShort(i)
                    // provides
                    out.writeShort(moduleAttribute.provides.size)
                    for (provides in moduleAttribute.provides) {
                        out.writeShort(provides.index)
                        out.writeShort(provides.withIndices.size)
                        for (i in provides.withIndices) out.writeShort(i)
                    }
                }
                Constants.Attributes.MODULE_HASHES -> {}
                Constants.Attributes.MODULE_MAIN_CLASS -> {}
                Constants.Attributes.MODULE_PACKAGES -> {}
                Constants.Attributes.MODULE_RESOLUTION -> {}
                Constants.Attributes.MODULE_TARGET -> {}
                Constants.Attributes.NEST_HOST -> {
                    val nestHost = attribute as NestHostAttribute
                    out.writeShort(nestHost.hostClassIndex)
                }
                Constants.Attributes.NEST_MEMBERS -> {
                    val nestMembers = attribute as NestMembersAttribute
                    out.writeShort(nestMembers.memberClassIndices.size)
                    for (classIndex in nestMembers.memberClassIndices) {
                        out.writeShort(classIndex)
                    }
                }
                Constants.Attributes.RECORD -> {
                    val recordAttribute = attribute as RecordAttribute
                    out.writeShort(recordAttribute.components.size)
                    for (component in recordAttribute.components) {
                        out.writeShort(component.nameIndex)
                        out.writeShort(component.descIndex)
                        out.writeShort(component.attributes.size)
                        for (subAttribute in component.attributes) out.write(writeAttribute(subAttribute))
                    }
                }
                Constants.Attributes.RUNTIME_VISIBLE_ANNOTATIONS, Constants.Attributes.RUNTIME_INVISIBLE_ANNOTATIONS -> AnnotationWriter(
                    out).writeAnnotations(
                    (attribute as AnnotationsAttribute))
                Constants.Attributes.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS, Constants.Attributes.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS -> AnnotationWriter(
                    out).writeParameterAnnotations(
                    (attribute as ParameterAnnotationsAttribute))
                Constants.Attributes.RUNTIME_VISIBLE_TYPE_ANNOTATIONS, Constants.Attributes.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS -> AnnotationWriter(
                    out).writeTypeAnnotations(
                    (attribute as AnnotationsAttribute))
                Constants.Attributes.ANNOTATION_DEFAULT -> AnnotationWriter(out).writeAnnotationDefault(
                    (attribute as AnnotationDefaultAttribute))
                Constants.Attributes.PERMITTED_SUBCLASSES -> {
                    val permittedClasses = attribute as PermittedClassesAttribute
                    out.writeShort(permittedClasses.classes.size)
                    for (classIndex in permittedClasses.classes) out.writeShort(classIndex)
                }
                Constants.Attributes.SIGNATURE -> {
                    val signatureAttribute = attribute as SignatureAttribute
                    out.writeShort(signatureAttribute.signatureIndex)
                }
                Constants.Attributes.SOURCE_DEBUG_EXTENSION -> {
                    val debugExtension = attribute as DebugExtensionAttribute
                    out.write(debugExtension.debugExtension)
                }
                Constants.Attributes.SOURCE_FILE -> {
                    val sourceFileAttribute = attribute as SourceFileAttribute
                    out.writeShort(sourceFileAttribute.sourceFileNameIndex)
                }
                Constants.Attributes.SOURCE_ID -> {}
                Constants.Attributes.STACK_MAP_TABLE -> {
                    val stackMapTable = attribute as StackMapTableAttribute
                    writeStackMapTable(out, stackMapTable)
                }
                else -> {}
            }
        }
        return baos.toByteArray()
    }

    @Throws(IOException::class)
    private fun writeVerificationType(out: DataOutputStream, type: StackMapTableAttribute.TypeInfo) {
        out.writeByte(type.tag)
        if (type is ObjectVariableInfo) {
            out.writeShort(type.classIndex)
        } else if (type is UninitializedVariableInfo) {
            out.writeShort(type.offset)
        }
    }

    @Throws(IOException::class)
    private fun writeStackMapTable(out: DataOutputStream, stackMapTable: StackMapTableAttribute) {
        out.writeShort(stackMapTable.frames.size)
        for (frame in stackMapTable.frames) {
            out.writeByte(frame.frameType)
            when (frame) {
                is SameLocalsOneStackItem -> writeVerificationType(out, frame.stack)
                is SameLocalsOneStackItemExtended -> {
                    out.writeShort(frame.offsetDelta)
                    writeVerificationType(out, frame.stack)
                }
                is ChopFrame -> out.writeShort(frame.offsetDelta)
                is SameFrameExtended -> out.writeShort(frame.offsetDelta)
                is AppendFrame -> {
                    out.writeShort(frame.offsetDelta)
                    for (type in frame.additionalLocals) {
                        writeVerificationType(out, type)
                    }
                }
                is FullFrame -> {
                    out.writeShort(frame.offsetDelta)
                    out.writeShort(frame.locals.size)
                    for (type in frame.locals) {
                        writeVerificationType(out, type)
                    }
                    out.writeShort(frame.stack.size)
                    for (type in frame.stack) {
                        writeVerificationType(out, type)
                    }
                }
            }
        }
    }
}