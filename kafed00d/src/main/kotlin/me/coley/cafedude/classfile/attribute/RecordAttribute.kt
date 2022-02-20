package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Permitted classes attribute.
 *
 * @property nameIndex  Name index in constant pool.
 * @property components Record components *(fields)*.
 */
class RecordAttribute(nameIndex: Int, val components: List<RecordComponent>) : Attribute(nameIndex) {


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
     *
     * @property nameIndex  Index of name of component.
     * @param descIndex  Index of field descriptor of component.
     * @param attributes Attributes of the record field.
     */
    data class RecordComponent(val nameIndex: Int, val descIndex: Int, val attributes: List<Attribute>) : CpAccessor {


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