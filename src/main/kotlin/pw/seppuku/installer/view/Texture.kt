package pw.seppuku.installer.view

import org.lwjgl.opengl.GL11
import org.lwjgl.BufferUtils
import javax.imageio.ImageIO
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12


class Texture(file: String) {

    private var width: Int = 0
    private var height: Int = 0

    val id: Int by lazy {
        glGenTextures()
    }

    init {
        val image = ImageIO.read(this::class.java.getResource(file))

        width = image.width
        height = image.height

        val pixels = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width);

        val buffer = BufferUtils.createByteBuffer(image.width * image.height * 4) // todo: fix?

        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val pixel = pixels[y * image.width + x]
                buffer.put((pixel shr 16 and 0xFF).toByte()) //r
                buffer.put((pixel shr 8 and 0xFF).toByte()) // g
                buffer.put((pixel and 0xFF).toByte()) // b
                buffer.put((pixel shr 24 and 0xFF).toByte()) // a
            }
        }

        buffer.flip()

        glBindTexture(GL_TEXTURE_2D, id)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
    }
}