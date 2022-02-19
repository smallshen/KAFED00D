package me.coley.cafedude.classfile.annotation

import me.coley.cafedude.classfile.behavior.CpAccessor

/**
 * Indicates which type in a declaration or expression is annotated.
 *
 * @author Matt Coley
 */
abstract class TargetInfo protected constructor(targetTypeKind: TargetInfoType, targetType: Int) : CpAccessor {
    /**
     * @return Info type, indicating the union layout. Abstraction of [.targetType].
     */
    val targetTypeKind: TargetInfoType

    /**
     * @return Target type, the `target_type` of a `type_annotation`
     */
    val targetType: Int

    /**
     * @param targetTypeKind
     * Info type, indicating the union layout. Abstraction of [.targetType].
     * @param targetType
     * Target type, the `target_type` of a `type_annotation`
     */
    init {
        this.targetTypeKind = targetTypeKind
        this.targetType = targetType
    }

    override fun cpAccesses(): MutableSet<Int> {
        return mutableSetOf()
    }

    /**
     * @return Length of info.
     */
    abstract fun computeLength(): Int

    /**
     * Indicates the annotation appears on the n'th type parameter of a generic type or member.
     */
    class TypeParameterTargetInfo
    /**
     * @param targetType
     * Value of `target_type` of the enclosing `type_annotation`,
     * indicating the purpose of the `target_info`.
     * @param typeParameterIndex
     * Index of the type parameter annotated.
     */(
        targetType: Int,
        /**
         * @return Index of the type parameter annotated.
         */
        val typeParameterIndex: Int,
    ) :
        TargetInfo(TargetInfoType.TYPE_PARAMETER_TARGET, targetType) {

        override fun computeLength(): Int {
            return 1 // type_parameter_index
        }
    }

    /**
     * Indicates the annotation appears on a type in the `extends` or `implements` clause
     * of a class or interface declaration.
     */
    class SuperTypeTargetInfo
    /**
     * @param targetType
     * Value of `target_type` of the enclosing `type_annotation`,
     * indicating the purpose of the `target_info`.
     * @param superTypeIndex
     * For `extends` index is 65535.
     * Otherwise the index indicates the interface index of the associated class.
     */(
        targetType: Int,
        /**
         * @return For `extends` index is 65535.
         * Otherwise the index indicates the interface index of the associated class.
         */
        val superTypeIndex: Int,
    ) :
        TargetInfo(TargetInfoType.SUPERTYPE_TARGET, targetType) {

        /**
         * @return `` true when [.getSuperTypeIndex] is 65535.
         */
        val isExtends: Boolean
            get() = superTypeIndex == EXTENDS

        override fun cpAccesses(): MutableSet<Int> {
            return mutableSetOf(superTypeIndex)
        }

        override fun computeLength(): Int {
            return 2 // supertype_index
        }

        companion object {
            const val EXTENDS = 65535
        }
    }

    /**
     * Indicates the annotation appears on the n'th bound of the j'th parameter of a generic type or member.
     */
    class TypeParameterBoundTargetInfo(
        targetType: Int,
        /**
         * @return Index of type parameter declaration
         */
        val typeParameterIndex: Int,
        /**
         * @return Index of the bound of the parameter.
         */
        val boundIndex: Int,
    ) :
        TargetInfo(TargetInfoType.TYPE_PARAMETER_BOUND_TARGET, targetType) {


        override fun computeLength(): Int {
            return 2 // type_parameter_index + bound_index
        }
    }

    /**
     * Indicates that an annotation appears on either the type in a field declaration,
     * the return type of a method, the type of a newly constructed object,
     * or the receiver type of a method or constructor.
     */
    class EmptyTargetInfo
    /**
     * @param targetType
     * Value of `target_type` of the enclosing `type_annotation`,
     * indicating the purpose of the `target_info`.
     */
        (targetType: Int) :
        TargetInfo(TargetInfoType.EMPTY_TARGET, targetType) {
        override fun computeLength(): Int {
            return 0 // nothing
        }
    }

    /**
     * Indicates that an annotation appears on the type in a formal parameter declaration of
     * a method, constructor, or lambda expression.
     */
    class FormalParameterTargetInfo(targetType: Int, formalParameterIndex: Int) :
        TargetInfo(TargetInfoType.FORMAL_PARAMETER_TARGET, targetType) {
        /**
         * This is technically not a one-to-one mapping to the method descriptor's parameters according to the specs.
         * It does not give a concrete example, but instead to refer to a similar case for parameter annotations.
         *
         * @return Index of the formal parameter.
         */
        val formalParameterIndex: Int

        /**
         * @param targetType
         * Value of `target_type` of the enclosing `type_annotation`,
         * indicating the purpose of the `target_info`.
         * @param formalParameterIndex
         * Index of the formal parameter.
         */
        init {
            this.formalParameterIndex = formalParameterIndex
        }

        override fun computeLength(): Int {
            return 1 // formal_parameter_index
        }
    }

