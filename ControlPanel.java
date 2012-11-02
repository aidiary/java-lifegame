import javax.swing.*;
import java.awt.event.*;

public class ControlPanel extends JPanel implements ActionListener {
    private MainPanel panel;

    private JButton startButton;
    private JButton stopButton;
    private JButton stepButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton randButton;

    public ControlPanel(MainPanel panel) {
        this.panel = panel;

        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        stepButton = new JButton("Step");
        clearButton = new JButton("Clear");
        saveButton = new JButton("Save");
        randButton = new JButton("Rand");

        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        stepButton.addActionListener(this);
        clearButton.addActionListener(this);
        saveButton.addActionListener(this);
        randButton.addActionListener(this);

        add(startButton);
        add(stopButton);
        add(stepButton);
        add(clearButton);
        add(saveButton);
        add(randButton);

        stopButton.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            stepButton.setEnabled(false);
            clearButton.setEnabled(false);
            panel.start();
        } else if (e.getSource() == stopButton) {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            stepButton.setEnabled(true);
            clearButton.setEnabled(true);
            panel.stop();
        } else if (e.getSource() == stepButton) {
            panel.step();
        } else if (e.getSource() == clearButton) {
            panel.clear();
        } else if (e.getSource() == saveButton) {
            panel.saveLife();
        } else if (e.getSource() == randButton) {
            panel.randLife();
        }
    }
}
