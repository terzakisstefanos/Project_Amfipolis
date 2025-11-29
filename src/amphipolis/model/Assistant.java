package amphipolis.model;

import amphipolis.controller.Controller;

/**
 * Represents the Assistant character card.
 * Ability: The player takes 1 tile from any sorting area.
 */
public class Assistant extends Character {

    public Assistant() {
        super("Assistant");
    }

    @Override
    public void useAbility(Player player, Controller controller) {
        Zone zone = controller.selectZone(null, true);
        assert zone != null;
        if (!zone.isEmpty()) {
            Tile drawnTile = zone.removeTile();
            player.addTile(drawnTile);
            setUsed();
        }
    }
}