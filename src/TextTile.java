import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TextTile extends JPanel {

    private String header = "-";
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

    public TextTile() {

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
        double sw = 0;
        int fontSize = 26;
        for (fontSize = 26; fontSize > 10; fontSize--) {
            Font font = new Font("Raleway", Font.PLAIN, fontSize);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            sb = fm.getStringBounds(header, g2d);
            sw = sb.getWidth();
            if (sw < textBox.getWidth()) {
                break;
            }
        }

        double sh = sb.getHeight();

        double xShift = (int) ((textBox.getWidth() - sw) / 2);
        double yShift = (int) ((textBox.getHeight() - sh) / 2) + 20;

        g2d.drawString(header, (int) (textBox.getX() + xShift), (int) (textBox.getY() + yShift));
    }

    public void setHeader(String s) {
        header = s;
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
}
