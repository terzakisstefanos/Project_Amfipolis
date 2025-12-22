import amphipolis.model.*; // Import your game classes
import org.junit.Test;
import static org.junit.Assert.*;

public class PlayerTest {

    // --- MOSAIC TESTS ---

    @Test
    public void testMosaicScoring_FullSet() {
        Player p = new Player("TestPlayer");
        // Add 4 Green Mosaics (Should be 4 points)
        for (int i = 0; i < 4; i++) {
            p.addTile(new MosaicTile("path", Color.GREEN));
        }
        assertEquals("4 Mosaics of same color should give 4 points", 4, p.computePoints());
    }

    @Test
    public void testMosaicScoring_Leftovers() {
        Player p = new Player("TestPlayer");
        // Add 2 Green and 2 Red.
        p.addTile(new MosaicTile("path", Color.GREEN));
        p.addTile(new MosaicTile("path", Color.GREEN));
        p.addTile(new MosaicTile("path", Color.RED));
        p.addTile(new MosaicTile("path", Color.RED));
        assertEquals("4 mixed leftover mosaics should give 2 points", 2, p.computePoints());
    }

    // --- SKELETON TESTS ---

    @Test
    public void testSkeletonScoring_SingleCompleteBig() {
        Player p = new Player("TestPlayer");
        // 1 Complete Big Skeleton (Top + Bottom) = 1 point
        p.addTile(new SkeletonTile("path", SkeletonType.BIG, SkeletonPart.UPPER));
        p.addTile(new SkeletonTile("path", SkeletonType.BIG, SkeletonPart.LOWER));

        assertEquals("1 Complete Big Skeleton should be 1 point", 1, p.computePoints());
    }

    @Test
    public void testSkeletonScoring_FamilyBonus() {
        Player p = new Player("TestPlayer");
        // Family Requirement: 2 Complete Bigs + 1 Complete Small = 6 points

        // Add 2 Big Sets
        for(int i=0; i<2; i++) {
            p.addTile(new SkeletonTile("path", SkeletonType.BIG, SkeletonPart.UPPER));
            p.addTile(new SkeletonTile("path", SkeletonType.BIG, SkeletonPart.LOWER));
        }
        // Add 1 Small Set
        p.addTile(new SkeletonTile("path", SkeletonType.SMALL, SkeletonPart.UPPER));
        p.addTile(new SkeletonTile("path", SkeletonType.SMALL, SkeletonPart.LOWER));

        assertEquals("Family (2 Big + 1 Small) should give 6 points", 6, p.computePoints());
    }

    // --- AMPHORA TESTS ---

    @Test
    public void testAmphoraScoring_AllColors() {
        Player p = new Player("TestPlayer");
        // Add 1 of each color
        p.addTile(new AmphoraTile("path", Color.BLUE));
        p.addTile(new AmphoraTile("path", Color.BROWN));
        p.addTile(new AmphoraTile("path", Color.RED));
        p.addTile(new AmphoraTile("path", Color.GREEN));
        p.addTile(new AmphoraTile("path", Color.YELLOW));
        p.addTile(new AmphoraTile("path", Color.PURPLE));

        assertEquals("Set of 6 different amphoras should give 6 points", 6, p.computePoints());
    }

    @Test
    public void testAmphoraScoring_MinimumSet() {
        Player p = new Player("TestPlayer");
        // Add 3 different colors -> Should be 1 point
        p.addTile(new AmphoraTile("path", Color.BLUE));
        p.addTile(new AmphoraTile("path", Color.BROWN));
        p.addTile(new AmphoraTile("path", Color.RED));

        assertEquals("Set of 3 different amphoras should give 1 point", 1, p.computePoints());
    }
}