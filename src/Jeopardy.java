import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
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
    private String currentQA = null;
    private TextTile currentTextTile = null;
    private boolean answerIsShown = false;
    private BufferedImage bgImage;
    private boolean drawHelp = false;
    private boolean playSound = false;
    private Clip clip = null;

    public Jeopardy(int width) {

        System.out.println("Jeopardy ...");
        technicalInits();

        initColors();
        System.out.println("Jeopardy after init colors ...");

        readBackgroundImage();
        System.out.println("Jeopardy after read back ground image ...");

        readSettings();
        System.out.println("Jeopardy after read settings ...");

        playJeopardySong(playSound);
        System.out.println("Jeopardy after paly sound ...");

        readQuestionsAndAnswers(width, 400);
        System.out.println("Jeopardy after read questions and answers ...");

        initOnNoData();
    }

    private void technicalInits() {
        addMouseListener(this);
        addKeyListener(this);
        addResizeListener();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            writeSettings();
        }));
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
                /// TODO: change this shit
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
                line = line.substring(1);
                line = line.trim();
                qs = new QuestionSelection(line, (int) xPos, (int) qsWidth, (int) qsHeight, maxRows);
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

    private void drawRotatedText(Graphics2D g2d, Color cb, String text, int xPos, int yPos, int rotate) {

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-45), 0, 0);
        Font font = new Font("Raleway", Font.PLAIN, 40);
        Font rotatedFont = font.deriveFont(affineTransform);

        double boxWidth = 190.0;
        double boxHeight = 66.0;
        Rectangle2D.Double box = new Rectangle2D.Double(xPos, yPos, boxWidth, boxHeight);
        Path2D.Double path = new Path2D.Double();
        path.append(box, false);

        affineTransform = new AffineTransform();

//        double ax = boxWidth / 2.0 + xPos;
//        double ay = boxHeight / 2.0 + yPos;
        double ax = xPos - 12;
        double ay = yPos - 46;

        affineTransform.rotate(Math.toRadians(-45), ax, ay);
        affineTransform.translate(-48, -54);
        path.transform(affineTransform);

        g2d.setColor(cb);
        g2d.fill(path);
//        g2d.setColor(Color.BLUE);
//        g2d.fillRect((int) (ax - 2), (int) (ay - 2), 4, 4);

        g2d.setFont(rotatedFont);
//        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, xPos, yPos);
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

//        testing(g2d);

        drawBgImage(g2d);

        if (drawHelp) {
            drawHelp(g2d);
            return;
        }

        if (currentQA == null) {
            showQuestionSelection(g2d);
        } else {
            showQuestionOrAnswer(g2d);
        }
    }

    private void testing(Graphics2D g2d) {


        AffineTransform affineTransform = new AffineTransform();
        double xPos = 100.0;
        double yPos = getHeight() / 2.0;
        double boxWidth = 200;
        double boxHeight = 80;
        Rectangle2D.Double box = new Rectangle2D.Double(xPos, yPos, boxWidth, boxHeight);
        Path2D.Double path = new Path2D.Double();
        path.append(box, false);
        g2d.setColor(Color.BLACK);
        g2d.draw(path);

//        affineTransform.rotate(Math.toRadians(-45), 250, 150);
        double ax = boxWidth / 2.0 + xPos;
        double ay = boxHeight / 2.0 + yPos;
        System.out.println("yPos: " + yPos + " ax: " + ax + " ay: " + ay);
        affineTransform.rotate(Math.toRadians(-45), ax, ay);
        path.transform(affineTransform);

        g2d.fillRect((int) (ax - 2), (int) (ay - 2), 4, 4);
        g2d.setColor(Color.RED);
        g2d.draw(path);
    }

    private void drawBgImage(Graphics2D g2d) {
        g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void showQuestionOrAnswer(Graphics2D g2d) {

        int inset = 140;
        int rectWidth = getWidth() - (2 * inset);
        double rectHeight = 100;
        int yPos = (int) ((getHeight() - rectHeight) / 2.0);
        Rectangle2D.Double qaBox = new Rectangle2D.Double(inset, yPos, rectWidth, rectHeight);

        g2d.setColor(Color.WHITE);
        g2d.fill(qaBox);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.draw(qaBox);

        Color cb = new Color(94, 129, 63);
        String text = " Answer ";
        if (!answerIsShown) {
            text = "Question";
            cb = new Color(160, 0, 0);
        }
        drawRotatedText(g2d, cb, text, (int) (inset / 1.8), (int) (yPos + (rectHeight / 1.8)), -45);

        int fontSize;
        Rectangle2D sb = null;
        double sw = 10;
        for (fontSize = 42; fontSize > 10; fontSize--) {
            Font font = new Font("Raleway", Font.PLAIN, fontSize);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            sb = fm.getStringBounds(currentQA, g2d);

            sw = sb.getWidth();
            if (sw < qaBox.getWidth() - (qaBox.getWidth() * 0.02)) {
                break;
            }
        }
        double sh = sb.getHeight();

        double xShift = (int) ((qaBox.getWidth() - sw) / 2);
        double yShift = (int) ((qaBox.getHeight() - sh) / 2) + (sh / 1.3);

        g2d.setColor(new Color(0, 0, 40));
        g2d.drawString(currentQA, (int) (qaBox.getX() + xShift), (int) (qaBox.getY() + yShift));
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
            currentQA = null;
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
            if (currentQA != null) {
                currentQA = currentTextTile.getAnswer();
                answerIsShown = true;
            } else {
                currentQA = currentTextTile.getQuestion();
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
                currentQA = null;
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
