import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Jeopardy extends JButton implements MouseListener, KeyListener {

    private static JFrame frame;
    private ArrayList<QuestionSelection> qsContainer = new ArrayList();
    private final ArrayList<ColorPair> colorPairs = new ArrayList();
    private String currentQuestionOrAnswer = null;
    private Font font = new Font("Raleway", Font.PLAIN, 42);
    private TextTile currentTextTile = null;
    private boolean answerIsShown = false;
    private BufferedImage bgImage;
    private boolean drawHelp = false;
    private boolean playSound = false;
    private Clip clip = null;

    public Jeopardy(int width) {

        addMouseListener(this);
        addKeyListener(this);

        initColors();

        readBackgroundImage();

        readSettings();

        /// this also reads the data since we have one resize event at the beginning
        addResizeListener();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            writeSettings();
        }));

        playJeopardySong(playSound);

        initOnNoData();
    }

    private void initOnNoData() {

//        int xPos = xGap;
//        int width = (w - xGap) / 4 - xGap;
//
//        QuestionSelection qs = new QuestionSelection("Powers", xPos, width);
//        qs.setColors(darkOrange, lightOrange);
//        qsContainer.add(qs);
//
//        xPos += width + xGap;
//        qs = new QuestionSelection("Triangles", xPos, width);
//        qs.setColors(darkBlue, lightBlue);
//        qsContainer.add(qs);
//
//        xPos += width + xGap;
//        qs = new QuestionSelection("Prisms", xPos, width);
//        qs.setColors(darkGreen, lightGreen);
//        qsContainer.add(qs);
//
//        xPos += width + xGap;
//        qs = new QuestionSelection("Integrals", xPos, width);
//        qs.setColors(darkRed, lightRed);
//        qsContainer.add(qs);
    }

    private void addResizeListener() {

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                readQuestionsAndAnswers(getWidth(), getHeight());
                repaint();
            }
        });
    }

    private void initColors() {
        Color darkBlue = new Color(66, 116, 177);
        Color lightBlue = new Color(194, 215, 236);
        Color darkOrange = new Color(246, 194, 66);
        Color lightOrange = new Color(249, 218, 120);
        Color darkGreen = new Color(94, 129, 63);
        Color lightGreen = new Color(202, 224, 184);
        Color darkRed = new Color(176, 35, 24);
        Color lightRed = new Color(224, 193, 194);
        Color darkPink = new Color(197, 20, 103);
        Color lightPink = new Color(229, 172, 199);
        colorPairs.add(new ColorPair(darkOrange, lightOrange));
        colorPairs.add(new ColorPair(darkBlue, lightBlue));
        colorPairs.add(new ColorPair(darkRed, lightRed));
        colorPairs.add(new ColorPair(darkGreen, lightGreen));
        colorPairs.add(new ColorPair(darkPink, lightPink));
    }

    private void readSettings() {

        try {
            String uh = System.getProperty("user.home");
            FileInputStream f = new FileInputStream(uh + "/Jeopardy.bin");
            ObjectInputStream os = new ObjectInputStream(f);

            try {
                Point pos = (Point) os.readObject();
                frame.setLocation(pos);
                Dimension dim = (Dimension) os.readObject();
                frame.setSize(dim);
                playSound = os.readBoolean();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            os.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(playSound);
    }

    private void writeSettings() {

        System.out.println("write settings");
        try {
            String uh = System.getProperty("user.home");
            FileOutputStream f = new FileOutputStream(uh + "/Jeopardy.bin");
            ObjectOutputStream os = new ObjectOutputStream(f);

            os.writeObject(frame.getLocation());
            os.writeObject(frame.getSize());

            os.writeBoolean(playSound);

            os.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readBackgroundImage() {
        URL url = getClass().getResource("clouds.jpg");
        System.out.println(url);
        try {
            bgImage = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readQuestionsAndAnswers(int widthIn, int heightIn) {

        File file = new File("jeopardy.txt");

        qsContainer = new ArrayList();

        Scanner scanner = null;

        int qsCount = 0;
        int maxRows = 0;
        int lineCount = 0;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("#")) {
                    lineCount = 0;
                    qsCount++;
                }
                lineCount++;
                if (lineCount > maxRows) {
                    maxRows = lineCount;
                }
            }
            maxRows /= 2;
            maxRows += 2;
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        double xGap = 20;
        double yGap = 10;
        double xPos = xGap;
        double qsWidth = (widthIn - ((qsCount + 1) * xGap)) / qsCount;
        double qsHeight = (heightIn - (maxRows * yGap)) / (maxRows - 1);

        QuestionSelection qs = null;
        int tileCount = 0;
        int myCount = 0;
        int currentColor = 0;

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine().trim();
            if (line.length() == 0) {
                continue;
            }
            if (line.startsWith("#")) {
                tileCount = 1;
                myCount = 0;
                qs = new QuestionSelection(line.substring(1), (int) xPos, (int) qsWidth, (int) qsHeight, maxRows);
                qs.setColors(colorPairs.get(currentColor++));
                qsContainer.add(qs);
                xPos += qsWidth + xGap;
                continue;
            }
            myCount++;

            TextTile tt = qs.getTile(tileCount);
            if (myCount % 2 == 0) {
                tileCount++;
                tt.setAnswer(line);
            } else {
                tt.setQuestion(line);
            }
        }
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        drawBgImage(g2d);

        if (drawHelp) {
            drawHelp(g2d);
            return;
        }

        if (currentQuestionOrAnswer == null) {
            showQuestionSelection(g2d);
        } else {
            showQuestionOrAnser(g2d);
        }
    }

    private void drawBgImage(Graphics2D g2d) {
        g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void showQuestionOrAnser(Graphics2D g2d) {


        int inset = 40;
        int rectWidth = getWidth() - (2 * inset);
        double rectHeight = 100;
        Rectangle2D.Double rect = new Rectangle2D.Double(inset, (getHeight() - rectHeight) / 2, rectWidth, rectHeight);

        g2d.setColor(Color.WHITE);
        g2d.fill(rect);

        g2d.setColor(new Color(0, 0, 40));

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D sb = fm.getStringBounds(currentQuestionOrAnswer, g2d);

        double sw = sb.getWidth();
        double sh = sb.getHeight();

        double xShift = (int) ((rect.getWidth() - sw) / 2);
        double yShift = (int) ((rect.getHeight() - sh) / 2) + (sh / 1.3);

        g2d.drawString(currentQuestionOrAnswer, (int) (rect.getX() + xShift), (int) (rect.getY() + yShift));
    }

    private void showQuestionSelection(Graphics2D g2d) {
        QuestionSelection qs;
        for (int i = 0; i < qsContainer.size(); i++) {
            qs = qsContainer.get(i);
            qs.paint(g2d);
        }
    }

    private void clearBackground(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
    }

    private void drawHelp(Graphics2D g2d) {

        int yPos = 36;
        int xPos = 10;
        int yShift = 36;
        int xShift = 200;
        g2d.setColor(new Color(0, 0, 40));
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Use mouse to click on tiles. The question will appear. Click again and the answer will appear. Again and you back to selection", xPos, yPos);
        yPos += 2 * yShift;
        g2d.drawString("s", xPos, yPos);
        g2d.drawString("Toggle sound on | off", xPos + xShift, yPos);
        yPos += yShift;
        g2d.drawString("t", xPos, yPos);
        g2d.drawString("Test", xPos + xShift, yPos);
        yPos += yShift;
    }

    public void playJeopardySong(boolean play) {

        if (play == false) {
            if (clip != null) {
                clip.stop();
                clip = null;
            }
            return;
        }
        try {
            URL url = getClass().getResource("Jeopardy.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (answerIsShown) {
            currentQuestionOrAnswer = null;
            currentTextTile = null;
            answerIsShown = false;
            repaint();
            return;
        }

        if (currentTextTile == null) {
            for (int i = 0; i < qsContainer.size(); i++) {
                QuestionSelection qs = qsContainer.get(i);
                currentTextTile = qs.findTile(e.getPoint());
                if (currentTextTile != null) {
                    break;
                }
            }
        }

        if (currentTextTile != null) {
            if (currentQuestionOrAnswer != null) {
                currentQuestionOrAnswer = currentTextTile.getAnswer();
                answerIsShown = true;
            } else {
                currentQuestionOrAnswer = currentTextTile.getQuestion();
            }
        }
        repaint();
    }

    private void clearAllTiles() {

        QuestionSelection qs;
        for (int i = 0; i < qsContainer.size(); i++) {
            qs = qsContainer.get(i);
            qs.clearAllTiles();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                clearAllTiles();
                currentQuestionOrAnswer = null;
                break;
            case KeyEvent.VK_H:
                drawHelp = !drawHelp;
                break;
            case KeyEvent.VK_S:
                playSound = !playSound;
                playJeopardySong(playSound);
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {

        int width = 1500;
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 40);
        frame.setTitle("Type h for help. Type h again to hide help.");
        frame.setSize(width, 700);
        Jeopardy jeo = new Jeopardy(width);
        frame.add(jeo);
        frame.setVisible(true);
    }

}
