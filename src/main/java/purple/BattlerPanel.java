package purple;

import javax.swing.*;
import java.awt.*;

public class BattlerPanel extends JPanel {

    private final JLabel nameLabel = new JLabel();
    private final JLabel statusLabel = new JLabel();
    private final JLabel hpLabel = new JLabel();
    private final JProgressBar hpBar = new JProgressBar(0, 100);

    public BattlerPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.WHITE));

        // Top: Name + Status
		JPanel top = new JPanel(new GridLayout(1, 2, 10, 0)); // 10px horizontal gap, 0 vertical
		top.setOpaque(false);
		top.add(nameLabel);
		top.add(statusLabel);

        // Middle: HP text
        JPanel middle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        middle.setOpaque(false);
        middle.add(new JLabel("HP: "));
        middle.add(hpLabel);

        // Bottom: Health bar
        hpBar.setStringPainted(false);
        hpBar.setPreferredSize(new Dimension(200, 18));

        add(top, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);
        add(hpBar, BorderLayout.SOUTH);
    }

    public void updateBattler(Battler b) {
        nameLabel.setText("Level "+b.level+" "+b.nickname);
        statusLabel.setText(b.status);

        // Display "hp/mhp" literally
        hpLabel.setText(b.hp + " / " + b.mhp);

        // Compute percentage
        int percent = (int) ((b.hp / (float) b.mhp) * 100);
        hpBar.setValue(percent);

        // Color logic
        if (percent <= 33) {
            hpBar.setForeground(Color.RED);
        } else if (percent <= 66) {
            hpBar.setForeground(Color.YELLOW);
        } else {
            hpBar.setForeground(Color.GREEN);
        }
    }
}
