package amphipolis.model;

public class EntranceZone extends Zone {

    /**
     * The maximum number of landslide tiles allowed before the game ends.
     * Constant value: 16.
     */
    private final int MAX_CAPACITY = 16;

    /**
     * Checks if the entrance area is full.
     * <b>Pre-condition:</b> None.
     * <b>Post-condition:</b> Returns true if the number of tiles equals MAX_CAPACITY.
     * This signals the Controller to terminate the game.
     * * @return true if full, false otherwise.
     */
    public boolean isFull() {
        return false; // TODO: IMPLEMENT IT
    }
}