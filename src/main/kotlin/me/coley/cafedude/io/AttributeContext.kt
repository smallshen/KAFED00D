package me.coley.cafedude.io

import me.coley.cafedude.Constants
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
                Constants.Annotations.PARAMETER_OF_CLASS_OR_INTERFACE, Constants.Annotations.SUPERTYPE, Constants.Annotations.BOUND_TYPE_PARAMETER_OF_CLASS -> CLASS
                Constants.Annotations.PARAMETER_OF_METHOD, Constants.Annotations.BOUND_TYPE_PARAMETER_OF_METHOD, Constants.Annotations.METHOD_RETURN_TYPE, Constants.Annotations.METHOD_RECEIVER_TYPE, Constants.Annotations.METHOD_PARAMETER, Constants.Annotations.METHOD_THROWS -> METHOD
                Constants.Annotations.FIELD -> FIELD
                Constants.Annotations.LOCAL_VARIABLE_DECLARATION, Constants.Annotations.RESOURCE_VARIABLE_DECLARATION, Constants.Annotations.EXCEPTION_PARAMETER_DECLARATION, Constants.Annotations.INSTANCEOF_EXPRESSION, Constants.Annotations.NEW_EXPRESSION, Constants.Annotations.LAMBDA_NEW_EXPRESSION, Constants.Annotations.LAMBDA_METHOD_REF_EXPRESSION, Constants.Annotations.CAST_EXPRESSION, Constants.Annotations.TYPE_ARGUMENT_OF_NEW_GENERIC_EXPRESSION, Constants.Annotations.TYPE_ARGUMENT_OF_GENERIC_NEW_METHOD_REF_EXPRESSION, Constants.Annotations.TYPE_ARGUMENT_OF_GENERIC_NEW_LAMBDA_CONSTRUCTOR_EXPRESSION, Constants.Annotations.TYPE_ARGUMENT_OF_GENERIC_METHOD_REF_EXPRESSION -> ATTRIBUTE
                else -> {
                    logger.debug("Unknown target type, cannot determine attribute context for: {}",
                        targetType)
                    null
                }
            }
        }
    }
}