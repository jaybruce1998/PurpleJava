package purple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class BlackjackDialog extends JDialog {

    private static final int TILE = 64;
    private static final int SIZE = TILE * 11;

    private final Deck deck = new Deck();
    private final List<Card> playerHand = new ArrayList<>();
    private final List<Card> dealerHand = new ArrayList<>();

    private boolean dealerHideFirst = true;
    private boolean finished = false;
    private boolean playerWon = false;
    private boolean tie = false;

    private final TablePanel table = new TablePanel();
    private final JButton hit = new JButton("Hit");
    private final JButton stand = new JButton("Stand");

    private BlackjackDialog(Frame owner) {
        super(owner, "Blackjack", true);
        setSize(SIZE, SIZE);
        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        add(table, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(hit);
        buttons.add(stand);
        add(buttons, BorderLayout.SOUTH);

        hit.addActionListener(this::onHit);
        stand.addActionListener(this::onStand);

        startRound();
    }

    /* ========================= PUBLIC API ========================= */

    public static boolean play(Frame parent) {
        BlackjackDialog dlg = new BlackjackDialog(parent);
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true); // modal
        return dlg.playerWon;
    }

    /* ========================= GAME FLOW ========================= */

    private void startRound() {
        playerHand.clear();
        dealerHand.clear();
        dealerHideFirst = true;
        tie = false;

        deck.shuffle();

        playerHand.add(deck.draw());
        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());
        dealerHand.add(deck.draw());

        repaint();

        checkImmediateOutcome();
    }

    private void onHit(ActionEvent e) {
        if (finished) return;

        playerHand.add(deck.draw());

        if (handValue(playerHand) > 21) {
            end(false);
        }

        repaint();
    }

    private void onStand(ActionEvent e) {
        if (finished) return;

        dealerHideFirst = false;

        while (dealerShouldHit()) {
            dealerHand.add(deck.draw());
        }

        int p = handValue(playerHand);
        int d = handValue(dealerHand);

        if (p > 21) {
            end(false);
        } else if (d > 21) {
            end(true);
        } else if (p > d) {
            end(true);
        } else if (p < d) {
            end(false);
        } else {
            // Tie → immediately start another round
            tie = true;
            Timer t = new Timer(1000, e2 -> startRound());
            t.setRepeats(false);
            t.start();
        }

        repaint();
    }

    private void checkImmediateOutcome() {
        if (handValue(playerHand) == 21) {
            dealerHideFirst = false;
            end(true);
        }
    }

    private void end(boolean win) {
        finished = true;
        playerWon = win;

        hit.setEnabled(false);
        stand.setEnabled(false);

        repaint();

        Timer t = new Timer(2000, e -> dispose());
        t.setRepeats(false);
        t.start();
    }

    /* ========================= DEALER LOGIC ========================= */

    private boolean dealerShouldHit() {
        int value = handValue(dealerHand);
        return value < 17 || (value == 17 && isSoft(dealerHand));
    }

    /* ========================= HAND MATH ========================= */

    private int handValue(List<Card> hand) {
        int sum = 0;
        int aces = 0;

        for (Card c : hand) {
            sum += c.value();
            if (c.rank == Rank.ACE) aces++;
        }

        while (sum > 21 && aces-- > 0) {
            sum -= 10;
        }

        return sum;
    }

    private boolean isSoft(List<Card> hand) {
        int sum = 0;
        int aces = 0;

        for (Card c : hand) {
            sum += c.value();
            if (c.rank == Rank.ACE) aces++;
        }

        return aces > 0 && sum <= 21;
    }

    /* ========================= DRAWING ========================= */

    private class TablePanel extends JPanel {

        TablePanel() {
            setBackground(new Color(10, 110, 30));
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            drawHand(g2, dealerHand, SIZE / 2, 120, dealerHideFirst);
            drawHand(g2, playerHand, SIZE / 2, SIZE - 240, false);

            g2.setColor(Color.WHITE);
            g2.setFont(OverworldGui.pokemonFont);

			g2.drawString("Dealer", 40, 60);
			g2.drawString("Player", 40, SIZE - 300);
			g2.drawString("Points: " + handValue(playerHand), 40, SIZE - 280);
            if (!finished) {

                if (tie) {
                    g2.drawString("TIE! DEALING AGAIN", SIZE / 2 - 110, SIZE / 2);
                }
            } else {
                g2.drawString(playerWon ? "YOU WIN!" : "YOU LOSE",
                        SIZE / 2 - 60, SIZE / 2);
                    g2.drawString("Points: " + handValue(dealerHand), 40, 80);
            }
        }

        private void drawHand(Graphics2D g2, List<Card> hand, int centerX, int y, boolean hideFirst) {
            int cardW = 48;
            int cardH = 64;
            int spacing = 22;
            int totalW = hand.size() * spacing + cardW;
            int startX = centerX - totalW / 2;

            for (int i = 0; i < hand.size(); i++) {
                boolean hidden = hideFirst && i == 0;
                drawCard(g2, startX + i * spacing, y, cardW, cardH, hand.get(i), hidden);
            }
        }

        private void drawCard(Graphics2D g2, int x, int y, int w, int h, Card c, boolean hidden) {
            g2.setColor(hidden ? Color.DARK_GRAY : Color.WHITE);
            g2.fillRoundRect(x, y, w, h, 12, 12);

            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y, w, h, 12, 12);

            if (!hidden) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString(c.rank.symbol, x + 6, y + 18);
                g2.drawString(c.suit.symbol, x + 6, y + 36);
            }
        }
    }

    /* ========================= CARD MODEL ========================= */

    enum Suit {
        HEARTS("♥"), DIAMONDS("♦"), CLUBS("♣"), SPADES("♠");
        final String symbol;
        Suit(String s) { symbol = s; }
    }

    enum Rank {
        ACE("A", 11),
        TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5),
        SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9),
        TEN("10", 10), JACK("J", 10), QUEEN("Q", 10), KING("K", 10);

        final String symbol;
        final int value;
        Rank(String s, int v) { symbol = s; value = v; }
    }

    static class Card {
        final Suit suit;
        final Rank rank;

        Card(Suit s, Rank r) {
            suit = s;
            rank = r;
        }

        int value() {
            return rank.value;
        }
    }

    static class Deck {
        private final List<Card> cards = new ArrayList<>();

        Deck() {
            for (Suit s : Suit.values())
                for (Rank r : Rank.values())
                    cards.add(new Card(s, r));
        }

        void shuffle() {
            Collections.shuffle(cards);
        }

        Card draw() {
            return cards.remove(cards.size() - 1);
        }
    }
}
