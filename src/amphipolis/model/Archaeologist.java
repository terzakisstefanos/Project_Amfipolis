package amphipolis.model;

import amphipolis.controller.Controller;

/**
 * Represents the Archaeologist character card.
 * Ability: The player takes up to 2 tiles from any area, EXCEPT the one they drew from earlier.
 */
public class Archaeologist extends Character {

    public Archaeologist() {
        super("Archaeologist");
    }

    @Override
    public void useAbility(Player player, Controller controller) {
        // 1. Ask player to pick ONE zone (excluding the last visited one)
        Zone zone = controller.selectZone(player.getLastVisitedZone(), false);

        // 2. If a valid zone is selected and not empty
        if (zone != null && !zone.isEmpty()) {

            // Take the first tile
            Tile drawnTile1 = zone.removeTile();
            player.addTile(drawnTile1);

            // Check if they want a second one AND if there are tiles left in THAT SAME zone
            if (!zone.isEmpty()) {
                if (controller.howmany() == 2) {
                    Tile drawnTile2 = zone.removeTile();
                    player.addTile(drawnTile2);
                }
            }

            setUsed();
        }
    }
}