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
    private static final String standardFrameTitleTex = "Type h for help. Type h again to hide help.";
    private ArrayList<QuestionSelection> categories = new ArrayList();
    private final ArrayList<ColorPair> colorPairs = new ArrayList();
    private String currentQandA = null;
    private Tile currentTile = null;
    private boolean answerIsShown = false;
    private BufferedImage bgImage;
    private boolean drawHelp = false;
    private boolean playSound = false;
    private Clip clip = null;
    private boolean editMode = false;
    private Icon qaIcon;
    private Icon categoryIcon;
    private String lastDirectory = null;
    private int maxRows = 0;
    private int currentColor = 0;

    public Jeopardy(int width) {

        System.out.println("Jeopardy ...");
        technicalInits();

        try {
            qaIcon = new ImageIcon(ImageIO.read(getClass().getResource("qaIconSmall.png")));
            categoryIcon = new ImageIcon(ImageIO.read(getClass().getResource("categoryIcon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        initColors();
        System.out.println("Jeopardy after init colors ...");

        readBackgroundImage();
        System.out.println("Jeopardy after read back ground image ...");

        readSettings();
        System.out.println("Jeopardy after read settings ...");

        playJeopardySong(playSound);
        System.out.println("Jeopardy after paly sound ...");

        readJeopardy(null);
        System.out.println("Jeopardy after read questions and answers ... ready to go!");

//        initOnNoData();
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
        Color darkPink = new Color(147, 20, 103);
        Color lightPink = new Color(239, 172, 200);
        colorPairs.add(new ColorPair(darkOrange, lightOrange));
        colorPairs.add(new ColorPair(darkBlue, lightBlue));
        colorPairs.add(new ColorPair(darkRed, lightRed));
        colorPairs.add(new ColorPair(darkGreen, lightGreen));
        colorPairs.add(new ColorPair(darkPink, lightPink));
        colorPairs.add(new ColorPair(Color.GRAY, Color.LIGHT_GRAY));
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
        try {
            bgImage = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readJeopardy(File file) {

        Scanner scanner = null;
        URL url = null;
        try {
            if (file != null) {
                scanner = new Scanner(file);
            } else {
                url = getClass().getResource("default.jeopardy");
                scanner = new Scanner(url.openStream());
            }
        } catch (Exception e) {
            System.out.println("Classloader failed");
            e.printStackTrace();
        }

        categories = new ArrayList();


        int lineCount = 0;
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("#")) {
                    lineCount = 0;
                }
                lineCount++;
                if (lineCount > maxRows) {
                    maxRows = lineCount;
                }
            }
            maxRows /= 2;
            maxRows += 2;
            if (file != null) {
                scanner = new Scanner(file);
            } else {
                scanner = new Scanner(url.openStream());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        QuestionSelection qs = null;
        int tileCount = 0;
        int myCount = 0;
        currentColor = 0;

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
                qs = new QuestionSelection(line, maxRows);
                qs.setColors(colorPairs.get(currentColor++));
                categories.add(qs);
                continue;
            }
            myCount++;

            Tile tt = qs.getTile(tileCount);
            if (myCount % 2 == 0) {
                tileCount++;
                tt.setAnswer(line);
            } else {
                tt.setQuestion(line);
            }
        }
    }

    private void addCategory() {
        System.out.println("maxRows: " + maxRows);
        QuestionSelection qs = new QuestionSelection("new category", maxRows);
        qs.printAllTiles();
        System.out.println("qs tiles: " + qs.getAllTiles().size());
        qs.setColors(colorPairs.get(currentColor++));
        categories.add(qs);

    }

    private void writeJeopardy(File file) {

        PrintWriter pw;
        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < categories.size(); i++) {
            QuestionSelection qc = categories.get(i);
            ArrayList<Tile> at = qc.getAllTiles();
            for (int j = 0; j < at.size() - 1; j++) {
                Tile t = at.get(j);
                if (j == 0) {
                    pw.append("#    " + t.getHeaderText() + "\n\n");
                    continue;
                }
                t.write(pw);
            }
        }
        pw.close();
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
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        drawBgImage(g2d);

        if (drawHelp) {
            drawHelp(g2d);
            return;
        }

        if (currentQandA == null) {
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
        double rectHeight = getHeight() * 0.2;
        if (rectHeight < 100) {
            rectHeight = 100;
        }
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
        FontMetrics fm = null;
        for (fontSize = 100; fontSize > 10; fontSize--) {
            Font font = new Font("Raleway", Font.PLAIN, fontSize);
            g2d.setFont(font);
            fm = g2d.getFontMetrics();
            sb = fm.getStringBounds(currentQandA, g2d);

            sw = sb.getWidth();
            if (sw < qaBox.getWidth() - (qaBox.getWidth() * 0.02)) {
                break;
            }
        }
        double sh = sb.getHeight();

        double xShift = (int) ((qaBox.getWidth() - sw) / 2);
        double yShift = (int) ((qaBox.getHeight() - sh) / 2) + fm.getAscent();

        g2d.setColor(new Color(0, 0, 40));
        g2d.drawString(currentQandA, (int) (qaBox.getX() + xShift), (int) (qaBox.getY() + yShift));
    }

    private void showQuestionSelection(Graphics2D g2d) {

        double xGap = 10;
        double yGap = 10;
        double qsWidth = (getWidth() - ((categories.size() + 1) * xGap)) / categories.size();
        double qsHeight = (getHeight() - (maxRows * yGap)) / (maxRows - 1);

        double xPos = xGap;
        double yPos = yGap;
        QuestionSelection qs;
        for (int i = 0; i < categories.size(); i++) {
            qs = categories.get(i);
            System.out.println("category: " + i);

            qs.paint(g2d, xPos, yPos, yGap, qsWidth, qsHeight);
            xPos += qsWidth + xGap;
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
        g2d.drawString("e", xPos, yPos);
        g2d.drawString("Toggle EDIT mode", xPos + xShift, yPos);
        yPos += yShift;
        g2d.drawString("o", xPos, yPos);
        g2d.drawString("Open", xPos + xShift, yPos);
        yPos += yShift;
        g2d.drawString("p", xPos, yPos);
        g2d.drawString("Play sound on | off", xPos + xShift, yPos);
        yPos += yShift;
        g2d.drawString("s", xPos, yPos);
        g2d.drawString("Safe as", xPos + xShift, yPos);
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

        if (answerIsShown && !editMode) {
            currentQandA = null;
            currentTile = null;
            answerIsShown = false;
            repaint();
            return;
        }

        if (currentTile == null) {
            for (int i = 0; i < categories.size(); i++) {
                QuestionSelection qs = categories.get(i);
                currentTile = qs.findTile(e.getPoint());
                if (currentTile != null) {
                    break;
                }
            }
        }

        if (currentTile != null) {

            if (editMode) {

                handleEdit(e.isShiftDown());
                repaint();
                return;
            }

            if( currentTile.getIsHeader() ) {
                currentTile = null;
                repaint();
                return;
            }

            if (currentQandA != null) {
                currentQandA = currentTile.getAnswer();
                currentTile.setHit(true);
                answerIsShown = true;
            } else {
                currentQandA = currentTile.getQuestion();
            }
        }
        repaint();
    }

    private void handleEdit(boolean shiftDown) {


        if (shiftDown) {
            String s = currentTile.getHeaderText();
            AutoSelectingTextField header = new AutoSelectingTextField("");
            header.setPreferredSize(new Dimension(300, 42));
            header.setText(currentTile.getHeaderText());
            Object[] message = {
                    " Category:", header,
            };
            int opt = JOptionPane.showConfirmDialog(null,
                    message,
                    "Enter new category!",
                    JOptionPane.OK_CANCEL_OPTION,
                    0, categoryIcon);
            if (opt == JOptionPane.OK_OPTION) {
                currentTile.setHeaderText(header.getText());
            }
            currentTile.setHit(false);
            currentTile = null;
            return;
        }

        if( currentTile.getIsHeader() ) {
            currentTile = null;
            return;
        }

        AutoSelectingTextField question = new AutoSelectingTextField("");
        question.setPreferredSize(new Dimension(800, 42));
        AutoSelectingTextField answer = new AutoSelectingTextField("");
        answer.setPreferredSize(new Dimension(800, 42));
        question.setText(currentTile.getQuestion());
        question.requestFocus();
        answer.setText(currentTile.getAnswer());
        Object[] message = {
                " Question:", question,
                " Answer:", answer
        };
        int opt = JOptionPane.showConfirmDialog(null,
                message,
                "Enter question and answer!",
                JOptionPane.OK_CANCEL_OPTION,
                0, qaIcon);
        if (opt == JOptionPane.OK_OPTION) {
            String nq = question.getText();
            String na = answer.getText();
            currentTile.setQuestion(nq);
            currentTile.setAnswer(na);
        }
        currentTile.setHit(false);
        currentTile = null;
    }

    private void clearAllTiles() {

        QuestionSelection qs;
        for (int i = 0; i < categories.size(); i++) {
            qs = categories.get(i);
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
                currentQandA = null;
                break;
            case KeyEvent.VK_C:
                addCategory();
                break;
            case KeyEvent.VK_E:
                editMode = !editMode;
                if (editMode) {
                    clearAllTiles();
                    frame.setTitle("!!! Edit Mode !!!");
                } else {
                    frame.setTitle(standardFrameTitleTex);
                }
                break;
            case KeyEvent.VK_H:
                drawHelp = !drawHelp;
                break;
            case KeyEvent.VK_P:
                playSound = !playSound;
                playJeopardySong(playSound);
                break;
            case KeyEvent.VK_O: {
                handleOpen();
                break;
            }
            case KeyEvent.VK_S:
                handleSafe();
                break;
        }
        repaint();
    }

    private void handleOpen() {

        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        FileDialog dialog = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
        if (lastDirectory != null) {
            dialog.setDirectory(lastDirectory);
        }
        dialog.setVisible(true);
        String directoryName = dialog.getDirectory();
        String fileName = dialog.getFile();
        lastDirectory = directoryName;
        File inFile = new File(dialog.getDirectory() + dialog.getFile());
        readJeopardy(inFile);
        System.out.println("directory plus file: " + directoryName + fileName);
    }

    private void handleSafe() {

        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        FileDialog dialog = new FileDialog(frame, "Safe file as ...", FileDialog.SAVE);
        if (lastDirectory != null) {
            dialog.setDirectory(lastDirectory);
        }
        dialog.setVisible(true);
        String directoryName = dialog.getDirectory();
        String fileName = dialog.getFile();
        lastDirectory = directoryName;
        System.out.println("directory plus file: " + directoryName + fileName);
        File outFile = new File(dialog.getDirectory() + dialog.getFile());
        writeJeopardy(outFile);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        repaint();
    }

    public static void main(String[] args) {

        int width = 1500;
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 40);
        frame.setTitle(standardFrameTitleTex);
        frame.setSize(width, 700);
        Jeopardy jeo = new Jeopardy(width);
        frame.add(jeo);
        frame.setMinimumSize(new Dimension(800, 400));
        frame.setVisible(true);
    }

}
