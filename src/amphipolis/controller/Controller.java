package amphipolis.controller;

import amphipolis.model.*;
import amphipolis.model.Character;
import amphipolis.view.GameView;
import java.io.*;
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
        //todo when we create the players we need to ask for their name maybe
        this.view = new GameView(this);
        boolean wantToLoad = view.promptLoadGame();
        if (wantToLoad) {
            int loadType = view.promptLoadType(); // 0 = Last Saved, 1 = Custom File

            String path = null;
            if (loadType == 0) {
                path = "last_save.ser";
            } else if (loadType == 1) {
                path = view.promptFileSelection();
            }

            if (path != null) {
                loadGame(path);
                view.updateView();
                return;
            } else {
                view.showMessage("No file selected. Starting new game.");
            }
        }
        this.bag = new Bag();
        this.board = new Board();
        this.board.init();
        this.gameFinished = false;
        this.players = new ArrayList<>();
        int numPlayers = view.promptPlayerCount();
        this.isSinglePlayer = (numPlayers == 1);
        for (int i = 1; i <= numPlayers; i++) {
            players.add(new Player("Player " + i));
        }
        if (isSinglePlayer) {
            this.thief = new Player("Thief");
            players.add(thief);
        }
        this.currentPlayerIndex = 0;
        view.updateView();
        startTurn();
    }

    /**
     * Begins a new turn for the current player.
     * <b>Pre-condition:</b> A turn is not currently in progress (the previous player must have finished).
     * <b>Post-condition:</b> The current player draws 4 tiles. If a Landslide occurs, handleLandslide is called.
     */
    public void startTurn() {
        Player current = players.get(currentPlayerIndex);
        for (int i = 0; i < 4; i++) {
            Tile tile = bag.drawRandomTile();

            if (tile != null) {
                if (tile.getClass() == MosaicTile.class) {
                    board.getMosaicZone().addTile(tile);
                } else if (tile.getClass() == AmphoraTile.class) {
                    board.getAmphoraZone().addTile(tile);
                } else if (tile.getClass() == SkeletonTile.class) {
                    board.getSkeletonZone().addTile(tile);
                } else if (tile.getClass() == StatueTile.class) {
                    board.getStatueZone().addTile(tile);
                } else if (tile.getClass() == LandslideTile.class) {
                    handleLandslide((LandslideTile) tile);
                }
            }
        }
        Zone zone = selectZone(null, true);
        current.setLastVisitedZone(zone);
        assert zone != null;
        if (!zone.isEmpty()) {
            Tile drawnTile = zone.removeTile();
            players.get(currentPlayerIndex).addTile(drawnTile);
        }
        // Check again if not empty before asking for second tile
        if (!zone.isEmpty()) {
            Tile drawnTile = zone.removeTile();
            players.get(currentPlayerIndex).addTile(drawnTile);
        }

        view.updateView(); // update the view
        if (view.promptUseCharacter()) {
            boolean validCharacterSelected = false;

            while (!validCharacterSelected) {
                int charIndex = view.promptCharacterSelection();
                if (charIndex == -1) {
                    break;
                }
                Character chosenChar = current.getCharacters()[charIndex];// select the chosen character
                if (!chosenChar.getIsUsed()) {
                    chosenChar.useAbility(current, this);
                    validCharacterSelected = true;
                    view.updateView();
                } else {
                    view.showErrorMessage("You have already used this character!");
                }
            }
        }
        view.updateView(); // update again
        endTurn();
    }

    /**
     * Ends the current player's turn manually.
     * <b>Pre-condition:</b> The player must be in the "Action Phase" of their turn.
     * <b>Post-condition:</b> Control passes to the next player index. startTurn() is called for them.
     */
    public void endTurn() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0;
        }
        startTurn();
    }

    /**
     * Handles the special logic when a Landslide Tile is drawn.
     * * @param t The landslide tile that was drawn.
     */
    private void handleLandslide(LandslideTile t) {
        board.getEntranceZone().addTile(t);
        checkGameOver(board.getEntranceZone());
        endTurn();
    }

    /**
     * Checks if the game should end based on the Entrance Zone status.
     * * @param zone The entrance zone to check.
     * <b>Post-condition:</b> If zone.isFull() is true, gameFinished becomes true and winners are calculated.
     */
    /**
     * Checks if the game should end based on the Entrance Zone status.
     * @param zone The entrance zone to check.
     * <b>Post-condition:</b> If zone.isFull() is true, gameFinished becomes true and the GUI displays the results.
     */
    private void checkGameOver(EntranceZone zone) {
        if (zone.isFull()) {
            this.gameFinished = true; // Set flag to stop further moves

            // 1. Prepare the output String for the GUI
            StringBuilder scoreboard = new StringBuilder();
            scoreboard.append("GAME OVER!\n\n");
            scoreboard.append("Final Scores:\n");
            scoreboard.append("-----------------\n");

            int maxScore = -1;
            ArrayList<Player> winners = new ArrayList<>();

            // 2. Calculate scores for everyone (including Thief if present)
            for (Player p : players) {
                int score = p.computePoints(); // Calculate final score
                scoreboard.append(p.getName()).append(": ").append(score).append("\n");

                // Determine winner(s)
                if (score > maxScore) {
                    maxScore = score;
                    winners.clear();
                    winners.add(p);
                } else if (score == maxScore) {
                    winners.add(p);
                }
            }

            scoreboard.append("-----------------\n\n");

            // 3. Add the winner announcement
            if (winners.size() == 1) {
                scoreboard.append("WINNER: ").append(winners.get(0).getName());
            } else {
                scoreboard.append("TIE BETWEEN: ");
                for (int i = 0; i < winners.size(); i++) {
                    scoreboard.append(winners.get(i).getName());
                    if (i < winners.size() - 1) scoreboard.append(", ");
                }
            }

            // 4. Send the final string to the View to display in a popup/dialog
            view.showMessage(scoreboard.toString());
        }
    }

    /**
     * Saves the current game state to a file using Java Serialization.
     * @param filePath The location to save the file (e.g., "saved_game.ser").
     */
    public void saveGame(String filePath) {
        // Ensure the file has a proper extension
        if (!filePath.endsWith(".ser")) {
            filePath += ".ser";
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            // Write all necessary state objects in a specific order
            out.writeObject(players);
            out.writeInt(currentPlayerIndex);
            out.writeObject(bag);
            out.writeObject(board);
            out.writeBoolean(gameFinished);
            out.writeObject(thief);
            out.writeBoolean(isSinglePlayer);

            // Note: We DO NOT save the 'view' because GUI components are not serializable.
            // We only save the data (Model).

            view.showMessage("Game saved successfully to " + filePath);

        } catch (IOException i) {
            view.showErrorMessage("Failed to save game: " + i.getMessage());
            i.printStackTrace();
        }
    }

    /**
     * Loads a game state from a file and restores the application state.
     * @param filePath The location of the save file.
     */
    public void loadGame(String filePath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {

            // Read the objects in the EXACT SAME ORDER they were written
            this.players = (ArrayList<Player>) in.readObject();
            this.currentPlayerIndex = in.readInt();
            this.bag = (Bag) in.readObject();
            this.board = (Board) in.readObject();
            this.gameFinished = in.readBoolean();
            this.thief = (Player) in.readObject();
            this.isSinglePlayer = in.readBoolean();

            // After loading the raw data, we must tell the View to repaint itself
            // based on the new Board and Player objects we just loaded.
            view.showMessage("Game loaded successfully!");
            view.updateView();

        } catch (IOException i) {
            view.showErrorMessage("Could not load file: " + i.getMessage());
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            view.showErrorMessage("Game file is corrupted or incompatible.");
            c.printStackTrace();
        }
    }

    /**
     * Prompts the user to select a zone via the View.
     *
     * @param forbiddenZone   The zone the player visited previously (can be null).
     * @param ignoreForbidden If true, the forbiddenZone restriction is ignored (e.g. Assistant).
     * @return The selected Zone object.
     */
    public Zone selectZone(Zone forbiddenZone, boolean ignoreForbidden) {
        Zone selectedZone = null;
        boolean validSelection = false;

        while (!validSelection) {
            int choice = view.promptZoneSelection();
            switch (choice) {
                case 0:
                    selectedZone = board.getMosaicZone();
                    break;
                case 1:
                    selectedZone = board.getAmphoraZone();
                    break;
                case 2:
                    selectedZone = board.getSkeletonZone();
                    break;
                case 3:
                    selectedZone = board.getStatueZone();
                    break;
                default:
                    return null;
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
    public int howmany() {
        return view.promptTileCount();
    }

    public Board getBoard() {
        return board;
    }
}