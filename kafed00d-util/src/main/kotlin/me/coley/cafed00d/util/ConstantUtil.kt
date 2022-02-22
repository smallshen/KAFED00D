@file:Suppress("NOTHING_TO_INLINE")

package me.coley.cafed00d.util

import me.coley.cafedude.classfile.ClassFile
import me.coley.cafedude.classfile.constant.CpUtf8

inline fun ClassFile.utf(i: Int): String {
    return this.pool<CpUtf8>(i).text
}