package amphipolis.model;

import amphipolis.controller.Controller;

/**
 * Represents the Archaeologist character card.
 * Ability: The player takes up to 2 tiles from any area, EXCEPT the one they drew from earlier.
 */
public class Archaeologist extends Character {

    @Override
    public void useAbility(Player player) {
        Zone zone = Controller.selectZone(player.getLastVisitedZone(), false);
        assert zone != null;
        if (!zone.isEmpty()) {
            Tile drawnTile = zone.removeTile();
            player.addTile(drawnTile);
        }
        if (Controller.howmany() == 2) {
            if (!zone.isEmpty()) {// check again because it is possible that only one tile exists
                Tile drawnTile = zone.removeTile();
                player.addTile(drawnTile);
            }
        }
        setUsed();
    }
}
