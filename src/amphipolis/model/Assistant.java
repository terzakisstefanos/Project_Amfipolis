package amphipolis.model;

import amphipolis.controller.Controller;
import amphipolis.controller.Controller.*;

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
     * @param player The player performing the action.
     */
    public void useAbility(Player player) {
        Zone zone = Controller.selectZone(null, true);
        assert zone != null;
        if (!zone.isEmpty()) {
            Tile drawnTile = zone.removeTile();
            player.addTile(drawnTile);
            setUsed();
        }
    }
}