package me.coley.cafedude.tree

import me.coley.cafedude.Access.ACC_PUBLIC

class ClassNode {
    var versionMinor: Int = 0
    var versionMajor: Int = 0
    var access: Int = ACC_PUBLIC
    lateinit var name: String
    lateinit var superName: String
    var interfaces: MutableList<String> = mutableListOf()

    var deprecated = false
    var synthetic = false
    var signature: String? = null

    var sourceFile: String? = null
    var sourceDebug: ByteArray? = null

}