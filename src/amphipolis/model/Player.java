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
     *
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.collectedTiles = new ArrayList<>();
        this.myCharacters = new Character[5];
    }

    /**
     * Calculates the total score based on collected tiles (Mosaics, Skeletons, Amphoras).
     * Statues are calculated by the Controller and added separately.
     * <b>Post-condition:</b> Updates and returns the score variable.
     *
     * @return The calculated score.
     */
    public int computePoints() {
        int currentPoints = 0, greent = 0, redt = 0, yellowt = 0, bigtops = 0, bigbot = 0, smalltops = 0, smallbot = 0;
        int[] amphoraCounts = new int[6];
        for (Tile t : collectedTiles) {
            if (t.getClass() == MosaicTile.class) {
                MosaicTile m = (MosaicTile) t;
                if (m.getColor() == Color.GREEN) greent++;
                else if (m.getColor() == Color.RED) redt++;
                else if (m.getColor() == Color.YELLOW) yellowt++;
            }else if (t.getClass() == SkeletonTile.class) {
                SkeletonTile s = (SkeletonTile) t;
                if (s.getType() == SkeletonType.BIG) {
                    if (s.getPart() == SkeletonPart.UPPER) bigtops++;
                    else bigbot++;
                } else {
                    if (s.getPart() == SkeletonPart.UPPER) smalltops++;
                    else smallbot++;
                }
            }
            else if (t.getClass() == AmphoraTile.class) {
                AmphoraTile a = (AmphoraTile) t;
                amphoraCounts[a.getColor().ordinal()]++; // Use ordinal() to map enum color to an index (0-5)
            }
        }
        // Same color sets (4 points)
        currentPoints += (greent / 4) * 4;
        currentPoints += (redt / 4) * 4;
        currentPoints += (yellowt / 4) * 4;
        // Different color sets (2 points) from leftovers
        int leftovers = (greent % 4) + (redt % 4) + (yellowt % 4);
        currentPoints += (leftovers / 4) * 2;
        // Find complete skeletons (min of top and bottom)
        int completeBig = Math.min(bigtops, bigbot);
        int completeSmall = Math.min(smalltops, smallbot);

        // Find Families (2 Big + 1 Small = 6 points)
        while (completeBig >= 2 && completeSmall >= 1) {
            currentPoints += 6;
            completeBig -= 2;
            completeSmall -= 1;
        }

        // Remaining complete skeletons (1 point each)
        currentPoints += completeBig;
        currentPoints += completeSmall;
        boolean makingSets = true;
        while (makingSets) {
            int uniqueColorsFound = 0;
            for (int i = 0; i < 6; i++) {
                if (amphoraCounts[i] > 0) {
                    uniqueColorsFound++;
                }
            }

            // Calculate points based on set size
            if (uniqueColorsFound >= 3) {
                switch (uniqueColorsFound) {
                    case 6:
                        currentPoints += 6;
                        break;
                    case 5:
                        currentPoints += 4;
                        break;
                    case 4:
                        currentPoints += 2;
                        break;
                    case 3:
                        currentPoints += 1;
                        break;
                }
                for (int i = 0; i < 6; i++) {// remove the tiles that are used
                    if (amphoraCounts[i] > 0) {
                        amphoraCounts[i]--;
                    }
                }
            } else {
                makingSets = false; // not enough
            }
        }

        this.score = currentPoints;
        return currentPoints;
    }

    /**
     * Adds a tile to the player's collection.
     *
     * @param t The tile to add.
     */
    public void addTile(Tile t) {
        collectedTiles.add(t);
    }
}
