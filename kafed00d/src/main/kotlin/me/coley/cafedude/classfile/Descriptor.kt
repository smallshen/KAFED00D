package me.coley.cafedude.classfile

/**
 * Descriptor parsing utility.
 *
 * @author Matt Coley
 */
class Descriptor private constructor(
    val kind: Kind,
    val descriptor: String,
    val arrayLevel: Int = 0,
) {

    /**
     * Example: `[I` would yield `I`.
     *
     * @return Element desc of an [array][Kind.ARRAY] descriptor.
     * Otherwise, self.
     */
    val elementDesc: Descriptor?
        get() = if (arrayLevel == 0) this else from(descriptor.substring(arrayLevel))

    /**
     * @return Return desc of a [Kind.METHOD] descriptor.
     * Otherwise, singleton list of self.
     */
    val parameters: List<Descriptor?>
        get() {
            if (kind == Kind.METHOD) {
                var start = 1
                var stop = 1
                val max = descriptor.indexOf(')')
                val list: MutableList<Descriptor?> = ArrayList()
                while (start < max) {
                    stop++
                    var section = descriptor.substring(start, stop)
                    if (isPrimitive(descriptor)) {
                        list.add(from(section))
                    } else {
                        val first = section[0]
                        if (first == '[') {
                            var i = 1
                            while (i < stop && section[i] == '[') i++
                            val elementStart = descriptor[start + i]
                            stop = if (isPrimitive(elementStart)) start + i else descriptor.indexOf(';', start + 1) + 1
                            section = descriptor.substring(start, stop)
                            list.add(Descriptor(Kind.ARRAY, section, i))
                        } else if (first == 'L' && start + 1 < max) {
                            stop = descriptor.indexOf(';', start + 1) + 1
                            section = descriptor.substring(start, stop)
                            list.add(Descriptor(Kind.OBJECT, section))
                        } else {
                            list.add(Descriptor(Kind.ILLEGAL, section))
                        }
                    }
                    start = stop
                }
                return list
            }
            return listOf(this)
        }// Should not happen

    /**
     * @return Number of parameters the descriptor as, assuming it is a [Kind.METHOD].
     */
    val parameterCount: Int
        get() {
            if (kind == Kind.METHOD) {
                var count = 0
                var current = 1
                val max = descriptor.indexOf(')')
                while (current < max) {
                    var c = descriptor[current]
                    if (isPrimitive(c)) {
                        count++
                        current++
                    } else if (c == 'L') {
                        count++
                        val end = descriptor.indexOf(';', current + 2)
                        if (end < 0) return -1
                        current = end + 1
                    } else if (c == '[') {
                        while (descriptor[current++].also { c = it } == '[');
                        if (isPrimitive(c)) {
                            current++
                            count++
                        } else if (c == 'L') {
                            count++
                            val end = descriptor.indexOf(';', current + 2)
                            if (end < 0) return -1
                            current = end + 1
                        } else {
                            return -1
                        }
                    } else {
                        // Should not happen
                        return -1
                    }
                }
                return count
            }
            return -1
        }

    /**
     * @return Return desc of a [Kind.METHOD] descriptor.
     * Otherwise, self.
     */
    val returnDesc: Descriptor?
        get() = if (kind == Kind.METHOD) from(descriptor.substring(descriptor.indexOf(')') + 1)) else this

    /**
     * @return `true` when the descriptor denotes a wide primitive desc *(double/long)*.
     */
    val isWide: Boolean
        get() {
            if (kind == Kind.PRIMITIVE) {
                val c = descriptor[0]
                return c == 'J' || c == 'D'
            }
            return false
        }

    /**
     * Descriptor kind.
     */
    enum class Kind {
        PRIMITIVE, OBJECT, ARRAY, METHOD, ILLEGAL
    }

    companion object {
        val VOID = Descriptor(Kind.PRIMITIVE, "V")
        val BOOLEAN = Descriptor(Kind.PRIMITIVE, "Z")
        val BYTE = Descriptor(Kind.PRIMITIVE, "B")
        val CHAR = Descriptor(Kind.PRIMITIVE, "C")
        val INT = Descriptor(Kind.PRIMITIVE, "I")
        val FLOAT = Descriptor(Kind.PRIMITIVE, "F")
        val DOUBLE = Descriptor(Kind.PRIMITIVE, "D")
        val LONG = Descriptor(Kind.PRIMITIVE, "J")
        val OBJECT = Descriptor(Kind.OBJECT, "Ljava/lang/Object;")

        /**
         * @param desc Descriptor to parse.
         * @return Descriptor object instance.
         */
        fun from(desc: String?): Descriptor? {
            return if (desc == null || desc.trim { it <= ' ' }.isEmpty()) null else when (desc) {
                "V" -> VOID
                "Z" -> BOOLEAN
                "B" -> BYTE
                "C" -> CHAR
                "I" -> INT
                "F" -> FLOAT
                "D" -> DOUBLE
                "J" -> LONG
                else -> {
                    val first = desc[0]
                    if (first == '[') {
                        val max = desc.length
                        var i = 1
                        while (i < max && desc[i] == '[') i++
                        // Validate the element type is legitimate
                        val d = from(desc.substring(i))
                        if (d == null || d.kind == Kind.ILLEGAL) Descriptor(Kind.ILLEGAL,
                            desc) else Descriptor(Kind.ARRAY, desc, i)
                    } else if (first == '(') {
                        // Validate closing ')' exists and isn't the last char
                        val end = desc.indexOf(')')
                        if (end < 0 || end == desc.length - 1) return Descriptor(Kind.ILLEGAL, desc)
                        val d = Descriptor(Kind.METHOD, desc)
                        // Validate return type
                        if (from(d.returnDesc!!.descriptor)!!.kind == Kind.ILLEGAL) return Descriptor(Kind.ILLEGAL,
                            desc)
                        // Validate parameter count
                        if (d.parameterCount < 0) Descriptor(Kind.ILLEGAL, desc) else d
                    } else if (first == 'L') {
                        val end = desc.indexOf(';')
                        // Validate ';' exists and there is at least one char between 'L' and ';'
                        if (end < 0 || end == 1) Descriptor(Kind.ILLEGAL, desc) else Descriptor(Kind.OBJECT, desc)
                    } else {
                        Descriptor(Kind.ILLEGAL, desc)
                    }
                }
            }
        }

        /**
         * @param desc Descriptor to check.
         * @return `true` if it denotes a primitive.
         */
        fun isPrimitive(desc: String?): Boolean {
            return if (desc == null || desc.length != 1) false else isPrimitive(desc[0])
        }

        /**
         * @param desc Descriptor to check.
         * @return `true` if it denotes a primitive.
         */
        fun isPrimitive(desc: Char): Boolean {
            return when (desc) {
                'V', 'Z', 'B', 'C', 'I', 'F', 'D', 'J' -> true
                else -> false
            }
        }
    }
}