package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Attribute describing the inner classes of a class.
 *
 * @author JCWasmx86
 */
class InnerClassesAttribute
/**
 * @param nameIndex Name index in constant pool.
 * @param classes   All inner classes.
 */(
    nameIndex: Int,
    /**
     * @param innerClasses The new inner classes of this class.
     */
    var innerClasses: List<InnerClass>,
) : Attribute(nameIndex) {
    /**
     * @return The inner classes of this class.
     */

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
     * @author JCWasmx86
     */
    class InnerClass
    /**
     * @param innerClassInfoIndex   Index into the constant pool describing this inner class.
     * @param outerClassInfoIndex   Index into the constant pool describing the outer class. 0 if this
     * is a local or anonymous class.
     * @param innerNameIndex        Index into the constant pool. At this index, the name of this inner class
     * will be specified. 0 if this class is anonymous.
     * @param innerClassAccessFlags Access flags of the inner class.
     */(
        /**
         * @param innerClassInfoIndex New index into the constant pool describing this inner class.
         */
        var innerClassInfoIndex: Int,
        /**
         * @param outerClassInfoIndex New index into the constant pool describing the outer class. 0 if this
         * is a local or anonymous class.
         */
        var outerClassInfoIndex: Int,
        /**
         * @param innerNameIndex New index into the constant pool. At this index, the name of this inner class
         * will be specified. 0 if this class is anonymous.
         */
        var innerNameIndex: Int,
        /**
         * @param innerClassAccessFlags Access flags of the inner class.
         */
        var innerClassAccessFlags: Int,
    ) : CpAccessor {
        /**
         * @return Index into the constant pool describing this inner class.
         */
        /**
         * @return Index into the constant pool describing the outer class. 0 if this
         * is a local or anonymous class.
         */
        /**
         * @return Index into the constant pool. At this index, the name of this inner class
         * will be specified. 0 if this class is anonymous.
         */
        /**
         * @return Access flags of the inner class.
         */

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(outerClassInfoIndex)
            set.add(innerClassInfoIndex)
            set.add(innerNameIndex)
            return set
        }
    }
}