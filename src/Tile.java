import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;

public class Tile {

    private String headerText = null;
    private Color textColor = new Color(00, 00, 40);
    private Rectangle2D.Double textBox = new Rectangle2D.Double();
    private final String DEFAULT_QUESTION = "no question set";
    private final String DEFAULT_ANSWER = "no answer set";
    private String question = DEFAULT_QUESTION;
    private String answer = DEFAULT_ANSWER;
    private boolean isHeader = false;

    public boolean getHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    private boolean hit = false;

    public Tile() {

    }

    public void paint(Graphics2D g2d, double xPos, double yPos, double bw, double bh, Color col) {

        if (hit) {
            g2d.setColor(Color.GRAY);
        } else if (question.contains(DEFAULT_QUESTION) || answer.contains(DEFAULT_ANSWER)) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(col);
        }
        textBox.setRect(xPos, yPos, bw, bh);

        g2d.fill(textBox);
        g2d.setColor(textColor);

        Rectangle2D sb;
        double sw = 0.0;
        double sh = 0.0;

        int fontSize;
        FontMetrics fm = null;
        for (fontSize = 200; fontSize > 10; fontSize--) {
            Font font = new Font("Raleway", Font.PLAIN, fontSize);
            g2d.setFont(font);
            fm = g2d.getFontMetrics();
            sb = fm.getStringBounds(headerText, g2d);
            sw = sb.getWidth();
            sh = sb.getHeight();
            if (sw < bw - (bw * 0.20) && sh < bh - (bh * 0.20)) {
                break;
            }
        }

        double xShift = (bw - sw) / 2.0;
        double yShift = (bh - sh) / 2.0 + fm.getAscent();

        g2d.drawString(headerText, (int) (textBox.getX() + xShift), (int) (textBox.getY() + yShift));
    }

    public void setHeaderText(String s) {
        headerText = s;
    }

    public void setRect(int xPos, int yPos, int width, int height) {

        textBox.setRect(xPos, yPos, width, height);
    }

    public boolean getHit(Point point) {
        return textBox.contains(point);
    }

    public void print() {
        System.out.println("box:      " + textBox);
        System.out.println("Question: " + question);
        System.out.println("Answer:   " + answer);
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setQuestion(String in) {
        question = in;
    }

    public void setAnswer(String in) {
        answer = in;
    }

    public void write(PrintWriter pw) {

        pw.append(question + "\n");
        pw.append(answer + "\n");
        pw.append("\n");
    }

    public String getHeaderText() {
        return headerText;
    }

    public boolean getIsHeader() {
        return isHeader;
    }

    public void setIsHeader(boolean b) {
        question = "";
        answer= "";
        isHeader = b;
    }
}
