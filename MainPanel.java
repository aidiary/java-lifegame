import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.applet.*;
import java.util.*;

public class MainPanel extends JPanel
    implements Runnable, MouseListener, KeyListener {

    // field size (unit: pixel)
    private static final int WIDTH = 480;
    private static final int HEIGHT = 480;

    // field size (unit: cell)
    private static final int ROW = 96;
    private static final int COL = 96;

    // cell size
    private static final int CS = 5;

    // state of the life
    private static final int DEAD = 0;
    private static final int ALIVE = 1;

    private static final int SLEEP = 100;
    private static final double RAND_LIFE = 0.3;

    private int[][] field;
    private int generation;

    private Thread thread;
    private Random rand;

    private AudioClip saveAudio;
    private InfoPanel infoPanel;

    // row and col of key position
    private int r, c;

    public MainPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        rand = new Random();

        // init field
        field = new int[ROW][COL];
        clear();

        // init key position
        r = c = ROW / 2 - 1;

        // load sound clip
        saveAudio = Applet.newAudioClip(
            getClass().getResource("save.wav"));

        addMouseListener(this);
        addKeyListener(this);
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread = null;
        }
    }

    public void clear() {
        generation = 0;
        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++)
                field[i][j] = DEAD;
        repaint();
    }

    public void step() {
        // Lifegame Rules
        // 1. Any live cell with fewer than two live neighbours dies,
        //    as if caused by under-population.
        // 2. Any live cell with two or three live neighbours lives on to the next generation.
        // 3. Any live cell with more than three live neighbours dies,
        //    as if by overcrowding.
        // 4. Any dead cell with exactly three live neighbours becomes a live cell,
        //    as if by reproduction.
        int[][] nextField = new int[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                switch (around(i, j)) {
                    case 2 :
                        nextField[i][j] = field[i][j];
                        break;
                    case 3 :
                        nextField[i][j] = ALIVE;
                        break;
                    default :
                        nextField[i][j] = DEAD;
                        break;
                }
            }
        }
        field = nextField;
        generation++;
        repaint();
    }

    public void run() {
        while (thread != null) {
            step();
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void paintComponent(Graphics g) {
        // draw life
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (field[i][j] == ALIVE) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.BLACK);
                }
                g.fillRect(j * CS, i * CS, CS, CS);
            }
        }

        // draw center line
        g.setColor(Color.RED);
        g.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
        g.drawLine(0, HEIGHT / 2, WIDTH, HEIGHT / 2);

        // draw key position
        g.setColor(Color.WHITE);
        g.drawRect(c * CS, r * CS, CS, CS);

        // draw generation
        g.drawString("generation: " + generation, 2, 10);
    }

    public int getGeneration() {
        return generation;
    }

    public void mousePressed(MouseEvent e) {
        requestFocus();

        int x = e.getX();
        int y = e.getY();

        c = x / CS;
        r = y / CS;

        if (field[r][c] == DEAD) {
            field[r][c] = ALIVE;
        } else {
            field[r][c] = DEAD;
        }

        repaint();
    }

    public void mouseClicked(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }

    // count live cells around (i, j)
    private int around(int i, int j) {
        // at the edge of the field
        if (i == 0 || i == ROW - 1 || j == 0 || j == COL - 1)
            return 0;

        int sum = 0;
        sum += field[i - 1][j - 1];  // upper left
        sum += field[i][j - 1];      // left
        sum += field[i + 1][j - 1];  // lower left
        sum += field[i - 1][j];      // up
        sum += field[i + 1][j];      // down
        sum += field[i - 1][j + 1];  // upper right
        sum += field[i][j + 1];      // right
        sum += field[i + 1][j + 1];  // lower right

        return sum;
    }

    public void setInfoPanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    public void saveLife() {
        String lifeName = infoPanel.getLifeName();
        String lifeInfo = infoPanel.getLifeInfo();

        if (lifeName.equals("")) {
            JOptionPane.showMessageDialog(this, "no name", "no name",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            File lifeFile = new File("life" + File.separator + lifeName);
            if (lifeFile.exists()) {
                int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Do you want to overwrite? : " + lifeFile.getName(),
                    "overwrite?",
                    JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.NO_OPTION)
                    return;
            } else {
                infoPanel.addLife(lifeName);
            }

            PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(lifeFile)));
            pr.println(lifeInfo);
            // save positions of live cells
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COL; j++) {
                    if (field[i][j] == ALIVE) {
                        pr.println(i + " " + j);
                    }
                }
            }
            saveAudio.play();
            pr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLife(String filename) {
        clear();

        try {
            BufferedReader br = new BufferedReader(
                new FileReader("life" + File.separator + filename));
            String lifeInfo = br.readLine();
            infoPanel.setLifeInfo(lifeInfo);
            // load positions of live cells
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer parser = new StringTokenizer(line);
                while (parser.hasMoreTokens()) {
                    int i = Integer.parseInt(parser.nextToken());
                    int j = Integer.parseInt(parser.nextToken());
                    field[i][j] = ALIVE;
                }
            }
            repaint();
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void randLife() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (rand.nextDouble() < RAND_LIFE) {
                    field[i][j] = ALIVE;
                }
            }
        }
        repaint();
    }

    public boolean isFocusable() {
        return true;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
        case KeyEvent.VK_LEFT :
            c--;
            if (c < 0) c = 0;
            break;
        case KeyEvent.VK_RIGHT :
            c++;
            if (c > COL - 1) c = COL - 1;
            break;
        case KeyEvent.VK_UP :
            r--;
            if (r < 0) r = 0;
            break;
        case KeyEvent.VK_DOWN :
            r++;
            if (r > ROW - 1) r = ROW - 1;
            break;
        case KeyEvent.VK_SPACE :
            if (field[r][c] == ALIVE) {
                field[r][c] = DEAD;
            } else {
                field[r][c] = ALIVE;
            }
            break;
        }
        repaint();
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
}
