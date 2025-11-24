package amphipolis.model;

/**
 * Represents the Assistant character card.
 * Ability: The player takes 1 tile from any sorting area.
 */
public class Assistant extends Character {

    /**
     * Executes the Assistant's special ability.
     * <b>Post-condition:</b> The player adds 1 tile from a chosen zone to their inventory.
     * The character is marked as used.
     *
     * @param board The game board to take the tile from.
     * @param player The player performing the action.
     */
    @Override
    public void useAbility(Board board, Player player) {
        // TODO: IMPLEMENT IT
    }
}
