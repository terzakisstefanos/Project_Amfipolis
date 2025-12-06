package amphipolis.model;

import java.util.ArrayList;

/**
 * Represents a generic area on the board where tiles can be placed and drawn.
 * Functions as a dynamic collection of tiles.
 * <b>Invariant:</b> The internal list of tiles is never null.
 * <b>Invariant:</b> The zone stores generic Tile objects, allowing for polymorphism
 */
public class Zone implements java.io.Serializable{

    /**
     * The list of tiles currently in this zone.
     */
    private final ArrayList<Tile> tiles;

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
        if (t != null) {
            tiles.add(t);
        }
    }

    /**
     * Removes the top/last tile from the zone.
     * * <b>Pre-condition:</b> The zone must not be empty.
     * <b>Post-condition:</b> The tile is removed from the list. The size decreases by 1.
     * * @return The tile that was removed.
     */
    public Tile removeTile() {
        if (isEmpty()) {
            return null;
        }
        return tiles.remove(tiles.size()-1);
    }

    /**
     * Returns a reference to the list of tiles.
     * * <b>Pre-condition:</b> The list is not empty .
     * <b>Post-condition:</b> Returns the direct reference to the internal list.
     * * @return The ArrayList of tiles.
     */
    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    /**
     * Checks if the zone is empty.
     *
     * @return true if size is 0, false otherwise.
     */
    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}