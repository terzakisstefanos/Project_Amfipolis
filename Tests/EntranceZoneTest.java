import amphipolis.model.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class EntranceZoneTest {

    @Test
    public void testCapacityLimit() {
        EntranceZone zone = new EntranceZone();
        // Fill with 15 Landslide tiles (Capacity is 16)
        for (int i = 0; i < 15; i++) {
            zone.addTile(new LandslideTile("path"));
        }

        assertFalse("Zone should not be full", zone.isFull());

        // Add the 16th tile
        zone.addTile(new LandslideTile("path"));

        assertTrue("Zone should be full", zone.isFull());
    }

    @Test
    public void testIgnoreNonLandslideTiles() {
        EntranceZone zone = new EntranceZone();

        // Add 15 Landslide tiles
        for (int i = 0; i < 15; i++) {
            zone.addTile(new LandslideTile("path"));
        }
        // Add a random Mosaic tile
        zone.addTile(new MosaicTile("path", Color.RED));

        assertFalse("Zone should ignore non-Landslide tiles for capacity check", zone.isFull());
    }
}