    /**
     * Indicates that an annotation appears on the n'th type in the `throws` clause
     * of a method or constructor declaration.
     */
    class ThrowsTargetInfo(targetType: Int, throwsTypeIndex: Int) :
        TargetInfo(TargetInfoType.THROWS_TARGET, targetType) {
        /**
         * @return Index of the thrown type in the associated `exception_index_table`
         * of the `Exceptions` attribute.
         */
        val throwsTypeIndex: Int

        /**
         * @param targetType
         * Value of `target_type` of the enclosing `type_annotation`,
         * indicating the purpose of the `target_info`.
         * @param throwsTypeIndex
         * Index of the thrown type in the associated `exception_index_table`
         * of the [ExceptionsAttribute].
         */
        init {
            this.throwsTypeIndex = throwsTypeIndex
        }

        override fun computeLength(): Int {
            return 2 // throws_type_index
        }
    }

    /**
     * Indicates that an annotation appears on the type of a local variable.
     * <br></br>
     * Marked variables types are annotated but are not listed directly.
     * The information provided should be matched with what appears in the [LocalVariableTableAttribute].
     */
    class LocalVarTargetInfo(targetType: Int, variableTable: List<Variable>) :
        TargetInfo(TargetInfoType.LOCALVAR_TARGET, targetType) {
        /**
         * @return The minimal local variable table of values annotated.
         */
        val variableTable: List<Variable>

        /**
         * @param targetType
         * Value of `target_type` of the enclosing `type_annotation`,
         * indicating the purpose of the `target_info`.
         * @param variableTable
         * The minimal local variable table of values annotated.
         */
        init {
            this.variableTable = variableTable
        }

        override fun computeLength(): Int {
            return 2 + 6 * variableTable.size // u2: table_length + (u6 * varCount)
        }

        /**
         * Minimal local variable outline for [LocalVarTargetInfo].
         */
        class Variable(startPc: Int, length: Int, index: Int) {
            /**
             * @return Initial offset in the code attribute the variable starts at.
             */
            val startPc: Int

            /**
             * @return Duration in the code attribute the variable persists for.
             */
            val length: Int

            /**
             * @return Index of the variable in the current frame's local variable array.
             */
            val index: Int

            /**
             * @param startPc
             * Initial offset in the code attribute the variable starts at.
             * @param length
             * Duration in the code attribute the variable persists for.
             * @param index
             * Index of the variable in the current frame's local variable array.
             */
            init {
                this.startPc = startPc
                this.length = length
                this.index = index
            }
        }
    }

    /**
     * Indicates that an annotation appears on the n'th type in an exception parameter declaration.
     */
    class CatchTargetInfo(targetType: Int, exceptionTableIndex: Int) :
        TargetInfo(TargetInfoType.CATCH_TARGET, targetType) {
        /**
         * @return Index of exception parameter type.
         */
        val exceptionTableIndex: Int

        /**
         * @param targetType
         * Value of `target_type` of the enclosing `type_annotation`,
         * indicating the purpose of the `target_info`.
         * @param exceptionTableIndex
         * Index of exception parameter type.
         */
        init {
            this.exceptionTableIndex = exceptionTableIndex
        }

        override fun computeLength(): Int {
            return 2 // exception_table_index
        }
    }

    /**
     * Indicates that an annotation appears on either the type in an `instanceof` expression or
     * a `new` expression, or the type before the :: in a method reference expression.
     */
    class OffsetTargetInfo(targetType: Int, offset: Int) :
        TargetInfo(TargetInfoType.OFFSET_TARGET, targetType) {
        /**
         * @return Offset in the code attribute byte array of the annotated type instruction.
         */
        val offset: Int

        /**
         * @param targetType
         * Value of `target_type` of the enclosing `type_annotation`,
         * indicating the purpose of the `target_info`.
         * @param offset
         * Offset in the code attribute byte array of the annotated type instruction.
         */
        init {
            this.offset = offset
        }

        override fun computeLength(): Int {
            return 2 // offset
        }
    }

    /**
     * Indicates that an annotation appears either on the n'th type in a cast expression,
     * or on the n'th type argument in the explicit type argument list for any of the following:
     *
     *  * A `new` expression
     *  * An explicit constructor invocation statement
     *  * A method invocation expression
     *  * A method reference expression
     *
     */
    class TypeArgumentTargetInfo(targetType: Int, offset: Int, typeArgumentIndex: Int) :
        TargetInfo(TargetInfoType.TYPE_ARGUMENT_TARGET, targetType) {
        /**
         * @return Offset in the code attribute byte array of the annotated type instruction.
         */
        val offset: Int

        /**
         * @return Index of the type in the cast operator that is annotated.
         */
        val typeArgumentIndex: Int

        /**
         * @param targetType
         * Value of `target_type` of the enclosing `type_annotation`,
         * indicating the purpose of the `target_info`.
         * @param offset
         * Offset in the code attribute byte array of the annotated type instruction.
         * @param typeArgumentIndex
         * Index of the type in the cast operator that is annotated.
         */
        init {
            this.offset = offset
            this.typeArgumentIndex = typeArgumentIndex
        }

        override fun computeLength(): Int {
            return 3 // offset + type_argument_index
        }
    }
}