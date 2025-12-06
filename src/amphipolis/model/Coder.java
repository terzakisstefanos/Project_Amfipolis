package amphipolis.model;

import amphipolis.controller.Controller;

/**
 * Represents the Coder character card.
 * Ability: Allows the player to reserve an area to draw extra tiles in the next turn.
 */
public class Coder extends Character {
    public Coder() {
        super();
    }

    @Override
    public void useAbility(Player player, Controller controller) {
        Zone zone = controller.selectZone(null, true);
        player.setCoderReservedZone(zone);
        setUsed();
    }
}