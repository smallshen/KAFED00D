package me.coley.cafedude.classfile

import me.coley.cafedude.classfile.constant.ConstPoolEntry
import me.coley.cafedude.classfile.constant.CpUtf8
import java.util.*

/**
 * Constant pool wrapper.
 *
 * @author Matt Coley
 */
class ConstPool(private val backing: MutableList<ConstPoolEntry> = ArrayList()) :
    MutableList<ConstPoolEntry> by backing {


    private val wideIndices: SortedSet<Int> = TreeSet()
    override val size: Int
        get() = if (backing.isEmpty()) 0 else internalToCp(backing.size - 1)

    /**
     * Insert an entry after the given index in the pool.
     *
     * @param index CP index.
     * @param entry Inserted pool entry value.
     */
    fun insertAfter(index: Int, entry: ConstPoolEntry) {
        add(index, entry)
    }

    /**
     * Insert an entry before the given index in the pool.
     *
     * @param index CP index.
     * @param entry Inserted pool entry value.
     */
    fun insertBefore(index: Int, entry: ConstPoolEntry) {
        add(index - 1, entry)
    }

    /**
     * @param index CP index of UTF8 constant.
     * @return String value of constant.
     * @throws IllegalArgumentException When the index is not a UTF8 constant.
     */
    fun getUtf(index: Int): String {
        val entry = get(index)
        if (entry is CpUtf8) return entry.text
        error("Index $index not UTF8")
    }

    /**
     * @param index CP index to check/
     * @param type  Type to assert.
     * @return `true` when the entry at the index is the given type.
     */
    fun isIndexOfType(index: Int, type: Class<out ConstPoolEntry?>): Boolean {
        return try {
            val entry = get(index)
            type.isAssignableFrom(entry.javaClass)
        } catch (t: Throwable) {
            false
        }
    }

    /**
     * CP indices are 1-indexed, so the indices must start at 1.
     * In addition, wide constants *(long/double)* take two indices in the CP.
     * <br></br>
     * In order to count wide indices, we use [SortedSet.headSet] which is a sub-set of items
     * that are `< index`.
     *
     * @param index Internal index of [.backing].
     * @return Converted CP index.
     */
    private fun internalToCp(index: Int): Int {
        // 0: Double --> 1
        // 1: String --> 3 --
        // 2: String --> 4
        // 3: Double --> 5
        // 4: String --> 7 --
        // 5: String --> 8
        val wideCount = wideIndices.headSet(index + 1).size
        return 1 + index + wideCount
    }

    /**
     * CP indices are 1-indexed, so the indices must start at 1.
     * In addition, wide constants *(long/double)* take two indices in the CP.
     * <br></br>
     *
     * @param index CP index.
     * @return Converted internal index for [.backing].
     */
    private fun cpToInternal(index: Int): Int {
        // Edge case
        if (index == 0) return 0
        // Convert index back to 0-index
        var internal = index - 1
        // Just subtract until a match. Will be at worst O(N) where N is the # of wide entries.
        while (internalToCp(internal - 1) >= index) {
            internal--
        }
        return internal
    }

    /**
     * Clear wide entries.
     */
    private fun onClear() {
        wideIndices.clear()
    }

    /**
     * Update wide index tracking.
     *
     * @param constPoolEntry Entry added.
     * @param location       Location added.
     */
    private fun onAdd(constPoolEntry: ConstPoolEntry, location: Int) {
        val entrySize = if (constPoolEntry.isWide) 2 else 1
        // Need to push things over since something is being inserted.
        // Shift everything >= location by +entrySize
        val larger = wideIndices.tailSet(location)
        if (!larger.isEmpty()) {
            val tmp: List<Int> = ArrayList(larger)
            larger.clear()
            tmp.forEach { wideIndices.add(it + entrySize) }
        }
        // Add wide
        if (constPoolEntry.isWide) wideIndices.add(location)
    }

    /**
     * Update wide index tracking.
     *
     * @param constPoolEntry Entry removed.
     * @param location       Location removed from.
     */
    private fun onRemove(constPoolEntry: ConstPoolEntry, location: Int) {
        val entrySize = if (constPoolEntry.isWide) 2 else 1
        // Remove wide
        if (constPoolEntry.isWide) wideIndices.remove(location)
        // Need to move everything down to fill the gap.
        // Shift everything >= location by -entrySize
        val larger = wideIndices.tailSet(location + 1)
        if (!larger.isEmpty()) {
            val tmp: List<Int> = ArrayList(larger)
            larger.clear()
            tmp.forEach { wideIndices.add(it - entrySize) }
        }
    }

    override fun isEmpty(): Boolean {
        return backing.isEmpty()
    }

    override operator fun contains(element: ConstPoolEntry): Boolean {
        return backing.contains(element)
    }

    override fun iterator(): MutableIterator<ConstPoolEntry> {
        return backing.iterator()
    }

    override fun add(element: ConstPoolEntry): Boolean {
        onAdd(element, backing.size)
        return backing.add(element)
    }

    override fun add(index: Int, element: ConstPoolEntry) {
        onAdd(element, index)
        backing.add(cpToInternal(index), element)
    }

    override fun removeAt(index: Int): ConstPoolEntry {
        val ret = backing.removeAt(cpToInternal(index))
        onRemove(ret, index)
        return ret
    }

    override fun remove(element: ConstPoolEntry): Boolean {
        onRemove(element, indexOf(element))
        return backing.remove(element)
    }

    override fun containsAll(elements: Collection<ConstPoolEntry>): Boolean {
        return backing.containsAll(elements)
    }

    override fun addAll(elements: Collection<ConstPoolEntry>): Boolean {
        for (constPoolEntry in elements) add(constPoolEntry)
        return true
    }

    override fun addAll(index: Int, elements: Collection<ConstPoolEntry>): Boolean {
        for (constPoolEntry in elements) add(index, constPoolEntry)
        return true
    }

    override fun removeAll(elements: Collection<ConstPoolEntry>): Boolean {
        var ret = false
        for (o in elements) ret = ret or remove(o)
        return ret
    }

    override fun retainAll(elements: Collection<ConstPoolEntry>): Boolean {
        var ret = false
        for (o in this) if (!elements.contains(o)) ret = ret or remove(o)
        return ret
    }

    override fun clear() {
        onClear()
        backing.clear()
    }

    override fun get(index: Int): ConstPoolEntry {
        return backing[cpToInternal(index)]
    }

    override fun set(index: Int, element: ConstPoolEntry): ConstPoolEntry {
        val ret = removeAt(index)
        add(index, element)
        return ret
    }

    override fun indexOf(element: ConstPoolEntry): Int {
        return internalToCp(backing.indexOf(element))
    }

    override fun lastIndexOf(element: ConstPoolEntry): Int {
        return internalToCp(backing.lastIndexOf(element))
    }

    override fun listIterator(): MutableListIterator<ConstPoolEntry> {
        return backing.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<ConstPoolEntry> {
        return backing.listIterator(cpToInternal(index))
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<ConstPoolEntry> {
        return backing.subList(cpToInternal(fromIndex), cpToInternal(toIndex))
    }
}