package map

import com.badlogic.gdx.math.Vector3
import com.lavaeater.kftw.map.*

class ArrayMapManager : IMapManager {



    override fun getVisibleRange(x: Int, y: Int): TileRange {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getVisibleTilesWithFog(position: Vector3): List<RenderableTile> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getVisibleTiles(position: Vector3): Map<TileKey, Tile> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun tileForWorldPosition(position: Vector3): Tile {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTileAt(x: Int, y: Int): Tile {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTileAt(key: TileKey): Tile {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findTileOfTypeInRange(x: Int, y: Int, tileType: String, range: Int): TileKey? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findTileOfTypeInRange(key: TileKey, tileType: String, range: Int): TileKey? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTilesInRange(x: Int, y: Int, range: Int): Map<TileKey, Tile> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTilesInRange(posKey: TileKey, range: Int): Map<TileKey, Tile> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRingOfTiles(tileKey: TileKey, range: Int): List<TileKey> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBandOfTiles(tileKey: TileKey, range: Int, width: Int): List<TileKey> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateTilesFor(xCenter: Int, yCenter: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}