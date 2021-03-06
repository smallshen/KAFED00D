package me.coley.cafedude.classfile.annotation

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Annotation outline. Represents an annotation item to be contained in an annotation collection attribute such as:
 *
 *  * [RuntimeInvisibleAnnotations][AnnotationsAttribute]>
 *  * [RuntimeVisibleAnnotations][AnnotationsAttribute]>
 *  * [RuntimeInvisibleParameterAnnotations][ParameterAnnotationsAttribute]>
 *  * [RuntimeVisibleParameterAnnotations][ParameterAnnotationsAttribute]>
 *
 *
 * @author Matt Coley
 * @see AnnotationsAttribute
 *
 * @see ParameterAnnotationsAttribute
 *
 * @property typeIndex Annotation descriptor index.
 * @property values    Annotation key-value pairs. Keys point to UTF8 constants.
 */
open class Annotation(val typeIndex: Int, val values: Map<Int, ElementValue>) : CpAccessor {

    override fun cpAccesses(): MutableSet<Int> {
        val set: MutableSet<Int> = TreeSet()
        set.add(typeIndex)
        for (value in values.values) set.addAll(value.cpAccesses())
        return set
    }

    /**
     * @return Computed size for the annotation.
     */
    open fun computeLength(): Int {
        // u2: type_index
        // u2: num_element_value_pairs
        var length = 4
        // ??: element_values
        for ((_, value) in values) {
            // u2: name_index (key)
            // ??: value
            length += 2
            length += value.computeLength()
        }
        return length
    }
}