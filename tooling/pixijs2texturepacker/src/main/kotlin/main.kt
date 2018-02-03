package fungames

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.beust.klaxon.JsonBase
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.TerminalBuilder
import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {

    val converter = PixiJs2TexturePacker()
    converter.convert()

//    var terminal = TerminalBuilder.newBuilder()
//            .initialTerminalSize(Size.of(32, 16))
//            .buildTerminal()
//
//    terminal.flush()
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
                            var fullString = ""//getMetaData("${file.nameWithoutExtension}.png", 64, 64)

                            val obj = Klaxon()
                                    .parseJsonObject(file.reader())

                            val metaObj = obj.obj("meta")!!
                            val framesJson = obj.obj("frames")!!

                            val frames = framesJson.mapValues { getFrameFromJsonObject(it as Map.Entry<String, JsonObject>) }

                            val meta = PixiJsMeta(
                                    metaObj.string("app")!!,
                                    metaObj.string("version")!!,
                                    metaObj.string("image")!!,
                                    metaObj.string("format")!!,
                                    PixiJsSize(metaObj.obj("size")!!.int("w")!!, metaObj.obj("size")!!.int("h")!!))
                            val spriteSheet = PixiJsSpriteSheet(meta, frames)

                            val texturePackerString = convertToTexturePacker(spriteSheet).replace("\n", System.lineSeparator())

                            File(file.parent, file.nameWithoutExtension + ".txt")
                                    .writeText(texturePackerString)
                        }
                    }
                }
            }
        }
    }

    private fun convertToTexturePacker(spriteSheet: PixiJsSpriteSheet): String {
        var textPackString = "${getMetaData(spriteSheet.meta)}"
        for ((key, frame) in spriteSheet.frames) {
            textPackString += getRegion(key, frame)
        }
        return textPackString
    }

    private fun getFrameFromJsonObject(entry: Map.Entry<String, JsonObject>): PixiJsFrame {
        val frameJson = entry.value
        val frameRectangle = rectangleFromJson(frameJson.obj("frame")!!)
        val spriteSourceSize = rectangleFromJson(frameJson.obj("spriteSourceSize")!!)
        val sourceSize = sizeFromJson(frameJson.obj("sourceSize")!!)

        return PixiJsFrame(
                frameRectangle,
                frameJson.boolean("rotated")!!,
                frameJson.boolean("trimmed")!!,
                spriteSourceSize,
                sourceSize)
    }

    private fun sizeFromJson(sizeJson: JsonObject): PixiJsSize {
        return PixiJsSize(sizeJson.int("w")!!, sizeJson.int("h")!!)
    }

    private fun rectangleFromJson(rectangleJson: JsonObject): PixiJsRectangle {
        return PixiJsRectangle(
                rectangleJson.int("x")!!,
                rectangleJson.int("y")!!,
                rectangleJson.int("w")!!,
                rectangleJson.int("h")!!)
    }

    fun getRegion(name: String, frame: PixiJsFrame): String {
        return "${name.removeSuffix(".png")}\n" +
                "\trotate: ${frame.rotated}\n" +
                "\txy: ${frame.frame.x}, ${frame.frame.y}\n" +
                "\tsize: ${frame.sourceSize.w}, ${frame.sourceSize.h}\n" +
                "\torig: ${frame.sourceSize.w}, ${frame.sourceSize.h}\n" +
                "\toffset: 0, 0\n" + //Offset is the offset of the center of the sprite? Whaaat? For tiles, top left
                "\tindex: -1\n"
    }

    fun getMetaData(meta: PixiJsMeta): String {
//        "meta": {
//            "app": "https://github.com/piskelapp/piskel/",
//            "version": "1.0",
//            "image": "darkdirt.png",
//            "format": "RGBA8888",
//            "size": {
//            "w": 40,
//            "h": 40
//        }
        return getMetaData(meta.image, meta.size.w, meta.size.h)
    }


    fun getMetaData(fileName: String, width: Int, height: Int): String {

        return "$fileName\n" +
                "size: $width, $height\n" +
                "format: RGBA8888\n" +
                "filter: Linear,Linear\n" +
                "repeat: none\n"
    }
}

data class PixiJsRectangle(val x: Int, val y: Int, val w: Int, val h: Int)
data class PixiJsFrame(val frame: PixiJsRectangle, val rotated: Boolean, val trimmed: Boolean, val spriteSourceSize: PixiJsRectangle, val sourceSize: PixiJsSize)
data class PixiJsSize(val w: Int, val h: Int)
data class PixiJsMeta(val app: String, val version: String, val image: String, val format: String, val size: PixiJsSize)
data class PixiJsSpriteSheet(val meta: PixiJsMeta, val frames: Map<String, PixiJsFrame>)