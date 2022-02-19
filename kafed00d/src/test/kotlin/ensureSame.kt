import me.coley.cafedude.classfile.attribute.CodeAttribute
import me.coley.cafedude.io.ClassFileReader
import me.coley.cafedude.io.ClassFileWriter
import me.coley.cafedude.io.InstructionReader
import java.io.File

// TODO: unit test
fun main() {
    val classReader = ClassFileReader()
//    val bytes = File("run/classes/HelloWorld.class").readBytes()
    val bytes = File("run/classes/Test.class").readBytes()
    val classFile = classReader.read(bytes)
    val classWriter = ClassFileWriter()
    val newBytes = classWriter.write(classFile)
    classFile.methods.forEach { m ->
        println(m)
        m.attributes.forEach {
            println(it)
            if (it is CodeAttribute) {
                val size = InstructionReader.read(it).sumOf { insn -> insn.size }
                println(size)
                println(it.code.size)
            }
        }
    }

//    InstructionReader.read(classFile.methods.first().attributes.filterIsInstance<CodeAttribute>().first())

    File("run/classes/Test2.class").writeBytes(newBytes)
    println(bytes.contentEquals(newBytes))
    println(bytes.size - newBytes.size)
}