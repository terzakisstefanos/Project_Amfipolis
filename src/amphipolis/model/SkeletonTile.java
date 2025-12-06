package amphipolis.model;

/**
 * Represents a Skeleton tile.
 * Players try to match Upper and Lower parts to form complete skeletons.
 * Families score bonus points.
 */
public class SkeletonTile extends FindingTile {

    private final SkeletonType type;
    private final SkeletonPart part;

    /**
     * Constructor.
     * @param imagePath The path to the image.
     * @param type The size
     * @param part The body part
     */
    public SkeletonTile(String imagePath, SkeletonType type, SkeletonPart part) {
        super(imagePath);
        this.type = type;
        this.part = part;
    }

    public SkeletonType getType() {
        return type;
    }

    public SkeletonPart getPart() {
        return part;
    }

}