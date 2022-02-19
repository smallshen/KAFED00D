package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.Constants
import me.coley.cafedude.Constants.Attributes
import me.coley.cafedude.Constants.Attributes.*

/**
 * Attribute relations to class file versions.
 *
 * @author Matt Coley
 */
object AttributeVersions {
    /**
     * For more information on history see:
     * [jvms-4.7 Table 4.7-B](https://docs.oracle.com/javase/specs/jvms/se15/html/jvms-4.html#jvms-4.7-310)
     *
     * @param attributeName Name of attribute, see [Attributes].
     * @return Java version attribute was introduced in.
     * If the attribute's introduction version is unknown, then `-1`.
     */
    fun getIntroducedVersion(attributeName: String?): Int {
        return when (attributeName) {
            CODE, CONSTANT_VALUE, DEPRECATED, EXCEPTIONS, INNER_CLASSES, LINE_NUMBER_TABLE, LOCAL_VARIABLE_TABLE, SOURCE_FILE, SYNTHETIC -> Constants.JAVA1
            ANNOTATION_DEFAULT, ENCLOSING_METHOD, LOCAL_VARIABLE_TYPE_TABLE, RUNTIME_INVISIBLE_ANNOTATIONS, RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS, RUNTIME_VISIBLE_ANNOTATIONS, RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS, SIGNATURE, SOURCE_DEBUG_EXTENSION -> Constants.JAVA5
            STACK_MAP_TABLE -> Constants.JAVA6
            BOOTSTRAP_METHODS -> Constants.JAVA7
            METHOD_PARAMETERS, RUNTIME_INVISIBLE_TYPE_ANNOTATIONS, RUNTIME_VISIBLE_TYPE_ANNOTATIONS -> Constants.JAVA8
            MODULE, MODULE_MAIN_CLASS, MODULE_PACKAGES -> Constants.JAVA9
            NEST_HOST, NEST_MEMBERS -> Constants.JAVA11
            // Records first preview in 14
            RECORD -> Constants.JAVA14
            // Sealed classes first preview in 15
            PERMITTED_SUBCLASSES -> Constants.JAVA15


            // TODO: Research unused attributes?
            //  - Some of the following items are listed as consts in the compiler, but are nowhere in the spec...
            //     - CHARACTER_RANGE_TABLE
            //     - COMPILATION_ID
            //     - MODULE_HASHES
            //     - MODULE_RESOLUTION
            //     - MODULE_TARGET
            //     - SOURCE_ID
            else -> -1
        }

    }
}