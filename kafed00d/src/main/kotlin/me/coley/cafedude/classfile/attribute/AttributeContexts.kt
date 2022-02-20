package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.Constants.Attributes
import me.coley.cafedude.Constants.Attributes.*
import me.coley.cafedude.io.AttributeContext
import java.util.*

/**
 * Attribute relations to allowed locations.
 *
 * @author Matt Coley
 */
object AttributeContexts {
    /**
     * For more information on location see:
     * [jvms-4.7 Table 4.7-C](https://docs.oracle.com/javase/specs/jvms/se15/html/jvms-4.html#jvms-4.7-320)
     *
     * @param attributeName Name of attribute, see [Attributes].
     * @return Allowed locations for attribute.
     * If the attribute's allowed locations are unknown, then `-1`.
     */
    fun getAllowedContexts(attributeName: String?): Collection<AttributeContext> {
        return when (attributeName) {
            BOOTSTRAP_METHODS, ENCLOSING_METHOD, INNER_CLASSES, MODULE, MODULE_MAIN_CLASS, MODULE_PACKAGES, NEST_HOST, NEST_MEMBERS, PERMITTED_SUBCLASSES, RECORD, SOURCE_DEBUG_EXTENSION, SOURCE_FILE -> EnumSet.of(
                AttributeContext.CLASS
            )
            CONSTANT_VALUE -> EnumSet.of(AttributeContext.FIELD)
            ANNOTATION_DEFAULT, CODE, EXCEPTIONS, METHOD_PARAMETERS, RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS, RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS -> EnumSet.of(
                AttributeContext.METHOD
            )
            DEPRECATED, SYNTHETIC -> EnumSet.of(
                AttributeContext.CLASS,
                AttributeContext.FIELD,
                AttributeContext.METHOD
            )
            LINE_NUMBER_TABLE, LOCAL_VARIABLE_TABLE, LOCAL_VARIABLE_TYPE_TABLE, STACK_MAP_TABLE -> EnumSet.of(
                AttributeContext.ATTRIBUTE
            )
            RUNTIME_VISIBLE_ANNOTATIONS, RUNTIME_VISIBLE_TYPE_ANNOTATIONS, RUNTIME_INVISIBLE_ANNOTATIONS, RUNTIME_INVISIBLE_TYPE_ANNOTATIONS, SIGNATURE -> EnumSet.allOf(
                AttributeContext::class.java
            )
            else -> EnumSet.allOf(AttributeContext::class.java)
        }
    }
}