import amphipolis.model.*; // Import your game classes
import org.junit.Test;
import static org.junit.Assert.*;

public class BagTest {

    @Test
    public void testBagInitialization() {
        Bag bag = new Bag();
        assertFalse("New bag should not be empty", bag.isEmpty());
    }

    @Test
    public void testDrawRandomTile() {
        Bag bag = new Bag();
        Tile t = bag.drawRandomTile();
        assertNotNull("Drawing from a full bag should return a tile", t);
    }

    @Test
    public void testBagDepletion() {
        Bag bag = new Bag();

        // Draw all tiles until empty.
        int count = 0;
        while (!bag.isEmpty()) {
            Tile t = bag.drawRandomTile();
            assertNotNull("Tile should not be null if bag is not empty", t);
            count++;
        }

        // Verify it is empty
        assertTrue("Bag should be empty after drawing all tiles", bag.isEmpty());

        // Verify drawing from empty bag is safe (returns null)
        assertNull("Drawing from empty bag should return null", bag.drawRandomTile());

        System.out.println("Test drew " + count + " tiles total.");
    }
}