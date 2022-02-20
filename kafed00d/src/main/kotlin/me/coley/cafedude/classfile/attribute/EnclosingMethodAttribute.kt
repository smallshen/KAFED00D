package me.coley.cafedude.classfile.attribute

/**
 * Enclosing method attribute
 *
 * @property nameIndex   Name index in constant pool.
 * @property classIndex  Index into the constant pool representing the innermost class that encloses
 * the declaration of the current class.
 * @property methodIndex Used for anonymous classes e.g. in a method or constructor. If not, it is
 * zero.
 */
class EnclosingMethodAttribute
    (
    nameIndex: Int,
    val classIndex: Int,
    val methodIndex: Int,
) : Attribute(nameIndex) {

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.add(classIndex)
        set.add(methodIndex)
        return set
    }

    override fun computeInternalLength(): Int {
        return 4
    }
}