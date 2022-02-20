package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.Constants.StackMapTable
import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Used during the process of verification by type checking.
 * <br></br>
 * There may be at most one StackMapTable attribute in the attributes table of
 * a Code attribute.
 * <br></br>
 * In a class file whose version number is 50.0 or above, if a method's Code
 * attribute does not have a StackMapTable attribute, it has an implicit stack
 * map attribute (§4.10.1). This implicit stack map attribute is equivalent to
 * a StackMapTable attribute with number_of_entries equal to zero.
 *
 *
 * @property nameIndex Name index in constant pool.
 * @property frames    Stack map frames of a method.
 */
class StackMapTableAttribute(nameIndex: Int, val frames: List<StackMapFrame>) : Attribute(nameIndex), StackMapTable {
    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        for (frame in frames) set.addAll(frame.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        // u2: number_of_entries
        var length = 2
        // ??: attribute_entries
        for (frame in frames) {
            length += frame.length
        }
        return length
    }

    /**
     * A verification type specifies the type of either one or two locations,
     * where a location is either a single local variable or a single operand
     * stack entry. A verification type consists of a one-byte tag, indicating
     * which type is in use, followed by zero or more bytes, giving more
     * information about the tag.
     */
    abstract class TypeInfo : CpAccessor {
        /**
         * @return The one byte tag representing this type.
         */
        abstract val tag: Int

        /**
         * @return Size in bytes of the serialized type info.
         */
        open val length: Int
            get() =// u1: tag
                1

        override fun cpAccesses(): MutableSet<Int> {
            return mutableSetOf()
        }
    }

    /**
     * Indicates that the local variable has the verification type top.
     */
    class TopVariableInfo : TypeInfo() {
        /**
         * @return The one byte tag representing this type.
         */
        override val tag: Int
            get() = StackMapTable.ITEM_TOP
    }

    /**
     * Indicates that the location has the verification type int.
     */
    class IntegerVariableInfo : TypeInfo() {
        /**
         * @return The one byte tag representing this type.
         */
        override val tag: Int
            get() = StackMapTable.ITEM_INTEGER
    }

    /**
     * Indicates that the location has the verification type float.
     */
    class FloatVariableInfo : TypeInfo() {
        /**
         * @return The one byte tag representing this type.
         */
        override val tag: Int
            get() = StackMapTable.ITEM_FLOAT
    }

    /**
     * Indicates that the location has the verification type null.
     */
    class NullVariableInfo : TypeInfo() {
        /**
         * @return The one byte tag representing this type.
         */
        override val tag: Int
            get() = StackMapTable.ITEM_NULL
    }

    /**
     * Indicates that the location has the verification type uninitializedThis.
     */
    class UninitializedThisVariableInfo : TypeInfo() {
        /**
         * @return The one byte tag representing this type.
         */
        override val tag: Int
            get() = StackMapTable.ITEM_UNINITIALIZED_THIS
    }

    /**
     * Indicates that the location has the verification type which is the class
     * represented by the CONSTANT_Class_info found at classIndex.
     */
    data class ObjectVariableInfo(val classIndex: Int) : TypeInfo() {

        // u2: cpool_index
        /**
         * @return The one byte tag representing this type.
         */
        override val tag: Int
            get() = StackMapTable.ITEM_OBJECT

        /**
         * @return Size in bytes of the serialized type info.
         */
        override val length: Int
            get() =// u1: tag
                // u2: cpool_index
                1 + 2

        override fun cpAccesses(): MutableSet<Int> {
            return mutableSetOf(classIndex)
        }// u1: tag
    }

    /**
     * Indicates that the location has the verification type uninitialized.
     */
    data class UninitializedVariableInfo(val offset: Int) : TypeInfo() {
        /**
         * @return The one byte tag representing this type.
         */
        override val tag: Int
            get() = StackMapTable.ITEM_UNINITIALIZED// u1 tag
        // u2 offset
        /**
         * @return Size in bytes of the serialized type info.
         */
        override val length: Int
            get() =// u1 tag
                // u2 offset
                1 + 2
    }

