package amphipolis.model;

/**
 * Abstract class representing tiles that the player can collect (Mosaics, Statues, etc.).
 * These are distinct from Landslide tiles because they score points.
 */
public abstract class FindingTile extends Tile {
    public FindingTile(String imagePath) {
        super(imagePath);
    }
}