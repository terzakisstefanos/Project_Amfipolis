package amphipolis.controller;

import amphipolis.model.*;
import amphipolis.model.Character;
import amphipolis.view.GameView;

import javax.swing.Timer;
import java.util.*;
import java.io.*;
import javax.sound.sampled.*;

/**
 * The Controller class acts as the central coordinator of the MVC architecture.
 * It manages the game loop, enforces rules and synchronizes the Model state with the View.
 * <b>Invariant:</b> There is always exactly one active player during a turn.
 * <b>Invariant:</b> The game state is explicitly tracked.
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
    private Timer gameTimer;
    private int timeLeft;
    private final int TURN_DURATION = 30;
    private Clip musicClip;
    private boolean isMuted = false;
    private long clipTimePosition = 0;

    public Controller() {
    }

    // =============================================================
    // GAME FLOW
    // =============================================================

    /**
     * Starts the game setup process.
     * <b>Pre-condition:</b> The game must not already be running.
     * <b>Post-condition:</b> Initializes Players, Board, Bag, and starts the first turn.
     */
    public void startGame() {
        this.view = new GameView(this);
        view.setMuteButtonListener(e -> toggleMute());
        view.setSaveButtonListener(e -> {
            String path = view.promptSaveFilePath();
            if (path != null) {
                saveGame(path);
            }
        });
        view.setUseCharacterButtonListener(e -> useCharacter());

        view.setDebugEndButtonListener(e -> fillEntranceAndEnd());// delete

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
        int numPlayers = -1;
        while (numPlayers == -1) {
            numPlayers = view.promptPlayerCount();
        }
        numPlayers++;
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
        playMusicForPlayer(0);
        view.setDrawButtonListener(e -> {
            startTurn();
        });
        view.setEndTurnButtonListener(e -> {
            endTurn();
        });
    }

    /**
     * Begins a new turn for the current player.
     * <b>Pre-condition:</b> A turn is not currently in progress (the previous player must have finished).
     * <b>Post-condition:</b> The current player draws 4 tiles. If a Landslide occurs, handleLandslide is called.
     */
    public void startTurn() {
        if (gameFinished) {
            return;
        }
        Player current = players.get(currentPlayerIndex);
        if (current.getCoderReservedZone() != null) {// The coders action
            Zone reserved = current.getCoderReservedZone();
            if (!reserved.isEmpty()) {
                Tile extraTile = reserved.removeTile();
                current.addTile(extraTile);
                view.showMessage(current.getName() + " used the Coder's ability to get an extra tile.");
            } else {
                view.showMessage("The zone reserved by the Coder is empty! No extra tile drawn.");
            }
            current.setCoderReservedZone(null);// reset the reservation
            view.updateView();
        }
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
                    return;
                }
            }
        }
        startTimer();
        view.updateView();
        playMusicForPlayer(currentPlayerIndex);
        Zone zone = selectZone(null, true);
        current.setLastVisitedZone(zone);
        if (zone == null) {
            view.showErrorMessage("No zone selected. Ending turn.");
            view.updateView();
            endTurn();
            return;
        }
        if (!zone.isEmpty()) {
            Tile drawnTile = zone.removeTile();
            players.get(currentPlayerIndex).addTile(drawnTile);
        }
        // Check again if not empty before asking for second tile
        if (!zone.isEmpty()) {
            view.updateView();
            if (howmany() == 1) {
                Tile drawnTile2 = zone.removeTile();
                current.addTile(drawnTile2);
            }
        }

        view.updateView(); // update the view

    }

    /**
     * Ends the current player's turn manually.
     * <b>Pre-condition:</b> The player must be in the Action Phase of their turn.
     * <b>Post-condition:</b> Control passes to the next player index. startTurn() is called for them.
     */
    public void endTurn() {
        if (gameFinished) {
            return;
        }
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0;
        }
        if (!gameFinished) {
            startTurn();
        }
    }

    // =============================================================
    // GAME LOGIC
    // =============================================================

    /**
     * Handles the special logic when a Landslide Tile is drawn.
     *@param t The landslide tile that was drawn.
     */
    private void handleLandslide(LandslideTile t) {
        board.getEntranceZone().addTile(t);
        playLandslideSound();
        view.shakeWindow();
        view.showLandslideDialog();
        if (isSinglePlayer) {
            view.showMessage("The Thief steals all tiles from the board!");
            stealAllTiles(board.getMosaicZone());
            stealAllTiles(board.getAmphoraZone());
            stealAllTiles(board.getSkeletonZone());
            stealAllTiles(board.getStatueZone());
            view.updateView();
        }
        checkGameOver(board.getEntranceZone());
        if (!gameFinished) {
            endTurn();
        }
    }

    /**
     * Moves all tiles from a specific zone to the Thief's collection.
     * This is used in Single Player mode when a Landslide occurs.
     * <b>Post-condition:</b> The specified zone becomes empty.
     *
     * @param zone The zone from which to steal tiles.
     */
    private void stealAllTiles(Zone zone) {
        while (!zone.isEmpty()) {
            thief.addTile(zone.removeTile());
        }
    }

    /**
     * Checks if the game should end based on the Entrance Zone status.
     *
     * @param zone The entrance zone to check.
     * <b>Post-condition:</b> If zone.isFull() is true, gameFinished becomes true and winners are calculated.
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
            Map<Player, Integer> statuePoints = calculateStatuePoints();
            for (Player p : players) {
                int score = p.computePoints();
                score += statuePoints.getOrDefault(p, 0); // Get statue points
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
            view.setButtonsEnabled(false);
            view.scheduleExitAfterDelay(10_000);
        }
    }

    /**
     * Calculates the points awarded for Statue tiles
     * Points are awarded based on who has the majority of each statue type.
     * <b>Pre-condition:</b> The game must be in the scoring phase.
     * <b>Post-condition:</b> Returns a map associating each player with their statue bonus points.
     *
     * @return A Map where Key = Player and Value = Points from statues.
     */
    private Map<Player, Integer> calculateStatuePoints() {
        Map<Player, Integer> points = new HashMap<>();
        for (Player p : players) points.put(p, 0);

        points = assignMajorityPoints(points, true);
        points = assignMajorityPoints(points, false);

        return points;
    }

    /**
     * Helper method to determine the majority owner for a specific type of statue.
     * Rules: 6 points for most, 3 for others, 0 for least.
     * @param currentPoints The map of points accumulated so far.
     * @param isSphinx      True to calculate for Sphinxes, False for Caryatids.
     * @return The updated map of points.
     */
    private Map<Player, Integer> assignMajorityPoints(Map<Player, Integer> currentPoints, boolean isSphinx) {
        int maxCount = -1;
        int minCount = 1000;
        Map<Player, Integer> counts = new HashMap<>();
        for (Player p : players) {
            int count = 0;
            for (Tile t : p.getCollectedTiles()) {
                if (t instanceof StatueTile && ((StatueTile) t).isSphinx() == isSphinx) {
                    count++;
                }
            }
            counts.put(p, count);
            if (count > maxCount) maxCount = count;
            if (count < minCount) minCount = count;
        }
        for (Player p : players) {
            int count = counts.get(p);
            int bonus = 0;
            if (count == maxCount && count > 0) {
                bonus = 6;
            } else if (count == minCount) {
                bonus = 0;
            } else {
                bonus = 3;
            }

            currentPoints.put(p, currentPoints.get(p) + bonus);
        }
        return currentPoints;
    }

    // =============================================================
    // PLAYER ACTIONS
    // =============================================================

    /**
     * Handles the logic for using a Character card's special ability.
     * Validates the player's selection and delegates the action to the specific Character subclass.
     * Checks if the character has already been used and updates the view upon success.
     */
    public void useCharacter() {
        if (gameFinished) return;
        if (players == null || players.isEmpty()) return;
        if (currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) return;

        Player current = players.get(currentPlayerIndex);

        boolean validCharacterSelected = false;

        while (!validCharacterSelected) {
            int charIndex = view.promptCharacterSelection(); // or promptCharacterSelection()
            if (charIndex == -1) {
                break; // user cancelled
            }

            Character[] chars = current.getCharacters();
            if (chars == null || charIndex < 0 || charIndex >= chars.length) {
                view.showErrorMessage("Invalid character selection.");
                break;
            }

            Character chosenChar = chars[charIndex];
            if (chosenChar == null) {
                view.showErrorMessage("Selected character is not initialized.");
                break;
            }

            if (!chosenChar.getIsUsed()) {
                chosenChar.useAbility(current, this);
                validCharacterSelected = true;
                view.updateView();
            } else {
                view.showErrorMessage("You have already used this character!");
            }
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
        int i = view.promptTileCount();
        return i++;// because when the user selects 2 tiles the prompt returns 1
    }

    // =============================================================
    // PERSISTENCE
    // =============================================================

    /**
     * Saves the current game state to a file using Java Serialization.
     *
     * @param filePath The location to save the file (e.g., "saved_game.ser").
     */
    public void saveGame(String filePath) {
        if (!filePath.endsWith(".ser")) {// make the extension
            filePath += ".ser";
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            // we need to load them with the same order
            out.writeObject(players);
            out.writeInt(currentPlayerIndex);
            out.writeObject(bag);
            out.writeObject(board);
            out.writeBoolean(gameFinished);
            out.writeObject(thief);
            out.writeBoolean(isSinglePlayer);
            view.showMessage("Game saved successfully to " + filePath);
        } catch (IOException i) {
            view.showErrorMessage("Failed to save game: " + i.getMessage());
            i.printStackTrace();
        }
    }

    /**
     * Loads a game state from a file and restores the application state.
     *
     * @param filePath The location of the save file.
     */
    public void loadGame(String filePath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            this.players = (ArrayList<Player>) in.readObject();
            this.currentPlayerIndex = in.readInt();
            this.bag = (Bag) in.readObject();
            this.board = (Board) in.readObject();
            this.gameFinished = in.readBoolean();
            this.thief = (Player) in.readObject();
            this.isSinglePlayer = in.readBoolean();
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

    // =============================================================
    // AUDIO & TIMER
    // =============================================================

    /**
     * Initializes and starts the turn timer.
     * If the timer reaches 0, it automatically ends the turn.
     */
    private void startTimer() {
        if (gameTimer != null) {// Stop existing timer if running
            gameTimer.stop();
        }
        timeLeft = TURN_DURATION;
        view.updateTimer(timeLeft);
        gameTimer = new Timer(1000, e -> {// Create a new timer that ticks every 1 second
            timeLeft--;
            view.updateTimer(timeLeft);
            if (timeLeft <= 0) {
                gameTimer.stop();
                view.showMessage("Time's up! Turn ended.");
                endTurn();
            }
        });
        gameTimer.start();
    }

    /**
     * Plays the background music associated with the current player.
     * Now updated to play .wav files natively.
     *
     * @param playerIndex The index of the player (0-3).
     */
    private void playMusicForPlayer(int playerIndex) {
        if (isMuted) return;
        if (musicClip != null && musicClip.isRunning()) {// Stop old music
            musicClip.stop();
            musicClip.close();
        }
        try {
            String filePath = "music/Player" + (playerIndex + 1) + ".wav";
            File musicFile = new File(filePath);
            if (musicFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                musicClip = AudioSystem.getClip();// play the audio
                musicClip.open(audioStream);
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                musicClip.start();
            } else {
                System.err.println("Music file not found: " + filePath);
            }
        } catch (Exception e) {
            System.err.println("Error playing audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Toggles the game's audio mute state.
     * Stops the music if muted, or resumes the current player's track if unmuted.
     * Updates the view's mute button text.
     */
    public void toggleMute() {
        isMuted = !isMuted;
        view.updateMuteButton(isMuted);
        if (isMuted) {
            if (musicClip != null && musicClip.isRunning()) {
                musicClip.stop();
            }
        } else {
            playMusicForPlayer(currentPlayerIndex);
        }
    }

    /**
     * Plays a specific sound effect indicating a landslide occurred.
     * This uses a standard WAV file for compatibility.
     */
    private void playLandslideSound() {
        try {
            File soundFile = new File("music/landslide.wav");
            if (!soundFile.exists()) {
                System.err.println("Landslide sound not found: " + soundFile.getAbsolutePath());
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing landslide sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =============================================================
    // GETTERS
    // =============================================================

    /**
     * Accessor for the game board.
     *
     * @return The Board object associated with this controller.
     */
    public Board getBoard() {
        return board;
    }
    /**
     * Retrieves the player whose turn it currently is.
     *
     * @return The Player object at the current player index, or null if the list is empty.
     */
    public Player getCurrentPlayer() {
        if (players == null || players.isEmpty()) return null;
        if (currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) return null;
        return players.get(currentPlayerIndex);
    }
/**
 * DEBUG METHOD: Fills the entrance zone with Landslide tiles
 * to naturally trigger the Game Over condition.
 */
public void fillEntranceAndEnd() {
    EntranceZone zone = board.getEntranceZone();

    // 1. Keep adding tiles until the zone reports it is full
    while (!zone.isFull()) {
        // We add a LandslideTile because that's what normally goes in the entrance
        zone.addTile(new LandslideTile("images/landslide.png"));
    }

    // 2. Refresh the view so we can see the full zone
    view.updateView();

    // 3. Trigger the STANDARD game over check
    checkGameOver(zone);
}
}
