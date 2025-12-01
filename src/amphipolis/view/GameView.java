package amphipolis.view;

import amphipolis.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The main graphical user interface (GUI) for the Amphipolis game.
 * Responsible for rendering the game state (Board, Player Hand, Timer) to the screen.
 * <b>Invariant:</b> The View always holds a reference to a valid, initialized Controller.
 * <b>Invariant:</b> The main window (JFrame) is visible while the game is running.
 */
public class GameView extends JFrame {

    private Controller controller;
    private JLayeredPane boardPane; // Required by project description
    private JButton drawButton;
    private JButton endTurnButton;
    private JLabel timerLabel;
    private JLabel infoLabel;
    private JButton muteButton;

    /**
     * Constructor that generates the UI.
     * <b>Pre-condition:</b> The Controller must be started and initialized.
     * <b>Post-condition:</b> The game window is created, components are added, and the UI is displayed to the user.
     *
     * @param controller The game controller to link with this view.
     */
    public GameView(Controller controller) {

        this.controller = controller;

        this.setTitle("Amphipolis Game");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1000, 800);
        this.setLayout(new BorderLayout());

        this.boardPane = new JLayeredPane();
        this.drawButton = new JButton("Draw Tiles");
        this.endTurnButton = new JButton("End Turn");
        this.muteButton = new JButton("Mute");
        this.timerLabel = new JLabel("Time: 30");// todo add timer implementation + music

        this.add(boardPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.add(timerLabel);
        controlPanel.add(drawButton);
        controlPanel.add(endTurnButton);
        controlPanel.add(muteButton);
        this.add(controlPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    /**
     * Updates the graphical interface to reflect the current state of the Model.
     * <b>Pre-condition:</b> A view window is already active and visible.
     * <b>Post-condition:</b> All graphical components (Board tiles, Labels) are refreshed.
     */
    public void updateView() {
        // 1. Clear everything from the board pane so we don't draw over old tiles
        boardPane.removeAll();

        // 2. Add the Background Image (Layer 0 - Bottom)
        // Adjust the path if your image is in a different folder structure
        ImageIcon bgIcon = new ImageIcon("images/background.png");

        // Optional: Scale background to fit window if needed
        Image bgImage = bgIcon.getImage().getScaledInstance(1000, 800, Image.SCALE_SMOOTH);
        JLabel bgLabel = new JLabel(new ImageIcon(bgImage));

        bgLabel.setBounds(0, 0, 1000, 800);
        boardPane.add(bgLabel, Integer.valueOf(0)); // Layer 0

        // 3. Get the Board from the Controller
        amphipolis.model.Board board = controller.getBoard();

        // 4. Draw Tiles for each Zone (Layer 1 - Top)
        // You MUST adjust these x, y coordinates to match your background image slots!

        // Mosaic Zone (Top Left)
        drawZone(board.getMosaicZone(), 50, 50);

        // Statue Zone (Top Right)
        drawZone(board.getStatueZone(), 700, 50);

        // Amphora Zone (Bottom Left)
        drawZone(board.getAmphoraZone(), 50, 500);

        // Skeleton Zone (Bottom Right)
        drawZone(board.getSkeletonZone(), 700, 500);

        // Entrance Zone (Center - for Landslides)
        drawZone(board.getEntranceZone(), 380, 300);

        // 5. Refresh the UI to show changes
        boardPane.repaint();
        boardPane.revalidate();
    }

    /**
     * Helper method to draw all tiles in a specific zone at a given start position.
     * * @param zone The zone containing the tiles to draw.
     * @param startX The x-coordinate where the first tile should be placed.
     * @param startY The y-coordinate where the first tile should be placed.
     */
    private void drawZone(amphipolis.model.Zone zone, int startX, int startY) {
        if (zone == null) return;

        java.util.ArrayList<amphipolis.model.Tile> tiles = zone.getTiles();
        int x = startX;
        int y = startY;
        int tileWidth = 50;  // Adjust based on your actual tile image size
        int tileHeight = 50; // Adjust based on your actual tile image size
        int spacing = 10;    // Space between tiles

        for (amphipolis.model.Tile t : tiles) {
            // Load the image for this specific tile
            ImageIcon tileIcon = new ImageIcon(t.getImagePath());

            // Scale tile image if it's too big (optional but recommended)
            Image scaledImage = tileIcon.getImage().getScaledInstance(tileWidth, tileHeight, Image.SCALE_SMOOTH);
            JLabel tileLabel = new JLabel(new ImageIcon(scaledImage));

            // Set position
            tileLabel.setBounds(x, y, tileWidth, tileHeight);

            // Add to the BoardPane at Layer 1 (above background)
            boardPane.add(tileLabel, Integer.valueOf(1));

            // Move coordinates for the next tile
            // For a grid-like layout, you could add logic here:
            x += tileWidth + spacing;

            // Example wrap-around logic (if x gets too wide, move down a row)
            // if (x > startX + 200) {
            //     x = startX;
            //     y += tileHeight + spacing;
            // }
        }
    }

    /**
     * Displays a dialog asking the user if they want to load a previously saved game.
     *
     * @return true if the user chooses to load a game, false to start a new one.
     */
    public boolean promptLoadGame() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Do you want to load a saved game?",
                "Load Game",
                JOptionPane.YES_NO_OPTION
        );

        return response == JOptionPane.YES_OPTION;// returns 0 if yes

    }

