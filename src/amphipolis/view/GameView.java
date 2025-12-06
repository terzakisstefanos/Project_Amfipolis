package amphipolis.view;

import amphipolis.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GameView extends JFrame {

    private final Controller controller;
    private final JLayeredPane boardPane;
    private final JButton drawButton;
    private final JButton endTurnButton;
    private final JButton muteButton;
    private final JLabel timerLabel;
    private final JButton saveButton;
    private final JButton useCharacterButton;
    // Design-time positions
    private static final double MOSAIC_X_REL = 50.0 / 1000.0;
    private static final double MOSAIC_Y_REL = 50.0 / 800.0;

    private static final double STATUE_X_REL = 700.0 / 1000.0;
    private static final double STATUE_Y_REL = 50.0 / 800.0;

    private static final double AMPHORA_X_REL = 50.0 / 1000.0;
    private static final double AMPHORA_Y_REL = 500.0 / 800.0;

    private static final double SKELETON_X_REL = 700.0 / 1000.0;
    private static final double SKELETON_Y_REL = 500.0 / 800.0;

    private static final double ENTRANCE_X_REL = 380.0 / 1000.0;
    private static final double ENTRANCE_Y_REL = 300.0 / 800.0;

    // Zone box size (beige patch size) – tune once to match the artwork
    private static final int ZONE_BOX_WIDTH = 220;  // adjust if needed
    private static final int ZONE_BOX_HEIGHT = 180;  // adjust if needed


    // Hand window components
    private JFrame handWindow;
    private JPanel handPanel;
    private JLabel handTitleLabel;

    public GameView(Controller controller) {

        this.controller = controller;

        this.setTitle("Amphipolis Game");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Center: board
        this.boardPane = new JLayeredPane();
        this.add(boardPane, BorderLayout.CENTER);

        // Right: player bag / hand
        initHandPanel();
        this.add(createHandContainer(), BorderLayout.EAST);

        // Bottom: controls
        this.drawButton = new JButton("Draw Tiles");
        this.endTurnButton = new JButton("End Turn");
        this.muteButton = new JButton("Mute");
        this.timerLabel = new JLabel("Time: 30");
        this.saveButton = new JButton("Save Game");
        this.useCharacterButton = new JButton("Use Character");

        JPanel controlPanel = new JPanel();
        controlPanel.add(timerLabel);
        controlPanel.add(drawButton);
        controlPanel.add(endTurnButton);
        controlPanel.add(saveButton);
        controlPanel.add(muteButton);
        controlPanel.add(useCharacterButton);
        this.add(controlPanel, BorderLayout.SOUTH);

        this.setSize(1000, 800);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        // If you want the hand window to track the main window position:
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentMoved(java.awt.event.ComponentEvent e) {
                if (handWindow != null) {
                    Point mainLoc = getLocation();
                    handWindow.setLocation(mainLoc.x + getWidth(), mainLoc.y);
                }
            }
        });
    }

    /**
     * Initializes the separate window used to display the current player's hand.
     * Sets up the window's layout, size, and initial position relative to the main game window.
     */
    private void initHandWindow() {
        handWindow = new JFrame("Player Hand");
        handWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        handTitleLabel = new JLabel("Current Player:");
        handTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        handPanel = new JPanel(new FlowLayout());
        handPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        handWindow.setLayout(new BorderLayout());
        handWindow.add(handTitleLabel, BorderLayout.NORTH);
        handWindow.add(new JScrollPane(handPanel), BorderLayout.CENTER);

        handWindow.setSize(500, 300);

        // Place to the right of the main window
        Point mainLoc = getLocation();
        handWindow.setLocation(mainLoc.x + getWidth(), mainLoc.y);
        handWindow.setVisible(true);
    }

    /**
     * Initializes the panel responsible for displaying the player's collected tiles.
     * Configures the layout manager and adds the title label.
     */
    private void initHandPanel() {
        handPanel = new JPanel();
        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.Y_AXIS));
        handPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        handTitleLabel = new JLabel("Current Player:");
        handTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        handPanel.add(handTitleLabel);
        handPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    /**
     * Creates a scrollable container for the hand panel.
     * This container is typically added to the main game window's layout.
     *
     * @return A JPanel containing the scrollable hand panel.
     */
    private JPanel createHandContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setPreferredSize(new Dimension(250, 0)); // width of right bar

        JScrollPane scroll = new JScrollPane(handPanel);// add the scrolling
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scroll, BorderLayout.CENTER);

        return container;
    }

    // =============================================================
    // DRAWING & UPDATES
    // =============================================================

    /**
     * Updates the graphical interface
     */
    public void updateView() {
        boardPane.removeAll(); // Clear previous components

        int panelW = boardPane.getWidth();
        int panelH = boardPane.getHeight();
        if (panelW <= 0) panelW = getWidth(); // Handle initial size
        if (panelH <= 0) panelH = getHeight();

        ImageIcon bgIcon = new ImageIcon("images/background.png"); // Load background image
        Image bgOriginal = bgIcon.getImage();
        int imgW = bgOriginal.getWidth(null);
        int imgH = bgOriginal.getHeight(null);

        if (imgW > 0 && imgH > 0) {
            // Scale background while preserving aspect ratio
            double scaleX = (double) panelW / imgW;
            double scaleY = (double) panelH / imgH;
            double scale = Math.min(scaleX, scaleY); // Determine scale factor

            int drawW = (int) (imgW * scale);
            int drawH = (int) (imgH * scale);

            int offsetX = (panelW - drawW) / 2; // Center horizontally
            int offsetY = (panelH - drawH) / 2; // Center vertically

            Image bgScaled = bgOriginal.getScaledInstance(drawW, drawH, Image.SCALE_SMOOTH);
            JLabel bgLabel = new JLabel(new ImageIcon(bgScaled));
            bgLabel.setBounds(offsetX, offsetY, drawW, drawH);
            boardPane.add(bgLabel, Integer.valueOf(0)); // Add background layer

            amphipolis.model.Board board = controller.getBoard();
            if (board != null) {
                // Compute box top-left positions
                int boxX_mosaic = offsetX + (int) (MOSAIC_X_REL * drawW);
                int boxY_mosaic = offsetY + (int) (MOSAIC_Y_REL * drawH);

                int boxX_statue = offsetX + (int) (STATUE_X_REL * drawW);
                int boxY_statue = offsetY + (int) (STATUE_Y_REL * drawH);

                int boxX_amphora = offsetX + (int) (AMPHORA_X_REL * drawW);
                int boxY_amphora = offsetY + (int) (AMPHORA_Y_REL * drawH);

                int boxX_skeleton = offsetX + (int) (SKELETON_X_REL * drawW);
                int boxY_skeleton = offsetY + (int) (SKELETON_Y_REL * drawH);

                int boxX_entrance = offsetX + (int) (ENTRANCE_X_REL * drawW);
                int boxY_entrance = offsetY + (int) (ENTRANCE_Y_REL * drawH);

                // Draw zones using these box cords (drawZone centers the grid inside)
                drawZone(board.getMosaicZone(), boxX_mosaic, boxY_mosaic);
                drawZone(board.getStatueZone(), boxX_statue, boxY_statue);
                drawZone(board.getAmphoraZone(), boxX_amphora, boxY_amphora);
                drawZone(board.getSkeletonZone(), boxX_skeleton, boxY_skeleton);
                drawZone(board.getEntranceZone(), boxX_entrance, boxY_entrance);
            }
        }
        boardPane.revalidate(); // Refresh layout
        boardPane.repaint();
        updateHandPanel();

    }

    private void drawZone(amphipolis.model.Zone zone, int boxX, int boxY) {
        if (zone == null) return;

        java.util.ArrayList<amphipolis.model.Tile> tiles = zone.getTiles();
        int totalTiles = tiles.size();

        int boxWidth = ZONE_BOX_WIDTH;
        int maxVisible = 9;
        int cols = 3;
        int tileWidth = 50;
        int tileHeight = 50;
        int gap = 5;

        int visibleCount = Math.min(maxVisible, totalTiles);// limit visible tiles
        int rows = (int) Math.ceil(visibleCount / (double) cols);
        if (rows == 0) rows = 1;

        int gridWidth = cols * tileWidth + (cols - 1) * gap;
        int gridHeight = rows * tileHeight + (rows - 1) * gap;

        int gridX = boxX + (boxWidth - gridWidth) / 2;// center grid
        int gridY = boxY + (ZONE_BOX_HEIGHT - gridHeight) / 2;

        for (int i = 0; i < visibleCount; i++) {
            amphipolis.model.Tile t = tiles.get(i);
            int row = i / cols;
            int col = i % cols;

            int x = gridX + col * (tileWidth + gap);
            int y = gridY + row * (tileHeight + gap);// calculate position

            ImageIcon tileIcon = new ImageIcon(t.getImagePath());// load the image
            Image scaledImage = tileIcon.getImage().getScaledInstance(tileWidth, tileHeight, Image.SCALE_SMOOTH);// scale the image
            JLabel tileLabel = new JLabel(new ImageIcon(scaledImage));// Create label
            tileLabel.setBounds(x, y, tileWidth, tileHeight);
            boardPane.add(tileLabel, Integer.valueOf(2));// add to board
        }

        int extra = totalTiles - visibleCount;
        if (extra > 0) {// handle overflow
            JLabel extraLabel = new JLabel("+" + extra);
            extraLabel.setForeground(Color.WHITE);
            extraLabel.setOpaque(true);
            extraLabel.setBackground(new Color(0, 0, 0, 170));// style label
            extraLabel.setHorizontalAlignment(SwingConstants.CENTER);

            int labelW = 40;
            int labelH = 20;
            int labelX = boxX + boxWidth - labelW - 5;
            int labelY = boxY + 5;

            extraLabel.setBounds(labelX, labelY, labelW, labelH);
            boardPane.add(extraLabel, Integer.valueOf(3));// add overflow label
        }
    }

    /**
     * Updates the side panel that displays the current player's collected tiles.
     * This method refreshes the UI to reflect the current state of the player's inventory.
     */
    private void updateHandPanel() {
        if (handPanel == null || controller == null) return;
        amphipolis.model.Player current = controller.getCurrentPlayer();
        if (current == null) return;
        handTitleLabel.setText("Current Player: " + current.getName());// update title
        handPanel.removeAll();
        handPanel.add(handTitleLabel);// readd title
        handPanel.add(Box.createRigidArea(new Dimension(0, 10)));// add the area between the tittle and the tiles
        java.util.ArrayList<amphipolis.model.Tile> tiles = current.getCollectedTiles();
        if (tiles == null) tiles = new java.util.ArrayList<>();
        int tileWidth = 40;
        int tileHeight = 40;
        for (amphipolis.model.Tile t : tiles) {
            ImageIcon icon = new ImageIcon(t.getImagePath());// load the image
            Image img = icon.getImage().getScaledInstance(tileWidth, tileHeight, Image.SCALE_SMOOTH);// scale the image
            JLabel lbl = new JLabel(new ImageIcon(img));// Create the img
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);// Center the tile
            handPanel.add(Box.createRigidArea(new Dimension(0, 5)));// spacer between each tile
        }
        handPanel.revalidate();// Refresh
        handPanel.repaint();
    }

    // =============================================================
    // DIALOGS & PROMPTS
    // =============================================================

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
     * @return 0 for "Last Saved Game", 1 for Select File, or other codes as needed.
     */
    public int promptLoadType() {
        Object[] options = {"Last saved", "Custom File"};
        return JOptionPane.showOptionDialog(
                this,
                "From where do you want to load",
                "Choose",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
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
     * Opens a file chooser dialog to allow the user to select a location to save the game.
     *
     * @return The absolute path of the selected file, or null if the user cancelled.
     */
    public String promptSaveFilePath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose where to save the game");
        FileFilter filter = new FileNameExtensionFilter("Saved Games", "ser");
        chooser.setFileFilter(filter);

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            return file.getAbsolutePath();
        }
        return null; // user cancelled
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
     * Displays an error message to the user
     *
     * @param error The error message content.
     */
    public void showErrorMessage(String error) {
        JOptionPane.showMessageDialog(this, error, "Error Title", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Asks the user for the number of players participating in the game.
     *
     * @return The number of players
     */
    public int promptPlayerCount() {
        Object[] options = {"Single Player", "2 Players", "3 Players", "4 Players"};
        return JOptionPane.showOptionDialog(
                this,
                "The number of players",
                "Choose",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[3]
        );
    }

    /**
     * Prompts the user to select one of the four finding zones on the board.
     *
     * @return An integer representing the zone.
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
     * This is typically used for the Digger character ability.
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
                "Do you want to use a character card?",
                "Use Ability",
                JOptionPane.YES_NO_OPTION
        );
        return response == JOptionPane.YES_OPTION;
    }

    /**
     * Displays the available characters for the current player and asks them to select one.
     *
     * @return The index of the selected character in the player's hand, or -1 if cancelled.
     */
    public int promptCharacterSelection() {
        String[] imagePaths = {
                "images/assistant.png",
                "images/archaeologist.png",
                "images/digger.png",
                "images/professor.png",
                "images/coder.png"
        };
        final int[] selectedIndex = {-1};// Initialize to -1 to indicate no selection
        JDialog dialog = new JDialog(this, "Select a Character", true);//Block interaction with the main window until a choice is made
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new FlowLayout());//Display buttons in a row
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        int buttonSize = 120;
        for (int i = 0; i < imagePaths.length; i++) {// make the images
            final int index = i;
            ImageIcon icon = new ImageIcon(imagePaths[i]);// Size the image correctly
            Image img = icon.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
            JButton button = new JButton(icon);// Create a button with the character icon
            button.setPreferredSize(new Dimension(buttonSize, buttonSize));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.addActionListener(e -> { // the button listener for the characters
                selectedIndex[0] = index;
                dialog.dispose();
            });
            panel.add(button);
        }
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return selectedIndex[0]; // if the pop-up is closed then return -1
    }

    /**
     * Displays a dialog with an image alerting the players that a Landslide has occurred.
     */
    public void showLandslideDialog() {
        ImageIcon icon = new ImageIcon("images/landslide.png");
        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        JOptionPane.showMessageDialog(
                this,
                null,             // you can also pass null if you only want the image
                "Landslide!",
                JOptionPane.INFORMATION_MESSAGE,
                icon
        );
    }

    // =============================================================
    // UI CONTROLS & LISTENERS
    // =============================================================

    /**
     * Updates the timer label text.
     *
     * @param seconds The integer seconds remaining.
     */
    public void updateTimer(int seconds) {
        this.timerLabel.setText("Time: " + seconds);
        if (seconds <= 5) {
            this.timerLabel.setForeground(Color.RED);// if time is running low change to red
        } else {
            this.timerLabel.setForeground(Color.BLACK);
        }
    }

    /**
     * Updates the text or icon of the mute button.
     *
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
     *
     * @param listener The action to perform when clicked.
     */
    public void setMuteButtonListener(java.awt.event.ActionListener listener) {
        this.muteButton.addActionListener(listener);
    }

    /**
     * Connects the Draw button to the Controller.
     * @param listener  the button listener
     */
    public void setDrawButtonListener(java.awt.event.ActionListener listener) {
        this.drawButton.addActionListener(listener);
    }

    /**
     * Connects the End Turn button to the Controller.
     * @param listener  the button listener
     */
    public void setEndTurnButtonListener(java.awt.event.ActionListener listener) {
        this.endTurnButton.addActionListener(listener);
    }

    /**
     * Connects the Save button to the Controller.
     *@param listener  the button listener
     */
    public void setSaveButtonListener(java.awt.event.ActionListener listener) {
        this.saveButton.addActionListener(listener);
    }

    /**
     * Connects the Use Character button to the Controller.
     * @param listener  the button listener
     */
    public void setUseCharacterButtonListener(java.awt.event.ActionListener listener) {
        this.useCharacterButton.addActionListener(listener);
    }

    /**
     * Performs a visual shake animation on the game window.
     * Used to provide visual feedback when a Landslide occurs.
     */
    public void shakeWindow() {
        final int shakeDistance = 10;   // pixels
        final int shakeDuration = 400;  // total duration in ms
        final int shakeDelay = 40;      // delay between moves in ms

        Point originalLocation = this.getLocation();
        long start = System.currentTimeMillis();

        Timer shakeTimer = new Timer(shakeDelay, null);
        // start the shake logic
        shakeTimer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > shakeDuration) {
                // End: restore original location and stop timer
                this.setLocation(originalLocation);
                shakeTimer.stop();
            } else {
                int offsetX = (int) ((Math.random() - 0.5) * 2 * shakeDistance);
                int offsetY = (int) ((Math.random() - 0.5) * 2 * shakeDistance);
                this.setLocation(originalLocation.x + offsetX, originalLocation.y + offsetY);
            }
        });
        shakeTimer.start();
    }

    /**
     * Enables or disables the main game control buttons.
     * Useful for preventing input during animations or after the game has ended.
     *
     * @param enabled true to enable buttons, false to disable them.
     */
    public void setButtonsEnabled(boolean enabled) {
        drawButton.setEnabled(enabled);
        endTurnButton.setEnabled(enabled);
        muteButton.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        useCharacterButton.setEnabled(enabled);
    }

    /**
     * Closes the window and exits the program after the given delay.
     * @param delayMillis  the milliseconds its going to wait
     */
    public void scheduleExitAfterDelay(int delayMillis) {
        new javax.swing.Timer(delayMillis, e -> {
            System.exit(0);
        }) {{
            setRepeats(false);
            start();
        }};
    }
}