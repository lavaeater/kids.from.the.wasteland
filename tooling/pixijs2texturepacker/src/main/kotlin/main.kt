package fungames

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.beust.klaxon.JsonReader
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.TerminalBuilder
import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {

    val converter = PixiJs2TexturePacker()
    converter.convert()

    var terminal = TerminalBuilder.newBuilder()
            .initialTerminalSize(Size.of(32, 16))
            .buildTerminal()

    terminal.flush()
}

class PixiJs2TexturePacker {
    val baseDir = "C:\\projects\\kids.from.the.wasteland\\src\\android\\assets\\tiles"
    val file = "darkdirt"
    fun convert(): Unit {
        val textureAtlas = TextureAtlas()
        File(baseDir).listFiles().forEach {
            if (it.isDirectory) {
                it.walk().forEach { file ->
                    if (!file.isDirectory) {
                        //Read the json, create txt-file for TexturePacker
                        if (file.extension.equals("json")) {
                            val metaString = getMetaData("${file.nameWithoutExtension}.png", 64, 64)
                            JsonReader(file.reader()).use {
                                it.beginObject {
                                    while (it.hasNext()) {
                                        val currentNode = it.nextName()
                                        when (currentNode) {

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getMetaData(fileName: String, width: Int, height: Int): String {

        return "$fileName\n" +
                "size: $width, $height\n" +
                "format: RGBA8888\n" +
                "filter: Linear,Linear\n" +
                "repeat: none"
    }
}

data class PixiMeta (val image:String, val format:String, val width:Int, val height:Int)
