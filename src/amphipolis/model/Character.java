package amphipolis.model;

import amphipolis.controller.Controller;

/**
 * Abstract class representing a Character card owned by a player.
 * Defines the common state (name, usage status) and the abstract interface for special abilities.
 * <b>Invariant:</b> A character belongs to exactly one player.
 * <b>Invariant:</b> A character can be in only one of two states: "Active" or "Used". Once used, it cannot return to active status in the same game.
 */
public abstract class Character implements java.io.Serializable{

    private String name;
    private boolean isUsed;

    public Character(String name) {
            this.name=name;
    }

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
     * @param player The player using the card (recipient of tiles).
     * @param controller The controller managing the game flow.
     */
    public abstract void useAbility(Player player, Controller controller);

}