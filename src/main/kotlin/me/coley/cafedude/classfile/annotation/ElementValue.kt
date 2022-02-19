package me.coley.cafedude.classfile.annotation

import me.coley.cafedude.classfile.behavior.CpAccessor

/**
 * Base attribute element value.
 *
 * @author Matt Coley
 */
abstract class ElementValue
/**
 * @param tag ASCII tag representation, indicating the type of element value.
 */(
    /**
     * @return ASCII tag representation, indicating the type of element value.
     */
    val tag: Char,
) : CpAccessor {

    /**
     * @return Computed size for the element value.
     */
    abstract fun computeLength(): Int
}