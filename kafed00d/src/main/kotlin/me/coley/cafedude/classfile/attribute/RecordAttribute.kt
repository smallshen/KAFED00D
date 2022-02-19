package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Permitted classes attribute.
 *
 * @author Matt Coley
 */
class RecordAttribute
/**
 * @param nameIndex  Name index in constant pool.
 * @param components Record components *(fields)*.
 */(
    nameIndex: Int,
    /**
     * @param components New record components *(fields)*.
     */
    var components: List<RecordComponent>,
) : Attribute(nameIndex) {
    /**
     * @return Record components *(fields)*.
     */

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        for (component in components) set.addAll(component.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        // u2: count
        // u2: class_index * count
        return 2 + components.stream().mapToInt { obj: RecordComponent -> obj.length() }.sum()
    }

    /**
     * Component entry.
     */
    class RecordComponent
    /**
     * @param nameIndex  Index of name of component.
     * @param descIndex  Index of field descriptor of component.
     * @param attributes Attributes of the record field.
     */(
        /**
         * @param nameIndex New index of name of component.
         */
        var nameIndex: Int,
        /**
         * @param descIndex New index of field descriptor of component.
         */
        var descIndex: Int,
        /**
         * @param attributes New attributes of the record field.
         */
        var attributes: List<Attribute>,
    ) : CpAccessor {
        /**
         * @return Index of name of component.
         */
        /**
         * @return Index of field descriptor of component.
         */
        /**
         * @return Attributes of the record field.
         */

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(nameIndex)
            set.add(descIndex)
            return set
        }

        /**
         * @return Component bytecode size.
         */
        fun length(): Int {
            // u2: name_index
            // u2: desc_index
            // u2: attribute_count
            // ??  attributes[]
            var len = 6
            for (attribute in attributes) len += attribute.computeCompleteLength()
            return len
        }
    }
}