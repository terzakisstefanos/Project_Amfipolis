package amphipolis.controller;

import amphipolis.model.*;
import amphipolis.view.GameView;

import java.util.ArrayList;

/**
 * The Controller class acts as the central coordinator (Brain) of the MVC architecture.
 * It manages the game loop, enforces rules (turn order, landslide mechanics), and synchronizes
 * the Model state with the View.
 * <b>Invariant:</b> There is always exactly one active player during a turn.
 * <b>Invariant:</b> The game state (playing vs. finished) is explicitly tracked.
 * <b>Invariant:</b> The Controller maintains a valid reference to the Board and the Players throughout the game lifecycle.
 */
public class Controller {

    private ArrayList<Player> players;
    private int currentPlayerIndex;
    private Bag bag;
    private Board board;
    private boolean gameFinished;
    private GameView view;
    private Player thief;
    private boolean isSinglePlayer;


    /**
     * Starts the game setup process.
     * <b>Pre-condition:</b> The game must not already be running (gameFinished should be true or uninitialized).
     * <b>Post-condition:</b> Initializes Players, Board, Bag, and starts the first turn.
     */
    public void startGame() {
        // TODO: IMPLEMENT Logic to init everything
    }

    /**
     * Begins a new turn for the current player.
     * <b>Pre-condition:</b> A turn is not currently in progress (the previous player must have finished).
     * <b>Post-condition:</b> The current player draws 4 tiles. If a Landslide occurs, handleLandslide is called.
     */
    public void startTurn() {
        // TODO: IMPLEMENT IT
        // 1. Identify current player
        // 2. Draw 4 tiles from Bag
        // 3. Loop through tiles:
        //    if (tile instanceof LandslideTile) -> handleLandslide((LandslideTile) tile)
        //    else -> Place in correct Board Zone
    }

    /**
     * Ends the current player's turn manually.
     * <b>Pre-condition:</b> The player must be in the "Action Phase" of their turn.
     * <b>Post-condition:</b> Control passes to the next player index. startTurn() is called for them.
     */
    public void endTurn() {
        // TODO: IMPLEMENT IT currentPlayerIndex++ (wrap around to 0)
    }

    /**
     * Handles the special logic when a Landslide Tile is drawn.
     * * @param t The landslide tile that was drawn.
     */
    private void handleLandslide(LandslideTile t) {
        // TODO: IMPLEMENT IT
        // 1. Add t to board.getEntranceZone()
        // 2. Call checkGameOver(board.getEntranceZone())
        // 3. FORCE END TURN (Player loses their action phase)
    }

    /**
     * Checks if the game should end based on the Entrance Zone status.
     * * @param zone The entrance zone to check.
     * <b>Post-condition:</b> If zone.isFull() is true, gameFinished becomes true and winners are calculated.
     */
    private void checkGameOver(EntranceZone zone) {
        if (zone.isFull()) {
            // TODO: IMPLEMENT Calculate scores and declare winner
        }
    }

    /**
     * Saves the current game state to a file.
     * <b>Pre-condition:</b> The game is currently valid/running.
     * <b>Post-condition:</b> A file is created at the specified path containing the serialized objects.
     * * @param filePath The location to save the file.
     */
    public void saveGame(String filePath) {
        //TODO: Use ObjectOutputStream to write 'this' or specific objects
    }

    /**
     * Loads a game from a file.
     * <b>Pre-condition:</b> A valid save file exists at filePath.
     * <b>Post-condition:</b> The Controller's state (players, board) is replaced with the data from the file.
     * * @param filePath The location of the save file.
     */
    public void loadGame(String filePath) {
        //TODO: Use ObjectInputStream to read data
    }
    /**
     * Prompts the user to select a zone via the View.
     * @param forbiddenZone The zone the player visited previously (can be null).
     * @param ignoreForbidden If true, the forbiddenZone restriction is ignored (e.g. Assistant).
     * @return The selected Zone object.
     */
    public Zone selectZone(Zone forbiddenZone, boolean ignoreForbidden) {
        Zone selectedZone = null;
        boolean validSelection = false;

        while (!validSelection) {
            int choice = view.promptZoneSelection();
            switch (choice) {
                case 0: selectedZone = board.getMosaicZone(); break;
                case 1: selectedZone = board.getAmphoraZone(); break;
                case 2: selectedZone = board.getSkeletonZone(); break;
                case 3: selectedZone = board.getStatueZone(); break;
                default: return null;
            }
            if (!ignoreForbidden && selectedZone != null && selectedZone == forbiddenZone) {
                view.showErrorMessage("You cannot select this Zone again this turn");
            } else {
                validSelection = true;
            }
        }

        return selectedZone;
    }
    /**
     * Prompts the user to select how many tiles they want to draw.
     * typically used for the Digger character.
     *
     * @return The number of tiles selected by the user (1 or 2).
     */
    public  int howmany() {
        return view.promptTileCount();
    }

    public Board getBoard() {
        return board;
    }
}