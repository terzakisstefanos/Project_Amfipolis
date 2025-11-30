package amphipolis.model;

/**
 * Abstract base class for all game tiles (Landslide and Finding tiles).
 * Defines the essential properties shared by all tiles, such as the visual asset path.
 *
 * <b>Invariant:</b> The image path string must not be null or empty.
 * <b>Invariant:</b> Every concrete subclass must implement a point calculation strategy.
 */
public abstract class Tile implements java.io.Serializable {
    private String imagePath;

    /**
     * Constructor for the Tile class.
     *
     * @param imagePath The relative path to the image file (e.g., "images/red_mosaic.png").
     */
    public Tile(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Gets the image path for this tile.
     *
     * @return A String representing the file path.
     */
    public String getImagePath() {
        return imagePath;
    }

    ;
}


