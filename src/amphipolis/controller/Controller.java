package amphipolis.controller;

import amphipolis.model.*;
import amphipolis.model.Character;
import amphipolis.view.GameView;

import javax.swing.Timer;
import java.util.*;
import java.io.*;
import javax.sound.sampled.*;

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
    private Timer gameTimer;
    private int timeLeft;
    private final int TURN_DURATION = 30;
    private Clip musicClip;
    private boolean isMuted = false;
    private long clipTimePosition = 0;


    /**
     * Starts the game setup process.
     * <b>Pre-condition:</b> The game must not already be running (gameFinished should be true or uninitialized).
     * <b>Post-condition:</b> Initializes Players, Board, Bag, and starts the first turn.
     */
    public void startGame() {
        this.view = new GameView(this);
        view.setMuteButtonListener(e -> toggleMute());
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
        int numPlayers=-1;
        while (numPlayers==-1){
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
    }


    /**
     * Ends the current player's turn manually.
     * <b>Pre-condition:</b> The player must be in the "Action Phase" of their turn.
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

    /**
     * Handles the special logic when a Landslide Tile is drawn.
     * * @param t The landslide tile that was drawn.
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
     *             <b>Post-condition:</b> If zone.isFull() is true, gameFinished becomes true and the GUI displays the results.
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
     * Calculates the points awarded for Statue tiles (Sphinxes and Caryatids).
     * Points are awarded based on who has the majority of each statue type.
     * <b>Pre-condition:</b> The game must be in the scoring phase.
     * <b>Post-condition:</b> Returns a map associating each player with their statue bonus points.
     *
     * @return A Map where Key = Player and Value = Points from statues.
     */
    private Map<Player, Integer> calculateStatuePoints() {
        Map<Player, Integer> points = new HashMap<>();
        for (Player p : players) points.put(p, 0);

        points = assignMajorityPoints(points, true);  // Pass 1: Sphinxes
        points = assignMajorityPoints(points, false); // Pass 2: Caryatids

        return points;
    }

    /**
     * Helper method to determine the majority owner for a specific type of statue.
     * Rules: 6 points for most, 3 for others, 0 for least.
     *
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
        int i= view.promptTileCount();
        return i++;// because when the user selects 2 tiles the prompt returns 1
    }

    /**
     * Accessor for the game board.
     *
     * @return The Board object associated with this controller.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Plays the background music associated with the current player using Java Sound SPI (jFLAC).
     *
     * @param playerIndex The index of the player (0-3).
     */
    private void playMusicForPlayer(int playerIndex) {
        if (isMuted) return;

        // Stop old music
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }

        try {
            // 1. Get the file (e.g., "music/Player1.flac")
            String filePath = "music/Player" + (playerIndex + 1) + ".flac";
            File musicFile = new File(filePath);

            if (musicFile.exists()) {
                // 2. Get the raw (compressed) audio stream
                AudioInputStream rawStream = AudioSystem.getAudioInputStream(musicFile);
                AudioFormat baseFormat = rawStream.getFormat();

                // 3. Create a "Decoded" format (PCM Signed) that Java can actually play
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16, // 16-bit is standard for playback
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2, // Frame Size
                        baseFormat.getSampleRate(),
                        false // Big Endian
                );

                // 4. Convert the FLAC stream to the Decoded PCM stream
                AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, rawStream);

                // 5. Play the decoded stream
                musicClip = AudioSystem.getClip();
                musicClip.open(decodedStream);
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
     * Toggles the sound on/off.
     */
    public void toggleMute() {
        isMuted = !isMuted;
        view.updateMuteButton(isMuted);

        if (isMuted) {
            if (musicClip != null && musicClip.isRunning()) {
                musicClip.stop();
            }
        } else {
            // Resume playing current player's music
            playMusicForPlayer(currentPlayerIndex);
        }
    }

    /**
     * Initializes and starts the turn timer.
     * If the timer reaches 0, it automatically ends the turn.
     */
    private void startTimer() {
        // Stop existing timer if running
        if (gameTimer != null) {
            gameTimer.stop();
        }

        timeLeft = TURN_DURATION;
        view.updateTimer(timeLeft);

        // Create a new timer that ticks every 1000ms (1 second)
        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            view.updateTimer(timeLeft);

            if (timeLeft <= 0) {
                gameTimer.stop();
                view.showMessage("Time's up! Turn ended.");
                endTurn(); // Force end of turn
            }
        });

        gameTimer.start();
    }
    private void playLandslideSound() {
        try {
            // Use a short WAV/AIFF/au sound; Java Sound handles these best.
            File soundFile = new File("music/landslide.wav"); // adjust path and format
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
    public Player getCurrentPlayer() {
        if (players == null || players.isEmpty()) return null;
        if (currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) return null;
        return players.get(currentPlayerIndex);
    }
}