    /**
     * Indicates the verification type long.
     */
    class LongVariableInfo : TypeInfo() {
        /**
         * @return The one byte tag representing this type.
         */
        override val tag: Int
            get() = StackMapTable.ITEM_LONG
    }

    /**
     * Indicates the verification type double.
     */
    class DoubleVariableInfo : TypeInfo() {
        /**
         * @return The one byte tag representing this type.
         */
        override val tag: Int
            get() = StackMapTable.ITEM_DOUBLE
    }

    /**
     * A stack map frame specifies *(either explicitly or implicitly)* the
     * bytecode offset at which it applies, and the verification types of local
     * variables and operand stack entries for that offset.
     * <br></br>
     * The bytecode offset at which a stack map frame applies is calculated by taking
     * the `offset_delta` of the frame, and `adding offset_delta + 1` to
     * the bytecode offset of the previous frame, unless the previous frame is the
     * initial frame of the method. In that case, the bytecode offset at which the
     * stack map frame applies is the value `offset_delta` specified in the frame.
     */
    abstract class StackMapFrame(val offsetDelta: Int) : CpAccessor {
        /**
         * @return The one byte frame type representing this frame.
         */
        abstract val frameType: Int

        /**
         * @return Size in bytes of the serialized frame.
         */
        open val length: Int
            get() =// u1 frame_type
                1

        override fun cpAccesses(): MutableSet<Int> {
            return mutableSetOf()
        }
    }

    /**
     * This frame type indicates that the frame has exactly the same local
     * variables as the previous frame and that the operand stack is empty.
     */
    class SameFrame(offsetDelta: Int) : StackMapFrame(offsetDelta) {
        override val frameType: Int
            get() = StackMapTable.SAME_FRAME_MIN + offsetDelta
    }

    /**
     * This frame type indicates that the frame has exactly the same local
     * variables as the previous frame and that the operand stack has one entry.
     * The `offset_delta` value for the frame is given by the formula:
     * `frame_type - 64`
     * <br></br>
     * The verification type of the one stack entry appears after the frame type.
     *
     *
     * @property offsetDelta The offset delta of this frame.
     * @property stack       The singular stack item.
     */
    class SameLocalsOneStackItem(offsetDelta: Int, val stack: TypeInfo) : StackMapFrame(offsetDelta) {
        // verification_type_info stack

        /**
         * @return Size in bytes of the serialized frame.
         */
        override val length: Int
            get() =// u1 frame_type
                // verification_type_info stack
                1 + stack.length

        /**
         * @return The one byte frame type representing this frame.
         */
        override val frameType: Int
            get() = StackMapTable.SAME_LOCALS_ONE_STACK_ITEM_MIN + offsetDelta

        override fun cpAccesses(): MutableSet<Int> {
            return stack.cpAccesses()
        }// u1 frame_type
    }

    /**
     * Same as [SameLocalsOneStackItem] except has an explicit `offsetDelta`.
     *
     *
     * @param offsetDelta The offset delta of this frame.
     * @param stack       The singular stack item.
     */
    class SameLocalsOneStackItemExtended(offsetDelta: Int, val stack: TypeInfo) : StackMapFrame(offsetDelta) {
        // u2: offset_delta
        // verification_type_info stack

        /**
         * @return Size in bytes of the serialized frame.
         */
        override val length: Int
            get() =// u1: frame_type
            // u2: offset_delta
                // verification_type_info stack
                1 + 2 + stack.length

        /**
         * @return The one byte frame type representing this frame.
         */
        override val frameType: Int
            get() = StackMapTable.SAME_LOCALS_ONE_STACK_ITEM_EXTENDED_MIN

        override fun cpAccesses(): MutableSet<Int> {
            return stack.cpAccesses()
        }// u1: frame_type
    }

