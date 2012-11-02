import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class InfoPanel extends JPanel implements ActionListener {
    private MainPanel panel;
    private JComboBox lifeBox;
    private JTextArea infoArea;

    public InfoPanel(MainPanel panel) {
        setPreferredSize(new Dimension(480, 100));
        this.panel = panel;

        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new BorderLayout());

        lifeBox = new JComboBox();
        lifeBox.setEditable(true);
        loadLife();
        lifeBox.addActionListener(this);
        tempPanel.add(lifeBox, BorderLayout.NORTH);

        infoArea = new JTextArea();
        infoArea.setPreferredSize(new Dimension(480, 100));
        infoArea.setLineWrap(true);
        tempPanel.add(infoArea, BorderLayout.CENTER);
        infoArea.setText("Welcome to Lifegame World!");

        add(tempPanel);
    }

    public String getLifeName() {
        return (String) lifeBox.getEditor().getItem();
    }

    public String getLifeInfo() {
        return infoArea.getText();
    }

    public void setLifeInfo(String str) {
        infoArea.setText(str);
    }

    public void loadLife() {
        lifeBox.addItem("");

        File lifeDir = new File("life");
        String[] lifeNameList = lifeDir.list();
        if (lifeNameList == null) return;
        for (int i = 0; i < lifeNameList.length; i++) {
            lifeBox.addItem(lifeNameList[i]);
        }
    }

    public void addLife(String lifeName) {
        lifeBox.addItem(lifeName);
    }

    public void actionPerformed(ActionEvent e) {
        String filename = (String) lifeBox.getSelectedItem();
        if (filename.equals("")) {
            setLifeInfo("");
            panel.clear();
        } else {
            panel.loadLife(filename);
        }
    }
}
