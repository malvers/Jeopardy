import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Tile extends JPanel {

    private String textOnDisplay = "-";
    private Color textColor = new Color(00, 00, 40);
    private Rectangle2D.Double textBox = new Rectangle2D.Double();
    private String question = "no question";
    private String answer = "no answer";

    public boolean getHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    private boolean hit = false;

    public Tile() {

    }

    public void paint(Graphics2D g2d, Color col) {

        if (hit) {
            g2d.setColor(Color.GRAY);
        } else {
            g2d.setColor(col);
        }
        g2d.fill(textBox);
        g2d.setColor(textColor);

        Rectangle2D sb = null;
        double sw = 0.0;
        double sh = 0.0;
        double bw = textBox.getWidth();
        double bh = textBox.getHeight();
        int fontSize;
        FontMetrics fm = null;
        for (fontSize = 100; fontSize > 10; fontSize--) {
            Font font = new Font("Raleway", Font.PLAIN, fontSize);
            g2d.setFont(font);
            fm = g2d.getFontMetrics();
            sb = fm.getStringBounds(textOnDisplay, g2d);
            sw = sb.getWidth();
            sh = sb.getHeight();
            if (sw < bw - (bw * 0.20) && sh < bh - (bh * 0.20)) {
                break;
            }
        }
        double xShift = (bw - sw) / 2.0;
        double yShift = (bh - sh) / 2.0 + fm.getAscent();

        g2d.drawString(textOnDisplay, (int) (textBox.getX() + xShift), (int) (textBox.getY() + yShift));
    }

    public void setTextOnDisplay(String s) {
        textOnDisplay = s;
    }

    public void setRect(int xPos, int yPos, int width, int height) {

        textBox.setRect(xPos, yPos, width, height);
    }

    public boolean getHit(Point point) {
        if (hit) {
            return true;
        }
        hit = textBox.contains(point);
        return hit;
    }

    public void print() {
//        System.out.println("Header:   " + header);
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

    public boolean isInit() {

        return !question.contains("no question") && !answer.contains("no answer");
    }

    public void write(PrintWriter pw) {

        pw.append(question + "\n");
        pw.append(answer + "\n");
        pw.append("\n");
    }

    public String getTextOnDisplay() {
        return textOnDisplay;
    }
}
