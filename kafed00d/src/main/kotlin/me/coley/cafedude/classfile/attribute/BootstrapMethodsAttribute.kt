package me.coley.cafedude.classfile.attribute

import me.coley.cafedude.classfile.behavior.CpAccessor
import java.util.*


/**
 * Bootstrap methods attribute.
 *
 *
 * @property nameIndex        Name index in constant pool.
 * @property bootstrapMethods List of boostrap methods *(ref + args)*.
 */
class BootstrapMethodsAttribute(nameIndex: Int, val bootstrapMethods: List<BootstrapMethod>) : Attribute(nameIndex) {

    override fun cpAccesses(): MutableSet<Int> {
        val set = super.cpAccesses()
        for (bsm in bootstrapMethods) set.addAll(bsm.cpAccesses())
        return set
    }

    override fun computeInternalLength(): Int {
        return 2 + bootstrapMethods.sumOf(BootstrapMethod::computeLength)
    }

    /**
     * Bootstrap method representation.
     *
     * @property bsmMethodRef Constant pool index of method reference, [CpMethodHandle].
     * @property args         List of arguments as indices of constant pool items.
     */
    class BootstrapMethod(
        val bsmMethodRef: Int,
        val args: List<Int>,
    ) : CpAccessor {

        override fun cpAccesses(): MutableSet<Int> {
            val set: MutableSet<Int> = TreeSet()
            set.add(bsmMethodRef)
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