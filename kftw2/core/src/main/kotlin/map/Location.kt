package map

/**
 * The tile manager class could be one
 * entry point for the concept of multiple maps.
 *
 * The TileManager actually delivers the tiles that we are using right now
 *
 * The Map Manager basically just delivers tiles for the rendering engine
 * and keeps track of very little, it's mostly pass-through to this
 * class.
 *
 * This class creates maps as we go, in the case of the world map.
 *
 * How do we partition this into something that can generate maps in
 * a different way?
 *
 * We need to partition up the code into the parts we need.
 *
 * We need some way to store and retrieve tiles. That's the tilestore class, which
 * can be reused for any type of map representation (but how do we handle "blank" tiles?
 * do we need a black sprite, or just mark it in some special way? Best would be to not draw
 * anything at all - because the background is black... OK; more on that later).
 *
 * The generation algorithm must be replaceable.
 *
 * We want to be able to use one algorithm for the world map (the current).
 * Then we want to be able to switch areas - this can be handled using a dictionary of
 * tilestores - one entry per "context" or id, if we need to save them later.
 *
 * So we have mutableMapOf("worldmap" to mutableSetOf<TileStoreBase>,
 * "dungeon_1" to mutableSetOf<TileStoreBase>
 *   and so on.
 */
open class Location(val name:String,
                    val parentLocation: Location? = null,
                    val subLocations: MutableSet<Location> = mutableSetOf()) {
    /*
    A location manages its own entities, it just has to.

    Or is that retarded?

    Yes, to tight of a coupling, a location does not handle the location's entities,
    the location **manager** handles the entities, the location must CONTAIN the entities.

    Everything must be some kind of meta representation of itself.

    So, an entity - henceforth called "ACTOR" for lack of a better word... no, AGENT

    We need to categorize things that can "exist" in a location.

    What can exist in a location?

    Well,

    PEOPLE
    MONSTERS
    THINGS / ITEMS
    LOCATIONS

    A location should be describable in text that is human readable

    A location might be the world map
    It could be a town or a dungeon as well.

    The internal storage model of each and every one of these must be hidden
    from the render engine, the world engine, etc.

    So, basically we have map render system. It renders the map.

    Then we have an entity render system - but it renders all the entities in the world,
    how do we, easily, keep track of all entities that are valid for this particular world
    right now?

    - They have to be stored in the location. A location at least needs
    a list of IAgents to keep track of - and perhaps their Entities to be
    able to switch between them easily.

    A location also keeps track of its maxbounds and keeps everything regarding
    how it implements things like that under wraps...

    I see, in the near future, that we start using coroutines to render maps etc.

    So, what we need RIGHT NOW is a system for the user to bump into a dungeon. The dungeon will
    be placed on a rock tile bordering a forest. Easy.

    The user must be presented a choice to enter the dungeon.

    The dungeon must then be generated and the LOCATION needs to be changed.

    So all locations work the exact same. They represent the "context" for the world, so to
    speak. So, we can surely create a fact that represents this!

    The location contains ALL possible sublocations etc for the location. So a location can switch
    to a sublocation OR it's parent location -> the world map for instance.
     */


    /*
    Maybe we should consider a slight rewrite.

    Instead of redoing everything from scratch, lets do a new implementation of the
    map, its locations and entitities etc.

    Could that be workable? Do we have the necessary energy for it?

    So instead of having tile and tileinstance etc we do new classes for that...
     */

    //A location MIGHT have a map!

    val tileMap: TileMap? = null
    val hasMap: Boolean get() = tileMap != null
}