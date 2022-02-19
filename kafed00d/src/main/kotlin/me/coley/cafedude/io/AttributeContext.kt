package me.coley.cafedude.io

import me.coley.cafedude.Constants.Annotations
import me.coley.cafedude.Constants.Annotations.*
import org.slf4j.LoggerFactory

/**
 * Indicates where attribute is applied to.
 *
 * @author Matt Coley
 */
enum class AttributeContext {
    CLASS, FIELD, METHOD, ATTRIBUTE;

    companion object {
        private val logger = LoggerFactory.getLogger(AttributeContext::class.java)

        /**
         * @param targetType Type annotation type
         * @return Where the type annotation *(That contains the given target type value)* is located.
         */
        fun fromAnnotationTargetType(targetType: Int): AttributeContext? {
            return when (targetType) {
                PARAMETER_OF_CLASS_OR_INTERFACE, SUPERTYPE, BOUND_TYPE_PARAMETER_OF_CLASS -> CLASS
                PARAMETER_OF_METHOD, BOUND_TYPE_PARAMETER_OF_METHOD, METHOD_RETURN_TYPE, METHOD_RECEIVER_TYPE, METHOD_PARAMETER, METHOD_THROWS -> METHOD
                Annotations.FIELD -> FIELD
                LOCAL_VARIABLE_DECLARATION, RESOURCE_VARIABLE_DECLARATION, EXCEPTION_PARAMETER_DECLARATION, INSTANCEOF_EXPRESSION, NEW_EXPRESSION, LAMBDA_NEW_EXPRESSION, LAMBDA_METHOD_REF_EXPRESSION, CAST_EXPRESSION, TYPE_ARGUMENT_OF_NEW_GENERIC_EXPRESSION, TYPE_ARGUMENT_OF_GENERIC_NEW_METHOD_REF_EXPRESSION, TYPE_ARGUMENT_OF_GENERIC_NEW_LAMBDA_CONSTRUCTOR_EXPRESSION, TYPE_ARGUMENT_OF_GENERIC_METHOD_REF_EXPRESSION -> ATTRIBUTE
                else -> {
                    logger.debug("Unknown target type, cannot determine attribute context for: {}", targetType)
                    null
                }
            }
        }
    }
}