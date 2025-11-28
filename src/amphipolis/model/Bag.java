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
        for(int i=0; i<24;i++){
            contents.add(new LandslideTile("images/landslide.png"));
        }
        for(int i=0; i<9;i++) {
            contents.add(new MosaicTile("images/mosaic_green.png",Color.GREEN));
            contents.add(new MosaicTile("images/mosaic_red.png",Color.RED));
            contents.add(new MosaicTile("images/mosaic_yellow.png",Color.YELLOW));
        }
        for(int i=0; i<12; i++){
            contents.add(new StatueTile("images/sphinx.png",true));
            contents.add(new StatueTile("images/caryatid.png",false));
        }
        for (Color c : Color.values()) {
            for (int i = 0; i < 5; i++) {
                // Dynamically creating the path: "images/amphora_blue.png", etc.
                String path = "images/amphora_" + c.toString().toLowerCase() + ".png";
                contents.add(new AmphoraTile(path, c));
            }
        }
        for (int i = 0; i < 10; i++) {
            contents.add(new SkeletonTile("images/skeleton_big_top.png", SkeletonType.BIG, SkeletonPart.UPPER));
            contents.add(new SkeletonTile("images/skeleton_big_bottom.png", SkeletonType.BIG, SkeletonPart.LOWER));
        }
        for (int i = 0; i < 5; i++) {
            contents.add(new SkeletonTile("images/skeleton_small_top.png", SkeletonType.SMALL, SkeletonPart.UPPER));
            contents.add(new SkeletonTile("images/skeleton_small_bottom.png", SkeletonType.SMALL, SkeletonPart.LOWER));
        }
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