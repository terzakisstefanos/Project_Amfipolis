package amphipolis.model;

import java.util.ArrayList;

/**
 * Represents a single player in the game.
 * Maintains the state of the user, including their personal inventory of collected tiles,
 * their assigned character cards, and their current score.
 *
 * <b>Invariant:</b> The player's score must always be non-negative.
 * <b>Invariant:</b> The player owns exactly 5 Character cards of a specific color.
 * <b>Invariant:</b> The collected tiles list is never null (though it may be empty).
 */
public class Player {

    private String name;
    private int score;
    private ArrayList<Tile> collectedTiles;
    private Character[] myCharacters;

    /**
     * Constructor for the Player.
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.collectedTiles = new ArrayList<>();
        this.myCharacters = new Character[5];
    }

    /**
     * Calculates the total score based on collected tiles.
     * <b>Post-condition:</b> Updates the score variable.
     * @return The calculated score.
     */
    public int computePoints() {
        return 0; // Logic to be implemented in Phase B
    }

    /**
     * Adds a tile to the player's collection.
     * @param t The tile to add.
     */
    public void addTile(Tile t) {
        // Logic to add tile
    }
}
