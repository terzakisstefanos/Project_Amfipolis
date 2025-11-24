package amphipolis.model;

import java.util.ArrayList;
/**
 * Represents the bag containing all unassigned tiles in the game.
 * Responsible for securely storing tiles and providing them randomly to players.
 *
 * <b>Invariant:</b> The number of tiles in the bag must always be non-negative.
 * <b>Invariant:</b> The bag is initialized with a specific fixed set of tiles as defined by the game rules.
 */
public class Bag {

    private ArrayList<Tile> contents;

    /**
     * Constructs the bag and initializes it with all game tiles.
     * * <b>Post-condition:</b> The bag contains exactly the number of tiles specified in the rules.
     */
    public Bag() {
        // TODO: IMPLEMENT IT
    }

    /**
     * Removes and returns a random tile from the bag.
     * * <b>Pre-condition:</b> The bag must not be empty.
     * <b>Post-condition:</b> The size of the bag decreases by 1.
     * * @return A random Tile object.
     */
    public Tile drawRandomTile() {
        return null; // TODO: IMPLEMENT IT
    }

    /**
     * Checks if the bag is empty.
     * @return true if no tiles remain, false otherwise.
     */
    public boolean isEmpty() {
        return false;
    }
}