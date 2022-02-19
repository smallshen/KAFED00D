package me.coley.cafedude.classfile.annotation

/**
 * Indicates purpose of the associated [TypePathElement].
 *
 * @author Matt Coley
 */
enum class TypePathKind(
    /**
     * @return Backing value.
     */
    val value: Int,
) {
    ARRAY_DEEPER(0), NESTED_DEEPER(1), WILDCARD_BOUND(2), TYPE_ARGUMENT(3);

    companion object {
        /**
         * @param value Backing value.
         * @return Enum kind instance.
         */
        fun fromValue(value: Int): TypePathKind {
            return when (value) {
                0 -> ARRAY_DEEPER
                1 -> NESTED_DEEPER
                2 -> WILDCARD_BOUND
                3 -> TYPE_ARGUMENT
                else -> throw IllegalArgumentException("Invalid type path kind: $value")
            }
        }
    }
}