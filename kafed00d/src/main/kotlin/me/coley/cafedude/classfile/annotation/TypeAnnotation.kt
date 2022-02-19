package me.coley.cafedude.classfile.annotation

/**
 * Type annotation outline.
 *
 * @author Matt Coley
 */
class TypeAnnotation
/**
 * @param typeIndex  Annotation descriptor index.
 * @param values     Annotation key-value pairs. Keys point to UTF8 constants.
 * @param targetInfo Information about where the annotation is applied.
 * @param typePath   Information about which part of the type is annotated.
 */(
    typeIndex: Int, values: Map<Int, ElementValue>,
    /**
     * @return Information about where the annotation is applied.
     */
    val targetInfo: TargetInfo,
    /**
     * @return Information about which part of the type is annotated.
     */
    val typePath: TypePath,
) : Annotation(typeIndex, values) {

    override fun computeLength(): Int {
        var length = 1 // u1: target_type
        length += targetInfo.computeLength() // ??: target_info
        length += typePath.computeLength() // type_path: target_path
        // Now add the rest of the normal annotation length
        return length + super.computeLength()
    }
}