package amphipolis.model;

import java.util.ArrayList;

/**
 * Represents the Entrance Zone of the board, which acts as the game's timer.
 * Accepts Landslide tiles and triggers the end of the game when full.
 * <b>Invariant:</b> The number of tiles in this zone must never exceed MAX_CAPACITY (16).
 * <b>Invariant:</b> Only Landslide tiles should be placed in this zone (though technically it accepts generic Tiles).
 */
public class EntranceZone extends Zone {

    /**
     * The maximum number of landslide tiles allowed before the game ends.
     * Constant value: 16.
     */
    private final int MAX_CAPACITY = 16;

    /**
     * Checks if the entrance area is full.
     * <b>Post-condition:</b> Returns true if the number of tiles equals MAX_CAPACITY.
     * This signals the Controller to terminate the game.
     * * @return true if full, false otherwise.
     */
    public boolean isFull() {
        ArrayList<Tile> tiles = getTiles();
        int count = 0;

        for (Tile t : tiles) {
            if (t.getClass() == LandslideTile.class) {
                count++;
            }
        }

        return count >= MAX_CAPACITY;
    }
}