    /**
     * Asks the user to choose the source of the save file.
     *
     * @return 0 for "Last Saved Game", 1 for "Select File", or other codes as needed.
     */
    public int promptLoadType() {
        Object[] options = {"Last saved", "Custom File"};
        return JOptionPane.showOptionDialog(
                this,
                "From where do you want to load",
                "Choose",
                JOptionPane.YES_NO_CANCEL_OPTION, // Option type
                JOptionPane.QUESTION_MESSAGE,     // Icon type
                null,     // icon if I want one
                options,
                options[0]// The default button
        );
    }

    /**
     * Opens a file chooser dialog to allow the user to select a specific .ser file.
     *
     * @return The absolute path of the selected file, or null if cancelled.
     */
    public String promptFileSelection() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose the save file");
        FileFilter filter = new FileNameExtensionFilter("Saved Games", "ser");
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * Displays a general informational message to the user.
     *
     * @param text The message content to display.
     */
    public void showMessage(String text) {
        JOptionPane.showMessageDialog(this, text, "Game Notification", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an error message to the user (usually with a warning icon).
     *
     * @param error The error message content.
     */
    public void showErrorMessage(String error) {
        JOptionPane.showMessageDialog(this, error, "Error Title", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Asks the user for the number of players participating in the game.
     *
     * @return The number of players (e.g., 1 or 4).
     */
    public int promptPlayerCount() {
        Object[] options = {"Single Player", "2 Players", "3 Players", "4 Players"};
        return JOptionPane.showOptionDialog(
                this,
                "The number of players",
                "Choose",
                JOptionPane.YES_NO_CANCEL_OPTION, // Option type
                JOptionPane.QUESTION_MESSAGE,     // Icon type
                null,     // icon if I want one
                options,
                options[0]// The default button
        );
    }

    /**
     * Prompts the user to select one of the four finding zones on the board.
     *
     * @return An integer representing the zone (e.g., 0=Mosaic, 1=Amphora, etc.).
     */
    public int promptZoneSelection() {
        Object[] options = {"Mosaic Zone", "Amphora Zone", "Skeleton Zone", "Statue Zone"};

        return JOptionPane.showOptionDialog(this,
                "Select a Zone to draw from:",
                "Zone Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    /**
     * Prompts the user to specify how many tiles they wish to draw.
     * This is typically used for the Digger character ability (1 or 2 tiles).
     *
     * @return The number of tiles selected.
     */
    public int promptTileCount() {
        Object[] options = {"1 Tile", "2 Tiles"};

        return JOptionPane.showOptionDialog(this,
                "Select how many tiles to pick:",
                "Tile Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    /**
     * Asks the user if they want to use a character card this turn.
     *
     * @return true if the user wants to use a card, false otherwise.
     */
    public boolean promptUseCharacter() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Do you want to use a character card?", // The Question
                "Use Ability",                          // The Title
                JOptionPane.YES_NO_OPTION               // The Buttons
        );
        return response == JOptionPane.YES_OPTION;
    }

    /**
     * Displays the available characters for the current player and asks them to select one.
     *
     * @return The index of the selected character in the player's hand, or -1 if cancelled.
     */
    public int promptCharacterSelection() {
        // These are the 5 standard characters in the game
        Object[] options = {"Assistant", "Archaeologist", "Digger", "Professor", "Coder"};

        return JOptionPane.showOptionDialog(
                this,
                "Select a Character to use:",       // Message
                "Character Selection",              // Title
                JOptionPane.DEFAULT_OPTION,         // Option Type
                JOptionPane.PLAIN_MESSAGE,          // Message Type (No icon needed usually)
                null,                               // Icon (null)
                options,                            // The Array of options
                options[0]                          // Default selection
        );
    }
    /**
     * Updates the timer label text.
     * @param seconds The integer seconds remaining (e.g., 29).
     */
    public void updateTimer(int seconds) {
        this.timerLabel.setText("Time: " + seconds);
        // Optional: Change color if time is running out
        if (seconds <= 5) {
            this.timerLabel.setForeground(Color.RED);
        } else {
            this.timerLabel.setForeground(Color.BLACK);
        }
    }
    /**
     * Updates the text or icon of the mute button.
     * @param isMuted The new state of the sound.
     */
    public void updateMuteButton(boolean isMuted) {
        if (isMuted) {
            this.muteButton.setText("Unmute");
        } else {
            this.muteButton.setText("Mute");
        }
    }
    /**
     * Connects the mute button to the Controller.
     * @param listener The action to perform when clicked.
     */
    public void setMuteButtonListener(java.awt.event.ActionListener listener) {
        this.muteButton.addActionListener(listener);
    }

    /**
     * Connects the Draw button to the Controller.
     */
    public void setDrawButtonListener(java.awt.event.ActionListener listener) {
        this.drawButton.addActionListener(listener);
    }

    /**
     * Connects the End Turn button to the Controller.
     */
    public void setEndTurnButtonListener(java.awt.event.ActionListener listener) {
        this.endTurnButton.addActionListener(listener);
    }
}