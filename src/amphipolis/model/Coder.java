package amphipolis.model;

import amphipolis.controller.Controller;

/**
 * Represents the Coder (Programmer) character card.
 * Ability: Allows the player to reserve an area to draw extra tiles in the next turn.
 */
public class Coder extends Character {

    @Override
    public void useAbility(Player player) {
        Zone zone = Controller.selectZone(null, true);
        player.setCoderReservedZone(zone);

    }
}