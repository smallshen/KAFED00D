import java.nio.ByteBuffer

fun main() {
    val byteBuffer = ByteBuffer.allocate(64)
    byteBuffer.put((1 and 0xff).toByte())
    byteBuffer.putInt(2)
    val b= ByteBuffer.wrap(byteBuffer.array())
    println(b.get())
    println(b.int)
}