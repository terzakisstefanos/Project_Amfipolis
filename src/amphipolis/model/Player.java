package amphipolis.model;

import java.util.ArrayList;

/**
 * Represents a player in the game.
 * Each player has a name, a score, an inventory of tiles, and a set of character cards.
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
