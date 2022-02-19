package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*

/**
 * Bootstrap methods attribute.
 *
 * @author Matt Coley
 */
class BootstrapMethodsAttribute
/**
 * @param nameIndex        Name index in constant pool.
 * @param bootstrapMethods List of boostrap methods *(ref + args)*.
 */(
    nameIndex: Int,
    /**
     * @param bootstrapMethods New list of boostrap methods.
     */
    var bootstrapMethods: List<BootstrapMethod>,
) : Attribute(nameIndex) {
    /**
     * @return List of boostrap methods.
     */

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        for (bsm in bootstrapMethods) set.addAll(bsm.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        return 2 + bootstrapMethods.stream().mapToInt { obj: BootstrapMethod -> obj.computeLength() }.sum()
    }

    /**
     * Bootstrap method representation.
     *
     * @author Matt Coley
     */
    class BootstrapMethod
    /**
     * @param bsmMethodref Constant pool index of method reference, [CpMethodHandle].
     * @param args         List of arguments as indices of constant pool items.
     */(
        /**
         * @param bsmMethodref New constant pool index of method reference, [CpMethodHandle].
         */
        var bsmMethodref: Int,
        /**
         * @param args New list of arguments to the [bootstrap method][.getBsmMethodref].
         */
        var args: List<Int>,
    ) : CpAccessor {
        /**
         * @return Constant pool index of method reference, [CpMethodHandle].
         */
        /**
         * @return List of arguments to the [bootstrap method][.getBsmMethodref]
         * as indices of constant pool items.
         */

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(bsmMethodref)
            set.addAll(args)
            return set
        }

        /**
         * @return Length of bsm item.
         */
        fun computeLength(): Int {
            // u2 bootstrap_method_ref;
            // u2 num_bootstrap_arguments;
            // u2 bootstrap_arguments[num_bootstrap_arguments];
            return 4 + 2 * args.size
        }
    }
}