    /**
     * This frame type indicates that the frame has the same local variables as
     * the previous frame except that a given number of the last local variables
     *
     *
     * @param offsetDelta     The offset delta of this frame.
     * @param absentVariables The number of chopped local variables.
     * absent.
     */// u1: frame_type
    class ChopFrame(offsetDelta: Int, val absentVariables: Int) : StackMapFrame(offsetDelta) {
        // u2: offset_delta
        /**
         * @return Size in bytes of the serialized frame.
         */
        override val length: Int
            get() =// u1: frame_type
                // u2: offset_delta
                1 + 2// 1 needs to be added, format starts at 1 instead of 0 as having a
        // chop frame that chops 0 locals would be redundant
        /**
         * @return The one byte frame type representing this frame.
         */
        override val frameType: Int
            get() =// 1 needs to be added, format starts at 1 instead of 0 as having a
                // chop frame that chops 0 locals would be redundant
                StackMapTable.CHOP_FRAME_MAX - absentVariables + 1
    }

    /**
     * This frame type indicates that the frame has exactly the same local
     * variables as the previous frame and that the operand stack is empty.
     */
    class SameFrameExtended(offsetDelta: Int) : StackMapFrame(offsetDelta) {
        /**
         * @return Size in bytes of the serialized frame.
         */
        override val length: Int
            get() = 1 + 2

        /**
         * @return The one byte frame type representing this frame.
         */
        override val frameType: Int
            get() = StackMapTable.SAME_FRAME_EXTENDED_MIN
    }

    /**
     * This frame type indicates that the frame has the same locals as the
     * previous frame except that a number of additional locals are defined, and
     * that the operand stack is empty.
     *
     * @property offsetDelta      The offset delta of this frame.
     * @property additionalLocals The additional locals defined in the frame.
     */
    class AppendFrame(offsetDelta: Int, val additionalLocals: List<TypeInfo>) : StackMapFrame(offsetDelta) {
        // u2: offset_delta
        // verification_type_info locals[frame_type - 251]

        /**
         * @return Size in bytes of the serialized frame.
         */
        override val length: Int
            get() {
                // u1: frame_type
                // u2: offset_delta
                var length = 1 + 2
                // verification_type_info locals[frame_type - 251]
                for (local in additionalLocals) {
                    length += local.length
                }
                return length
            }

        /**
         * @return The one byte frame type representing this frame.
         */
        override val frameType: Int
            get() = additionalLocals.size + StackMapTable.APPEND_FRAME_MIN - 1

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            for (info in additionalLocals) set.addAll(info.cpAccesses())
            return set
        }// u1: frame_type
    }

    /**
     * Contains the full types of the current frame.
     *
     * @property offsetDelta The offset delta of this frame.
     * @property locals      The local variable types of the current frame.
     * @property stack       The types of the current frame's stack.
     */
    class FullFrame(
        offsetDelta: Int,
        val locals: List<TypeInfo>,
        val stack: List<TypeInfo>
    ) : StackMapFrame(offsetDelta) {
        // u2: offset_delta
        // u2 number_of_locals
        // verification_type_info locals[number_of_locals]
        // u2 number_of_stack_items
        // verification_type_info stack[number_of_stack_items]

        /**
         *
         * @return Size in bytes of the serialized frame.
         */
        override val length: Int
            get() {
                // u1: frame_type
                // u2: offset_delta
                var length = 1 + 2
                // u2 number_of_locals
                // verification_type_info locals[number_of_locals]
                length += 2
                for (local in locals) {
                    length += local.length
                }
                // u2 number_of_stack_items
                // verification_type_info stack[number_of_stack_items]
                length += 2
                for (stackType in stack) {
                    length += stackType.length
                }
                return length
            }

        /**
         * @return The one byte frame type representing this frame.
         */
        override val frameType: Int
            get() = StackMapTable.FULL_FRAME_MIN

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            for (info in locals) set.addAll(info.cpAccesses())
            for (info in stack) set.addAll(info.cpAccesses())
            return set
        }// u1: frame_type
    }
}