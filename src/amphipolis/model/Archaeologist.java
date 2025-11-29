package amphipolis.model;

import amphipolis.controller.Controller;

import java.util.ArrayList;

/**
 * Represents the Archaeologist character card.
 * Ability: The player takes up to 2 tiles from any area, EXCEPT the one they drew from earlier.
 */
public class Archaeologist extends Character {

    @Override
    public void useAbility(Board board, Player player) {
        ArrayList<Zone> zones = new ArrayList<>();
        zones.add(board.getMosaicZone());
        zones.add(board.getAmphoraZone());
        zones.add(board.getSkeletonZone());
        zones.add(board.getStatueZone());
        Zone forbidden = player.getLastVisitedZone();
        for (Zone z : zones) {
            if (z != forbidden && !z.isEmpty()) {
                Tile t = z.removeTile();
                player.addTile(t);
        if (Controller.howmany() == 2) {
            if (!z.isEmpty()) {// check again because it is possible that only one tile exists
                Tile drawnTile = z.removeTile();
                player.addTile(drawnTile);
            }
        }
            }
        }
        setUsed();
    }
}
