package me.coley.cafedude.classfile.attribute

/**
 * Enclosing method attribute
 *
 * @author JCWasmx86
 */
class EnclosingMethodAttribute
/**
 * @param nameIndex   Name index in constant pool.
 * @param classIndex  Index into the constant pool representing the innermost class that encloses
 * the declaration of the current class.
 * @param methodIndex Used for anonymous classes e.g. in a method or constructor. If not, it is
 * zero.
 */(
    nameIndex: Int,
    /**
     * @param classIndex Set the enclosing class index.
     */
    var classIndex: Int,
    /**
     * @param methodIndex Set the enclosing method index.
     */
    var methodIndex: Int,
) : Attribute(nameIndex) {
    /**
     * @return Class index of the enclosing class.
     */
    /**
     * @return Index of the enclosing method.
     */

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