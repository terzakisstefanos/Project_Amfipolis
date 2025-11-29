package amphipolis.model;

import amphipolis.controller.Controller;

/**
 * Represents the Professor character card.
 * Ability: The player takes 1 tile from every area (Mosaic, Statue, Skeleton, Amphora),
 * excluding the area they already visited this turn.
 */
public class Professor extends Character {

    @Override
    public void useAbility(Player player) {
        Zone zone = Controller.selectZone(player.getLastVisitedZone(), false);
        assert zone != null;
        if (!zone.isEmpty()) {
            Tile drawnTile = zone.removeTile();
            player.addTile(drawnTile);
            setUsed();
        }
    }
}