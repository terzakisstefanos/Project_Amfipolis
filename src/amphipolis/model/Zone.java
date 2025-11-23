package amphipolis.model;

import java.util.ArrayList;

public class Zone {

    /**
     * The list of tiles currently in this zone.
     */
    private ArrayList<Tile> tiles;

    /**
     * Constructor.
     * Initializes the empty list of tiles.
     */
    public Zone() {
        this.tiles = new ArrayList<>();
    }

    /**
     * Adds a tile to the zone.
     * * <b>Pre-condition:</b> The tile t must not be null.
     * <b>Post-condition:</b> The tile is added to the end of the list. The size increases by 1.
     * * @param t The tile object to add.
     */
    public void addTile(Tile t) {
        // TODO: IMPLEMENT IT
    }

    /**
     * Removes the top/last tile from the zone (simulating a stack or queue).
     * * <b>Pre-condition:</b> The zone must not be empty.
     * <b>Post-condition:</b> The tile is removed from the list. The size decreases by 1.
     * * @return The tile that was removed.
     */
    public Tile removeTile() {
        return null;
    }

    /**
     * Returns a reference to the list of tiles.
     * * <b>Pre-condition:</b> The list is not empty (according to your spec).
     * <b>Post-condition:</b> Returns the direct reference (pointer) to the internal list.
     * * @return The ArrayList of tiles.
     */
    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    /**
     * Checks if the zone is empty.
     * @return true if size is 0, false otherwise.
     */
    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}