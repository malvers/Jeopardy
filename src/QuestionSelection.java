import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class QuestionSelection extends JPanel {

    private String title = "-";
    private ArrayList<Tile> theTiles = new ArrayList();
    private Color dark;
    private Color bright;

    private int xPos = 10;
    private int yPos = 10;
    private int yGap = 10;


    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private int width = 10;

    @Override
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int height = 84;

    public QuestionSelection(String t, int xPos, int w, int h, int maxRows) {

        width = w;
        height = h;
        title = t;
        for (int i = 0; i < maxRows; i++) {

            Tile tile = new Tile();
            if (i == 0) {
                tile.setTextOnDisplay(title);
            }
            if (i > 0) {
                tile.setTextOnDisplay("" + i * 100);
            }
            tile.setRect(xPos, yPos, width, height);
            yPos += height + yGap;
            theTiles.add(tile);
            add(tile);
        }
    }


    @Override
    public void paint(Graphics g) {

        for (int i = 0; i < theTiles.size(); i++) {
            Graphics2D g2d = (Graphics2D) g;
            Tile tile = theTiles.get(i);
            if (tile.getQuestion().contains("no question") && i > 0) {
                continue;
            }
            if (tile.getAnswer().contains("no answer") && i > 0) {
                continue;
            }
            if (i > 0) {
                tile.paint(g2d, bright);
            } else {
                tile.paint(g2d, dark);
            }
        }
    }

    public void setColors(ColorPair p) {
        this.dark = p.one;
        this.bright = p.two;
    }

    public Tile findTile(Point point) {

        /// start at one because the header tile is just the header
        for (int i = 1; i < theTiles.size(); i++) {

            Tile tt = theTiles.get(i);
            if (tt.getHit() || !tt.isInit()) {
                continue;
            }

            if (tt.getHit(point)) {
                tt.print();
                return tt;
            }
        }
        return null;
    }

    public Tile getTile(int index) {
        return theTiles.get(index);
    }

    public void clearAllTiles() {

        for (int i = 0; i < theTiles.size(); i++) {
            Tile tt = theTiles.get(i);
            tt.setHit(false);
        }
    }
}
