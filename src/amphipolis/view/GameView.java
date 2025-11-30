package amphipolis.view;

import amphipolis.controller.Controller;
import javax.swing.*;
import java.awt.*;

/**
 * The main graphical user interface (GUI) for the Amphipolis game.
 * Responsible for rendering the game state (Board, Player Hand, Timer) to the screen.
 *
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

        //TODO: Initialize Components
        this.boardPane = new JLayeredPane();
        this.drawButton = new JButton("Draw Tiles");
        this.endTurnButton = new JButton("End Turn");
        this.timerLabel = new JLabel("Time: 30");

        this.add(boardPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.add(timerLabel);
        controlPanel.add(drawButton);
        controlPanel.add(endTurnButton);
        this.add(controlPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    /**
     * Updates the graphical interface to reflect the current state of the Model.
     * <b>Pre-condition:</b> A view window is already active and visible.
     * <b>Post-condition:</b> All graphical components (Board, Hand, Labels) are refreshed to match the Model's data.
     */
    public void updateView() {
        // TODO:
        // 1. Get Board from controller.getBoard()
        // 2. Clear old icons from JLayeredPane
        // 3. Redraw tiles in their new positions
    }

    public boolean promptLoadGame() {
        return false;
    }

    public int promptLoadType() {
        return 0;
    }

    public String promptFileSelection() {
        return "";
    }

    public void showMessage(String s) {
    }

    public int promptPlayerCount() {
        return 0;
    }

    public int promptZoneSelection() {
        return 0;
    }

    public void showErrorMessage(String s) {
    }

    public int promptTileCount() {
        return 0;
    }
}