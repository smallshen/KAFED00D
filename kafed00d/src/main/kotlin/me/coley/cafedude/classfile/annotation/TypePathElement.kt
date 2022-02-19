package me.coley.cafedude.classfile.annotation

/**
 * Type path item.
 *
 * @author Matt Coley
 */
class TypePathElement(
    /**
     * @return Indicator of purpose of the element.
     */
    val kind: TypePathKind,
    argIndex: Int,
) {
    /**
     * @return Which type argument of a parameterized type is annotated.
     */
    val argIndex: Int

    /**
     * @param kind     Indicator of purpose of the element.
     * @param argIndex Which type argument of a parameterized type is annotated.
     */
    init {
        // Argument indices only allowed for type argument kinds
        require(!(kind != TypePathKind.TYPE_ARGUMENT && argIndex != 0)) {
            "Type path kind was not TYPE_ARGUMENT " + "but gave a non-zero type_argument_index"
        }
        this.argIndex = argIndex
    }
}