package amphipolis.model;

/**
 * Represents a Statue tile (Caryatid or Sphinx).
 * Statues score points based on who has the majority (most) of them.
 */
public class StatueTile extends FindingTile {

    private boolean isSphinx; // true = Sphinx, false = Caryatid

    /**
     * Constructor.
     * @param imagePath The path to the image.
     * @param isSphinx True if this is a Sphinx, False if it is a Caryatid.
     */
    public StatueTile(String imagePath, boolean isSphinx) {
        super(imagePath);
        this.isSphinx = isSphinx;
    }

    public boolean isSphinx() {
        return isSphinx;
    }

}