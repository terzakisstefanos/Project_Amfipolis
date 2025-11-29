package amphipolis.model;

import amphipolis.controller.Controller;

/**
 * Represents the Digger character card.
 * Ability: The player takes up to 2 tiles from the SAME area they drew from earlier.
 */
public class Digger extends Character {

    @Override
    public void useAbility(Player player) {
        Zone zone=player.getLastVisitedZone();
        assert zone != null;
        if (!zone.isEmpty()) {
            Tile drawnTile = zone.removeTile();
            player.addTile(drawnTile);
        }
        if (Controller.howmany() == 2) {
            if (!zone.isEmpty()) {
                Tile drawnTile = zone.removeTile();
                player.addTile(drawnTile);
            }
        }
        setUsed();
    }
}