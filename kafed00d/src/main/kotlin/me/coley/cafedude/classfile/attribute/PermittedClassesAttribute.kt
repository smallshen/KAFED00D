package me.coley.cafedude.classfile.attribute

/**
 * Permitted classes attribute.
 *
 *
 * @property nameIndex Name index in constant pool.
 * @property classes   Indices of allowed `CP_CLASS` values.
 */
class PermittedClassesAttribute(nameIndex: Int, val classes: List<Int>) : Attribute(nameIndex) {

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