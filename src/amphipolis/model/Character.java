package amphipolis.model;

public abstract class Character {

    private String name;
    private boolean isUsed;

    /**
     * Checks if the character ability has already been used in this game.
     * @return true if used, false otherwise.
     */
    public boolean getIsUsed() {
        return isUsed;
    }

    /**
     * Marks the character card as used.
     * <b>Post-condition:</b> isUsed becomes true.
     */
    public void setUsed() {
        this.isUsed = true;
    }

    /**
     * Abstract method that executes the character's special ability.
     *
     * @param board The game board (source of tiles).
     * @param player The player using the card (recipient of tiles).
     */
    public abstract void useAbility(Board board, Player player);
}