package me.coley.cafedude.classfile.attribute

/**
 * Permitted classes attribute.
 *
 * @author Matt Coley
 */
class PermittedClassesAttribute
/**
 * @param nameIndex Name index in constant pool.
 * @param classes   Indices of allowed `CP_CLASS` values.
 */(
    nameIndex: Int,
    /**
     * @param classes New indices of allowed `CP_CLASS` values.
     */
    var classes: List<Int>,
) : Attribute(nameIndex) {
    /**
     * @return Indices of allowed `CP_CLASS` values.
     */

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.addAll(classes)
        return set
    }

    override fun computeInternalLength(): Int {
        // u2: count
        // u2: class_index * count
        return 2 + 2 * classes.size
    }
}