package com.lavaeater.map

import ktx.math.vec2

/**
 * Created by tommie on 2017-09-30.
 */
class MapLoader {
    fun loadMap(fileName:String): GameMap {

//        val mapper = jacksonObjectMapper()
//
//        val fileHandle = Gdx.files.local(fileName)
//        val json = fileHandle.readString()
//        val map = mapper.readValue<GameMap>(json)
        return GameMap("rubba dub dub")
    }

    fun createBasicMap() : GameMap {
        val map = GameMap("basic")
        map.mapObject("circle", 4f)
        map.mapObject("frame", 4f)
        map.mapObject("platform1", 4f, vec2(-160f,110f),0f,0,true)
        map.mapObject("platform4", 4f, vec2(160f,110f),0f,0,true)
        map.mapObject("platform3", 4f, vec2(-170f,-142f),0f,0,true)
        map.mapObject("platform2", 4f, vec2(175f,-142f),0f,0,true)
        map.mapObject("platform5", 4f, vec2(85f, 47f),0f,0,false)
        map.mapObject("palm", 4f, vec2(73f, 65f))

        return map;
    }

    fun saveMap(fileName: String, map: GameMap) {
//        val mapper = jacksonObjectMapper()
//        val writer = mapper.writerWithDefaultPrettyPrinter()
//        val someFileHandle = Gdx.files.local(fileName)
//        val json = writer.writeValueAsString(map)
//        someFileHandle.writeString(json, false)
    }
}