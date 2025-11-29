package amphipolis.model;

import amphipolis.controller.Controller;

/**
 * Represents the Digger character card.
 * Ability: The player takes up to 2 tiles from the SAME area they drew from earlier.
 */
public class Digger extends Character {

    public Digger() {
        super("Digger");
    }

    @Override
    public void useAbility(Player player, Controller controller) {
        Zone zone = player.getLastVisitedZone();
        assert zone != null;

        if (!zone.isEmpty()) {
            Tile drawnTile = zone.removeTile();
            player.addTile(drawnTile);
        }

        // Check again if not empty before asking for second tile
        if (!zone.isEmpty()) {
            if (controller.howmany() == 2) {
                Tile drawnTile = zone.removeTile();
                player.addTile(drawnTile);
            }
        }
        setUsed();
    }
}