import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Jeopardy extends JButton implements MouseListener, KeyListener {

    private ArrayList<QuestionSelection> qsContainer = new ArrayList();
    private final ArrayList<ColorPair> colorPairs = new ArrayList();
    private String currentQuestionOrAnswer = null;
    private Font font = new Font("Raleway", Font.PLAIN, 42);
    private TextTile currentTextTile = null;
    private boolean answerIsShown = false;
    private BufferedImage bgImage;

    public Jeopardy(int width) {

        addMouseListener(this);
        addKeyListener(this);

        initColors();

        readBackgroundImage();

        /// this also reads the data since we have one resize event at the beginning
        addResizeListener();

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

        System.out.println("number columns:  " + qsCount + " max number rows: " + maxRows);

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

        clearBackground(g2d);

        drawBgImage(g2d);

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
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {

        int width = 1600;
        Jeopardy jeo = new Jeopardy(width);

        JFrame f = new JFrame();
        f.add(jeo);
        f.setLocation(100, 40);
        f.setSize(width, 700);
        f.setVisible(true);
    }

}
