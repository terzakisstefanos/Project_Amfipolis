package amphipolis.model;

import amphipolis.controller.Controller;

import java.util.ArrayList;

/**
 * Represents the Professor character card.
 * Ability: The player takes 1 tile from every area (Mosaic, Statue, Skeleton, Amphora),
 * excluding the area they already visited this turn.
 */
public class Professor extends Character {

    public Professor() {
        super("Professor");
    }

    @Override
    public void useAbility(Player player,Controller controller) {
        Board board=controller.getBoard();
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
            }
        }
        setUsed();
    }
}