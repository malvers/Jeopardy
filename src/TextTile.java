import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TextTile extends JPanel {

    private String header = "-";
    private Color textColor = new Color(00, 00, 40);
    private Font font = new Font("Raleway", Font.PLAIN, 26);
    private Rectangle2D.Double rect = new Rectangle2D.Double();
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
        g2d.fill(rect);
        g2d.setColor(textColor);

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D sb = fm.getStringBounds(header, g2d);

        double sw = sb.getWidth();
        double sh = sb.getHeight();

        double xShift = (int) ((rect.getWidth() - sw) / 2);
        double yShift = (int) ((rect.getHeight() - sh) / 2) + 20;

        g2d.drawString(header, (int) (rect.getX() + xShift), (int) (rect.getY() + yShift));
    }

    public void setHeader(String s) {
        header = s;
    }

    public void setRect(int xPos, int yPos, int width, int height) {

        rect.setRect(xPos, yPos, width, height);
    }

    public boolean getHit(Point point) {
        if (hit) {
            return true;
        }
        hit = rect.contains(point);
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
