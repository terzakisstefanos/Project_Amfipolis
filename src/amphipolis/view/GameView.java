package amphipolis.view;

import amphipolis.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GameView extends JFrame {

    private Controller controller;
    private JLayeredPane boardPane;
    private JButton drawButton;
    private JButton endTurnButton;
    private JButton muteButton;
    private JLabel timerLabel;


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

            JPanel controlPanel = new JPanel();
            controlPanel.add(timerLabel);
            controlPanel.add(drawButton);
            controlPanel.add(endTurnButton);
            controlPanel.add(muteButton);
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
     * Updates the graphical interface (board + hand window).
     */
    public void updateView() {
        boardPane.removeAll();

        int panelW = boardPane.getWidth();
        int panelH = boardPane.getHeight();
        if (panelW <= 0) panelW = getWidth();
        if (panelH <= 0) panelH = getHeight();

        ImageIcon bgIcon = new ImageIcon("images/background.png");
        Image bgOriginal = bgIcon.getImage();
        int imgW = bgOriginal.getWidth(null);
        int imgH = bgOriginal.getHeight(null);

        if (imgW > 0 && imgH > 0) {
            double scaleX = (double) panelW / imgW;
            double scaleY = (double) panelH / imgH;
            double scale = Math.min(scaleX, scaleY);

            int drawW = (int) (imgW * scale);
            int drawH = (int) (imgH * scale);

            int offsetX = (panelW - drawW) / 2;
            int offsetY = (panelH - drawH) / 2;

            Image bgScaled = bgOriginal.getScaledInstance(drawW, drawH, Image.SCALE_SMOOTH);
            JLabel bgLabel = new JLabel(new ImageIcon(bgScaled));
            bgLabel.setBounds(offsetX, offsetY, drawW, drawH);
            boardPane.add(bgLabel, Integer.valueOf(0)); // background layer

            amphipolis.model.Board board = controller.getBoard();
            if (board != null) {
                // Relative positions on the board image (tune to fit your artwork)
                double mosaicRelX   = 0.08;
                double mosaicRelY   = 0.10;
                double statueRelX   = 0.70;
                double statueRelY   = 0.10;
                double amphoraRelX  = 0.08;
                double amphoraRelY  = 0.65;
                double skeletonRelX = 0.70;
                double skeletonRelY = 0.65;
                double entranceRelX = 0.42;
                double entranceRelY = 0.40;

                int mosaicX   = offsetX + (int) (mosaicRelX   * drawW);
                int mosaicY   = offsetY + (int) (mosaicRelY   * drawH);
                int statueX   = offsetX + (int) (statueRelX   * drawW);
                int statueY   = offsetY + (int) (statueRelY   * drawH);
                int amphoraX  = offsetX + (int) (amphoraRelX  * drawW);
                int amphoraY  = offsetY + (int) (amphoraRelY  * drawH);
                int skeletonX = offsetX + (int) (skeletonRelX * drawW);
                int skeletonY = offsetY + (int) (skeletonRelY * drawH);
                int entranceX = offsetX + (int) (entranceRelX * drawW);
                int entranceY = offsetY + (int) (entranceRelY * drawH);

                drawZone(board.getMosaicZone(),   mosaicX,   mosaicY);
                drawZone(board.getStatueZone(),   statueX,   statueY);
                drawZone(board.getAmphoraZone(),  amphoraX,  amphoraY);
                drawZone(board.getSkeletonZone(), skeletonX, skeletonY);
                drawZone(board.getEntranceZone(), entranceX, entranceY);
            }
        }

        boardPane.revalidate();
        boardPane.repaint();

        // Update the right-side bag
        updateHandPanel();
    }

    private void drawZone(amphipolis.model.Zone zone, int startX, int startY) {
        if (zone == null) return;

        java.util.ArrayList<amphipolis.model.Tile> tiles = zone.getTiles();
        int x = startX;
        int y = startY;
        int tileWidth = 50;
        int tileHeight = 50;
        int spacing = 10;

        for (amphipolis.model.Tile t : tiles) {
            ImageIcon tileIcon = new ImageIcon(t.getImagePath());
            Image scaledImage = tileIcon.getImage().getScaledInstance(tileWidth, tileHeight, Image.SCALE_SMOOTH);
            JLabel tileLabel = new JLabel(new ImageIcon(scaledImage));
            tileLabel.setBounds(x, y, tileWidth, tileHeight);
            boardPane.add(tileLabel, Integer.valueOf(1));

            x += tileWidth + spacing;
        }
    }

    private void updateHandPanel() {
        if (handPanel == null || controller == null) return;

        amphipolis.model.Player current = controller.getCurrentPlayer();
        if (current == null) return;

        // Title
        handTitleLabel.setText("Current Player: " + current.getName());

        // Remove old tile labels (keep the title at index 0 and a spacer)
        // Easiest: clear all and re-add title + spacer + tiles
        handPanel.removeAll();
        handPanel.add(handTitleLabel);
        handPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        java.util.ArrayList<amphipolis.model.Tile> tiles = current.getCollectedTiles();
        if (tiles == null) tiles = new java.util.ArrayList<>();

        int tileWidth = 40;
        int tileHeight = 40;

        for (amphipolis.model.Tile t : tiles) {
            ImageIcon icon = new ImageIcon(t.getImagePath());
            Image img = icon.getImage().getScaledInstance(tileWidth, tileHeight, Image.SCALE_SMOOTH);
            JLabel lbl = new JLabel(new ImageIcon(img));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            handPanel.add(lbl);
            handPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        handPanel.revalidate();
        handPanel.repaint();
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
                options[3]// The default button
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
        // Paths to your character images (adjust as needed)
        String[] imagePaths = {
                "images/assistant.png",
                "images/archaeologist.png",
                "images/digger.png",
                "images/professor.png",
                "images/coder.png"
        };

        final int[] selectedIndex = {-1};

        JDialog dialog = new JDialog(this, "Select a Character", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int buttonSize = 120; // size for scaled images/buttons

        for (int i = 0; i < imagePaths.length; i++) {
            final int index = i;

            ImageIcon icon = new ImageIcon(imagePaths[i]);
            // Optional: scale image to a fixed size
            Image img = icon.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            JButton button = new JButton(icon);
            button.setPreferredSize(new Dimension(buttonSize, buttonSize));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false); // optional: transparent background

            button.addActionListener(e -> {
                selectedIndex[0] = index;
                dialog.dispose();
            });

            panel.add(button);
        }

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        // When dialog is closed without clicking, selectedIndex[0] stays -1
        return selectedIndex[0];
    }

    /**
     * Updates the timer label text.
     *
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

    public void shakeWindow() {
        final int shakeDistance = 10;   // pixels
        final int shakeDuration = 400;  // total duration in ms
        final int shakeDelay = 40;      // delay between moves in ms

        Point originalLocation = this.getLocation();
        long start = System.currentTimeMillis();

        Timer shakeTimer = new Timer(shakeDelay, null);
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

    public void showLandslideDialog() {
        // Adjust path to your landslide image
        ImageIcon icon = new ImageIcon("images/landslide.png");

        // Optionally scale
        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        JOptionPane.showMessageDialog(
                this,
                "Landslide!",             // you can also pass null if you only want the image
                "Landslide!",
                JOptionPane.INFORMATION_MESSAGE,
                icon
        );
    }
    public void setButtonsEnabled(boolean enabled) {
        drawButton.setEnabled(enabled);
        endTurnButton.setEnabled(enabled);
        muteButton.setEnabled(enabled);
    }

    /**
     * Closes the window / exits the program after the given delay (milliseconds).
     */
    public void scheduleExitAfterDelay(int delayMillis) {
        new javax.swing.Timer(delayMillis, e -> {
            System.exit(0);
        }) {{
            setRepeats(false);
            start();
        }};
    }
    private void initHandPanel() {
        handPanel = new JPanel();
        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.Y_AXIS));
        handPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        handTitleLabel = new JLabel("Current Player:");
        handTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        handPanel.add(handTitleLabel);
        handPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JPanel createHandContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setPreferredSize(new Dimension(250, 0)); // width of right bar

        JScrollPane scroll = new JScrollPane(handPanel);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scroll, BorderLayout.CENTER);

        return container;
    }
}

