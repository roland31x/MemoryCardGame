import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class MemoryCardGame extends JFrame {
    private final int NUM_PAIRS = 16;
    private final List<Integer> cardValues = new ArrayList<>();
    private final JButton[] buttons = new JButton[NUM_PAIRS * 2];
    private final boolean[] matched = new boolean[NUM_PAIRS * 2];

    private int firstIndex = -1;
    private int secondIndex = -1;
    private boolean gameStarted = false;

    private final JPanel gamePanel = new JPanel();
    private final JButton startButton = new JButton("Start Game");

    public MemoryCardGame() {
        setTitle("Memory Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 460);
        setLayout(new BorderLayout());

        // Top panel with Start button
        JPanel topPanel = new JPanel();
        topPanel.add(startButton);
        add(topPanel, BorderLayout.NORTH);

        // Game grid panel
        gamePanel.setLayout(new GridLayout(4, 8));
        add(gamePanel, BorderLayout.CENTER);

        initializeCards();
        createButtons();
        setButtonsEnabled(false);  // disabled until game starts

        startButton.addActionListener(e -> startGame());

        setVisible(true);
    }

    private void initializeCards() {
        cardValues.clear();
        for (int i = 0; i < NUM_PAIRS; i++) {
            cardValues.add(i);
            cardValues.add(i);
        }
        Collections.shuffle(cardValues);
    }

    private void createButtons() {
        gamePanel.removeAll();
        for (int i = 0; i < cardValues.size(); i++) {
            JButton button = new JButton();
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setBackground(null); // light gray
            button.setFocusPainted(false);

            int index = i;
            button.addActionListener(e -> handleClick(index));
            buttons[i] = button;
            ImageIcon icon = CardImageResolver.getIconForValue(cardValues.get(i), 64);
            buttons[i].setIcon(null);
            buttons[i].setDisabledIcon(null); // Set disabled icon for consistency

            // Wrap button in a panel with padding
            JPanel paddedPanel = new JPanel(new BorderLayout());
            paddedPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 5px margin
            paddedPanel.setBackground(gamePanel.getBackground());
            paddedPanel.add(button, BorderLayout.CENTER);
            gamePanel.add(paddedPanel);
        }
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    private void startGame() {
        gameStarted = true;
        startButton.setText("Restart Game");

        // Remove previous action listeners
        for (ActionListener al : startButton.getActionListeners()) {
            startButton.removeActionListener(al);
        }

        // Add new listener for restarting
        startButton.addActionListener(e -> restartGame());

        revealAllCards();

        // Hide cards after 2 seconds
        Timer timer = new Timer(2000, e2 -> {
            hideUnmatchedCards();
            setButtonsEnabled(true);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void restartGame() {
        // Reset state
        Arrays.fill(matched, false);
        firstIndex = -1;
        secondIndex = -1;
        gameStarted = true;

        // Shuffle cards again
        initializeCards();

        // Reset buttons
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setIcon(null);
            buttons[i].setEnabled(false);
            buttons[i].setBackground(null); // Reset green highlight
        }

        revealAllCards();

        // Hide after 2 seconds
        Timer timer = new Timer(2000, e -> {
            hideUnmatchedCards();
            setButtonsEnabled(true);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void revealAllCards() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(false);
            ImageIcon icon = CardImageResolver.getIconForValue(cardValues.get(i), 64);
            buttons[i].setIcon(icon);
            buttons[i].setDisabledIcon(icon);
        }
    }

    private void hideUnmatchedCards() {
        for (int i = 0; i < buttons.length; i++) {
            if (!matched[i]) {
                buttons[i].setIcon(null);
                buttons[i].setDisabledIcon(null);
                buttons[i].setEnabled(true);
            }
        }
    }

    private void handleClick(int index) {
        if (!gameStarted || matched[index] || index == firstIndex) return;

        ImageIcon icon = CardImageResolver.getIconForValue(cardValues.get(index), 64);
        buttons[index].setIcon(icon);
        buttons[index].setDisabledIcon(icon);
        if (firstIndex == -1) {
            firstIndex = index;
        } else if (secondIndex == -1) {
            secondIndex = index;
            setButtonsEnabled(false);
            Timer timer = new Timer(1000, e -> checkMatch());
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void checkMatch() {
        int val1 = cardValues.get(firstIndex);
        int val2 = cardValues.get(secondIndex);

        if (val1 == val2) {
            matched[firstIndex] = true;
            matched[secondIndex] = true;
            buttons[firstIndex].setBackground(Color.GREEN);
            buttons[secondIndex].setBackground(Color.GREEN);
        } else {
            buttons[firstIndex].setIcon(null);
            buttons[firstIndex].setDisabledIcon(null);
            buttons[secondIndex].setIcon(null);
            buttons[secondIndex].setDisabledIcon(null);
        }

        firstIndex = -1;
        secondIndex = -1;
        setButtonsEnabled(true);

        if (isGameOver()) {
            JOptionPane.showMessageDialog(this, "You won!");
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        for (int i = 0; i < buttons.length; i++) {
            if (!matched[i]) {
                buttons[i].setEnabled(enabled);
            }
        }
    }

    private boolean isGameOver() {
        for (boolean b : matched) {
            if (!b) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryCardGame::new);
    }
}