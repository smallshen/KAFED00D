package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.attribute.ModuleAttribute.*
import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Module attribute.
 *
 * @param attrNameIndex Name index in constant pool of attribute.
 * @property moduleIndex   Constant pool index of [module name][CpModule].
 * @property flags         Module flags, see
 * `ACC_OPEN / 0x0020`,
 * `ACC_SYNTHETIC / 0x1000`, and
 * `ACC_MANDATED / 0x8000`
 * @property versionIndex  Index in constant pool of module version utf8, or 0 if no version info.
 * @property requires      The [Requires] items.
 * @property exports       The [Exports] items.
 * @property opens         The [Opens] items.
 * @property uses          The uses list.
 * @property provides      The [Provides] items.
 */
class ModuleAttribute(
    attrNameIndex: Int,
    val moduleIndex: Int,
    val flags: Int,
    val versionIndex: Int,
    val requires: List<Requires>,
    val exports: List<Exports>,
    val opens: List<Opens>,
    val uses: List<Int>,
    val provides: List<Provides>,
) : Attribute(attrNameIndex) {


    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        set.add(moduleIndex)
        set.add(versionIndex)
        set.addAll(uses)
        for (requires in requires) set.addAll(requires.cpAccesses())
        for (exports in exports) set.addAll(exports.cpAccesses())
        for (opens in opens) set.addAll(opens.cpAccesses())
        for (provides in provides) set.addAll(provides.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        // 6 = module_name_index + module_flags + module_version_index
        var len = 6
        // requires = count + requires(u2 * 3)
        len += 2 + requires.size * 6
        // exports = count + exports(u2 * 3 + list[u2])
        len += 2 + exports.stream().mapToInt { obj: Exports -> obj.length() }.sum()
        // opens = count + opens(u2 * 3 + list[u2])
        len += 2 + opens.stream().mapToInt { obj: Opens -> obj.length() }.sum()
        // uses = uses_count + list[u2]
        len += 2 + uses.size * 2
        // provides = count + provides*(u2 * 2 + list[u2])
        len += 2 + provides.stream().mapToInt { obj: Provides -> obj.length() }.sum()
        return len
    }

    /**
     * Module dependencies.
     *
     *
     * @property index        Constant pool index of [required module][CpModule].
     * @property flags        Require flags, see [.getFlags] for more info.
     * @property versionIndex Index in constant pool of required module [version string][CpUtf8],
     * or `0` if no version info.
     */
    data class Requires(val index: Int, val flags: Int, val versionIndex: Int) : CpAccessor {
        /**
         * @return Constant pool index of [required module][CpModule].
         */
        /**
         * @return Require flags, see
         *
         *  * `ACC_TRANSITIVE` if any module depending on the current module also depends on
         * [this required module][.getIndex]
         *  * `ACC_STATIC_PHASE` if the dependency is only required at compile time.
         *  * `ACC_SYNTHETIC` if the dependency was not explicitly or implicitly defined
         * in the source of the module.
         *  * `ACC_MANDATED` if the dependency was implicitly defined in the source of the module.
         *
         */
        /**
         * @return Index in constant pool of required module [version string][CpUtf8],
         * or `0` if no version info.
         */

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(versionIndex)
            set.add(index)
            return set
        }
    }

    /**
     * Package export exposure for general usage.
     *
     * @author Matt Coley
     */
    class Exports
    /**
     * @param index     Constant pool index of a [package][CpPackage].
     * @param flags     Export flags,
     * `ACC_SYNTHETIC` if it was not explicitly/implicitly declared in the module source code.
     * `ACC_MANDATED` if it was implicitly declared in the module source code.
     * @param toIndices Constant pool indices of [modules][CpModule] the [package][.getIndex] exports to.
     */(
        /**
         * @param index New package cp index.
         */
        var index: Int,
        /**
         * @param flags New export flags.
         */
        var flags: Int,
        /**
         * @param toIndex New opened module indices.
         */
        var toIndices: List<Int>,
    ) : CpAccessor {
        /**
         * @return Constant pool index of a [package][CpPackage].
         */
        /**
         * @return Export flags,
         * `ACC_SYNTHETIC` if it was not explicitly/implicitly declared in the module source code.
         * `ACC_MANDATED` if it was implicitly declared in the module source code.
         */
        /**
         * @return Constant pool indices of [modules][CpModule] the [package][.getIndex] exports to.
         */

        /**
         * @return Length of the item.
         */
        fun length(): Int {
            // 6 = index + flags + list.size()
            return 6 + 2 * toIndices.size
        }

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(index)
            set.addAll(toIndices)
            return set
        }
    }

    /**
     * Package open exposure for reflection.
     *
     * @author Matt Coley
     */
    class Opens
    /**
     * @param index     Constant pool index of a [package][CpPackage].
     * @param flags     Open flags,
     * `ACC_SYNTHETIC` if it was not explicitly/implicitly declared in the module source code.
     * `ACC_MANDATED` if it was implicitly declared in the module source code.
     * @param toIndices Constant pool indices of [modules][CpModule] the [.getIndex] is open to.
     */(
        /**
         * @param index New package cp index.
         */
        var index: Int,
        /**
         * @param flags New open flags.
         */
        var flags: Int,
        /**
         * @param toIndex New opened module indices.
         */
        var toIndices: List<Int>,
    ) : CpAccessor {
        /**
         * @return Constant pool index of a [package][CpPackage].
         */
        /**
         * @return Open flags,
         * `ACC_SYNTHETIC` if it was not explicitly/implicitly declared in the module source code.
         * `ACC_MANDATED` if it was implicitly declared in the module source code.
         */
        /**
         * @return Constant pool indices of [modules][CpModule] the [package][.getIndex] is open to.
         */

        /**
         * @return Length of the item.
         */
        fun length(): Int {
            // 6 = index + flags + list.size()
            return 6 + 2 * toIndices.size
        }

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(index)
            set.addAll(toIndices)
            return set
        }
    }

    /**
     * Provided interfaces with implementations.
     *
     * @author Matt Coley
     */
    class Provides
    /**
     * @param index     Constant pool index of [class][CpClass] of a service interface.
     * @param withIndex Constant pool indices of [classes][CpClass] that are implementations of
     * [the service interface][.getIndex].
     */(
        /**
         * @param index New service interface index.
         */
        var index: Int,
        /**
         * @return Constant pool indices of [classes][CpClass] that are implementations of
         * [the service interface][.getIndex].
         */
        var withIndices: List<Int>,
    ) : CpAccessor {
        /**
         * @return Constant pool index of [class][CpClass] of a service interface.
         */

        /**
         * @param withIndex New implementation indices.
         */
        fun setWithIndex(withIndex: List<Int>) {
            withIndices = withIndex
        }

        /**
         * @return Length of the item.
         */
        fun length(): Int {
            // 4 = index + list.size()
            return 4 + 2 * withIndices.size
        }

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(index)
            set.addAll(withIndices)
            return set
        }
    }
}