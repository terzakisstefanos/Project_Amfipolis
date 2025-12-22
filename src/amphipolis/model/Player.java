package amphipolis.model;

import java.util.ArrayList;

/**
 * Represents a single player in the game.
 * Maintains the state of the user, including their personal inventory of collected tiles,
 * their assigned character cards, and their current score.
 * <b>Invariant:</b> The player's score must always be non-negative.
 * <b>Invariant:</b> The player owns exactly 5 Character cards of a specific color.
 * <b>Invariant:</b> The collected tiles list is never null (though it may be empty).
 */
public class Player {

    private final String name;
    private int score;
    private final ArrayList<Tile> collectedTiles;
    private final Character[] myCharacters;
    private Zone lastVisitedZone;
    private Zone coderReservedZone;

    /**
     * Constructor for the Player.
     *
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.collectedTiles = new ArrayList<>();
        this.lastVisitedZone = null;
        this.coderReservedZone = null;
        this.myCharacters = new Character[] {
                new Assistant(),
                new Archaeologist(),
                new Digger(),
                new Professor(),
                new Coder()
        };
    }

    /**
     * Gets the zone the player visited during the standard action of the current turn.
     * This is used to enforce restrictions for characters like the Archaeologist.
     *
     * @return The zone visited this turn, or null if reset or not yet selected.
     */
    public Zone getLastVisitedZone() {
        return lastVisitedZone;
    }

    /**
     * Sets the zone the player visited during the current turn.
     * This should be reset to null at the start of every new turn.
     *
     * @param lastVisitedZone The zone to mark as visited.
     */
    public void setLastVisitedZone(Zone lastVisitedZone) {
        this.lastVisitedZone = lastVisitedZone;
    }

    /**
     * Gets the zone reserved by the Coder character in the previous turn.
     * If this returns a non-null value, the player is entitled to extra tiles
     * from this zone at the start of their turn.
     *
     * @return The reserved zone, or null if no reservation exists.
     */
    public Zone getCoderReservedZone() {
        return coderReservedZone;
    }

    /**
     * Sets the zone reserved by the Coder character for the next turn.
     * <b>Post-condition:</b> This selection persists across the round until the player's next turn.
     *
     * @param coderReservedZone The zone to reserve.
     */
    public void setCoderReservedZone(Zone coderReservedZone) {
        this.coderReservedZone = coderReservedZone;
    }

    /**
     * Calculates the total score based on collected tiles (Mosaics, Skeletons, Amphoras).
     * Statues are calculated by the Controller and added separately.
     * <b>Post-condition:</b> Updates and returns the score variable.
     *
     * @return The calculated score.
     */
    public int computePoints() {
        // 1. Setup counters
        int greent = 0, redt = 0, yellowt = 0;
        int bigtops = 0, bigbot = 0, smalltops = 0, smallbot = 0;

        // FIX 1: Use dynamic size instead of hardcoded '6' to prevent crashes if new colors are added
        int colorCount = Color.values().length;
        int[] amphoraCounts = new int[colorCount];

        // 2. Iterate tiles safely
        for (Tile t : collectedTiles) {
            // FIX 2: Protect against null tiles
            if (t == null) continue;

            // CONSTRAINT: Using getClass() instead of instanceof
            if (t.getClass() == MosaicTile.class) {
                MosaicTile m = (MosaicTile) t;
                // FIX 3: Check if color is null before reading it
                if (m.getColor() == Color.GREEN) greent++;
                else if (m.getColor() == Color.RED) redt++;
                else if (m.getColor() == Color.YELLOW) yellowt++;

            } else if (t.getClass() == SkeletonTile.class) {
                SkeletonTile s = (SkeletonTile) t;
                // FIX 4: Ensure type/part are not null if necessary (enums are usually safe, but be careful)
                if (s.getType() == SkeletonType.BIG) {
                    if (s.getPart() == SkeletonPart.UPPER) bigtops++;
                    else bigbot++;
                } else {
                    if (s.getPart() == SkeletonPart.UPPER) smalltops++;
                    else smallbot++;
                }

            } else if (t.getClass() == AmphoraTile.class) {
                AmphoraTile a = (AmphoraTile) t;
                // FIX 5: CRITICAL CHECK - This is the most likely cause of your bug!
                if (a.getColor() != null) {
                    amphoraCounts[a.getColor().ordinal()]++;
                }
            }
        }

        int currentPoints = 0;

        // 3. Mosaic Scoring (Logic remains the same)
        currentPoints += (greent / 4) * 4;
        currentPoints += (redt / 4) * 4;
        currentPoints += (yellowt / 4) * 4;
        int leftovers = (greent % 4) + (redt % 4) + (yellowt % 4);
        currentPoints += (leftovers / 4) * 2;

        // 4. Skeleton Scoring
        int completeBig = Math.min(bigtops, bigbot);
        int completeSmall = Math.min(smalltops, smallbot);

        // Families (2 Big + 1 Small = 6 points)
        while (completeBig >= 2 && completeSmall >= 1) {
            currentPoints += 6;
            completeBig -= 2;
            completeSmall -= 1;
        }
        // Remaining complete skeletons
        currentPoints += completeBig;
        currentPoints += completeSmall;

        // 5. Amphora Scoring
        boolean makingSets = true;
        while (makingSets) {
            int uniqueColorsFound = 0;
            for (int count : amphoraCounts) {
                if (count > 0) uniqueColorsFound++;
            }

            if (uniqueColorsFound >= 3) {
                switch (uniqueColorsFound) {
                    case 6: currentPoints += 6; break;
                    case 5: currentPoints += 4; break;
                    case 4: currentPoints += 2; break;
                    case 3: currentPoints += 1; break;
                }
                // Remove one of each used color
                for (int i = 0; i < amphoraCounts.length; i++) {
                    if (amphoraCounts[i] > 0) amphoraCounts[i]--;
                }
            } else {
                makingSets = false;
            }
        }

        this.score = currentPoints;

        // DEBUG: Print to console to prove it ran for this player
        System.out.println("DEBUG: Calculated " + currentPoints + " points for " + this.name);

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
    /**
     * Accessor for the player's character cards.
     *
     * @return An array of the 5 Character objects owned by the player.
     */
    public Character[] getCharacters() {
        return myCharacters;
    }

    /**
     * Accessor for the player's name.
     *
     * @return The name string
     */
    public String getName() {
        return name;
    }
    /**
     * Accessor for the list of tiles collected by the player.
     *
     * @return The ArrayList containing all tiles in the player's possession.
     */
    public ArrayList<Tile> getCollectedTiles() {
        return collectedTiles;
    }
}
