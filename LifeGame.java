import java.awt.*;
import javax.swing.*;

public class LifeGame extends JFrame {
    public LifeGame() {
        setTitle("Conway's game of life");
        setResizable(false);

        MainPanel mainPanel = new MainPanel();
        Container contentPane = getContentPane();
        contentPane.add(mainPanel, BorderLayout.CENTER);

        ControlPanel ctrlPanel = new ControlPanel(mainPanel);
        contentPane.add(ctrlPanel, BorderLayout.NORTH);

        InfoPanel infoPanel = new InfoPanel(mainPanel);
        contentPane.add(infoPanel, BorderLayout.SOUTH);

        mainPanel.setInfoPanel(infoPanel);
        pack();
    }

    public static void main(String[] args) {
        LifeGame frame = new LifeGame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
