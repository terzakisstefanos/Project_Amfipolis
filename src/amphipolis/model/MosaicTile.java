package amphipolis.model;

/**
 * Represents a Mosaic tile.
 * Mosaics have specific colors (Green, Red, Yellow) and score points based on sets.
 */
public class MosaicTile extends FindingTile {

    private Color color;

    /**
     * Constructor.
     * @param imagePath The path to the image.
     * @param color The specific color of this mosaic.
     */
    public MosaicTile(String imagePath, Color color) {
        super(imagePath);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

}