package amphipolis.model;

/**
 * Represents the game board.
 * Holds the 5 different zones (Mosaic, Amphora, Skeleton, Statue, Entrance)
 * and manages the placement of tiles.
 */
public class Board {

    private Zone mosaicZone;
    private Zone amphoraZone;
    private Zone skeletonZone;
    private Zone statueZone;
    private EntranceZone entranceZone;

    /**
     * Initializes the board and creates empty zones.
     * <b>Post-condition:</b> All zones are instantiated and empty.
     */
    public void init() {
        // TODO: IMPLEMENT IT
        // mosaicZone = new Zone();
    }

    /**
     * Accessor for the Mosaic Zone.
     * @return The Zone object responsible for Mosaic tiles.
     */
    public Zone getMosaicZone() {
        return mosaicZone;
    }

    public Zone getAmphoraZone() {
        return amphoraZone;
    }

    public Zone getSkeletonZone() {
        return skeletonZone;
    }

    public Zone getStatueZone() {
        return statueZone;
    }

    /**
     * Accessor for the special Entrance Zone.
     * @return The EntranceZone object.
     */
    public EntranceZone getEntranceZone() {
        return entranceZone;
    }
}