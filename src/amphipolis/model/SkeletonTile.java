package amphipolis.model;

/**
 * Represents a Skeleton tile.
 * Players try to match Upper and Lower parts to form complete skeletons.
 * Families (2 Big + 1 Small) score bonus points.
 */
public class SkeletonTile extends FindingTile {

    private SkeletonType type;
    private SkeletonPart part;

    /**
     * Constructor.
     * @param imagePath The path to the image.
     * @param type The size (BIG/SMALL).
     * @param part The body part (UPPER/LOWER).
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