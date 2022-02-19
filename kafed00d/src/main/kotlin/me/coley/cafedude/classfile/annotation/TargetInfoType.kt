package me.coley.cafedude.classfile.annotation

import me.coley.cafedude.Constants.Annotations
import me.coley.cafedude.Constants.Annotations.*

/**
 * Target information denoting which type in a declaration or expression is annotated.
 *
 * @author Matt Coley
 */
enum class TargetInfoType : Annotations {
    TYPE_PARAMETER_TARGET, SUPERTYPE_TARGET, TYPE_PARAMETER_BOUND_TARGET, EMPTY_TARGET, FORMAL_PARAMETER_TARGET, THROWS_TARGET, LOCALVAR_TARGET, CATCH_TARGET, OFFSET_TARGET, TYPE_ARGUMENT_TARGET;

    companion object {
        /**
         * Get the associated info type from the type value.
         *
         * @param type Target type value.
         * @return Target info type based on the value.
         */
        fun fromTargetType(type: Int): TargetInfoType {
            return when (type) {
                PARAMETER_OF_CLASS_OR_INTERFACE, PARAMETER_OF_METHOD -> TYPE_PARAMETER_TARGET
                SUPERTYPE -> SUPERTYPE_TARGET
                BOUND_TYPE_PARAMETER_OF_CLASS, BOUND_TYPE_PARAMETER_OF_METHOD -> TYPE_PARAMETER_BOUND_TARGET
                FIELD, METHOD_RETURN_TYPE, METHOD_RECEIVER_TYPE -> EMPTY_TARGET
                METHOD_PARAMETER -> FORMAL_PARAMETER_TARGET
                METHOD_THROWS -> THROWS_TARGET
                LOCAL_VARIABLE_DECLARATION, RESOURCE_VARIABLE_DECLARATION -> LOCALVAR_TARGET
                EXCEPTION_PARAMETER_DECLARATION -> CATCH_TARGET
                INSTANCEOF_EXPRESSION, NEW_EXPRESSION, LAMBDA_NEW_EXPRESSION, LAMBDA_METHOD_REF_EXPRESSION -> OFFSET_TARGET
                CAST_EXPRESSION, TYPE_ARGUMENT_OF_NEW_GENERIC_EXPRESSION, TYPE_ARGUMENT_OF_GENERIC_NEW_METHOD_REF_EXPRESSION, TYPE_ARGUMENT_OF_GENERIC_NEW_LAMBDA_CONSTRUCTOR_EXPRESSION, TYPE_ARGUMENT_OF_GENERIC_METHOD_REF_EXPRESSION -> TYPE_ARGUMENT_TARGET
                else -> throw IllegalArgumentException("Invalid type annotation target_type value")
            }
        }
    }
}