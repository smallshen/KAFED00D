@file:JvmName("ClassFile")

package me.coley.cafedude.tree

import me.coley.cafedude.classfile.ClassFile
import me.coley.cafedude.classfile.attribute.*
import me.coley.cafedude.classfile.constant.CpUtf8
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ClassReader")!!

fun read(classFile: ClassFile): ClassNode {
    val node = ClassNode()
    with(node) {
        versionMinor = classFile.versionMinor
        versionMajor = classFile.versionMajor
        access = classFile.access
        name = classFile.name
        superName = classFile.superName
        classFile readAttributesTo this
    }

    TODO()
}


private infix fun ClassFile.readAttributesTo(node: ClassNode) {
    attributes.forEach {
        when(it) {
            is InnerClassesAttribute -> TODO()
            is EnclosingMethodAttribute -> TODO()
            is SyntheticAttribute -> TODO()
            is SignatureAttribute -> TODO()
            is SourceFileAttribute -> TODO()
            is SourceDebugExtensionAttribute -> TODO()
            is DeprecatedAttribute -> TODO()
            // runtime visible and invisible annotation
            is AnnotationsAttribute -> TODO()
            is AnnotationDefaultAttribute -> TODO()
            is BootstrapMethodsAttribute -> TODO()

            else -> logger.warn("Unknown attribute type: ${(getCp(it.nameIndex) as CpUtf8).text} appear in class")
        }
    }
}