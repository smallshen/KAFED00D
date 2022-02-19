import java.nio.ByteBuffer

fun main() {
    val byteBuffer = ByteBuffer.allocate(4)
    byteBuffer.putFloat(255.2f)
    byteBuffer.array()
    println(byteBuffer.array().size)
}