package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Attribute describing the inner classes of a class.
 *
 * @property nameIndex Name index in constant pool.
 * @property classes   All inner classes.
 */
class InnerClassesAttribute(nameIndex: Int, val innerClasses: List<InnerClass>) : Attribute(nameIndex) {

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        for (inner in innerClasses) set.addAll(inner.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        return 2 + innerClasses.size * 8
    }

    /**
     * An inner class.
     *
     *
     * @property innerClassInfoIndex   Index into the constant pool describing this inner class.
     * @property outerClassInfoIndex   Index into the constant pool describing the outer class. 0 if this
     * is a local or anonymous class.
     * @property innerNameIndex        Index into the constant pool. At this index, the name of this inner class
     * will be specified. 0 if this class is anonymous.
     * @property innerClassAccessFlags Access flags of the inner class.
     */
    class InnerClass(
        val innerClassInfoIndex: Int,
        val outerClassInfoIndex: Int,
        val innerNameIndex: Int,
        val innerClassAccessFlags: Int
    ) : CpAccessor {

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(outerClassInfoIndex)
            set.add(innerClassInfoIndex)
            set.add(innerNameIndex)
            return set
        }
    }
}