package amphipolis.model;

public class LandslideTile extends Tile {

    /**
     * Constructor.
     * @param imagePath The path to the landslide image.
     */
    public LandslideTile(String imagePath) {
        super(imagePath);
    }

    /**
     * Calculates points for this tile.
     * <b>Post-condition:</b> Always returns 0.
     * @return 0
     */
    @Override
    public int calculatePoints() {
        return 0;
    }
}
