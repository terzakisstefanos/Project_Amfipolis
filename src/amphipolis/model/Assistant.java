package amphipolis.model;

/**
 * Represents the Assistant character card.
 * Ability: The player takes 1 tile from any sorting area.
 */
public class Assistant extends Character {

    /**
     * Executes the Assistant's special ability.
     * <b>Post-condition:</b> The player adds 1 tile from the selected zone to their inventory.
     * The character is marked as used.
     *
     * @param zone   The zone the player wants to draw from.
     * @param player The player performing the action.
     */
    public void useAbility(Board board, Player player) {
        if (board == null) {
            System.err.println("Error: No board provided for Assistant ability.");
            return;
        }

        // 1. Check if the zone has tiles
        if (!zone.isEmpty()) {

            // 2. Take the top tile (Last In, First Out)
            Tile drawnTile = zone.removeTile();

            // 3. Give it to the player
            player.addTile(drawnTile);

            // 4. Mark the card as used so it can't be used again
            setUsed();
        }
    }
}