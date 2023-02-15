import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class QuestionSelection extends JPanel {

    private String title = "-";
    private ArrayList<Tile> theTiles = new ArrayList();
    private Color dark;
    private Color bright;

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

    public QuestionSelection(String title, int maxRows) {

        for (int i = 0; i < maxRows; i++) {

            Tile tile = new Tile();
            if (i == 0) {
                tile.setHeaderText(title);
                tile.setIsHeader(true);
            }
            if (i > 0) {
                tile.setHeaderText("" + i * 100);
            }
            theTiles.add(tile);
        }
    }

    public void paint(Graphics2D g2d, double xPos, double yPos, double yGap, double w, double height) {

        for (int i = 0; i < theTiles.size(); i++) {

            System.out.println("tile: " + i);
            Tile tile = theTiles.get(i);
            /// TODO: find a solution
//            if (tile.getQuestion().contains("no question") && i > 0) {
//                continue;
//            }
//            if (tile.getAnswer().contains("no answer") && i > 0) {
//                continue;
//            }
            if (i > 0) {
                tile.paint(g2d, xPos, yPos, w, height, bright);
            } else {
                tile.paint(g2d, xPos, yPos, w, height, dark);
            }
            yPos += height + yGap;
        }
    }

    public void setColors(ColorPair p) {
        this.dark = p.one;
        this.bright = p.two;
    }

    public Tile findTile(Point point) {

        for (int i = 0; i < theTiles.size(); i++) {

            Tile tt = theTiles.get(i);

            if (tt.getHit(point)) {
//                System.out.println("found: " + tt.getHeaderText());
                return tt;
            }
        }
//        System.out.println("return nix");
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

    public ArrayList<Tile> getAllTiles() {
        return theTiles;
    }

    public void printAllTiles() {

        for (int i = 0; i < theTiles.size(); i++) {

            theTiles.get(i).print();
        }
    }
}
