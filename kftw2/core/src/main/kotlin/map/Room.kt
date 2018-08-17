package map

data class Room(
    val topLeftX:Int,
    val topLeftY:Int,
    val width: Int,
    val height:Int,
    val tileInstances: MutableList<TileInstance> = mutableListOf())