package amphipolis.model;

/**
 * Represents an Amphora tile.
 * Amphoras score points based on how many DIFFERENT colors the player has collected.
 */
public class AmphoraTile extends FindingTile {

    private Color color;

    /**
     * Constructor.
     * @param imagePath The path to the image.
     * @param color The specific color of this amphora.
     */
    public AmphoraTile(String imagePath, Color color) {
        super(imagePath);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

}