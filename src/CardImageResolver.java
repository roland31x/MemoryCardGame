import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class CardImageResolver {
    public static ImageIcon getIconForValue(int value, int size) {
        Shape shape = getShapeByValue(value, size);
        Color color = getColorByValue(value);

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, size, size);

        g2.setColor(color);
        g2.fill(shape);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.draw(shape);

        g2.dispose();
        return new ImageIcon(image);
    }

    private static Shape getShapeByValue(int value, int size) {
        int shapeIndex = value % 8;
        int padding = size / 6;
        int w = size - 2 * padding;
        int h = size - 2 * padding;
        int x = padding;
        int y = padding;

        switch (shapeIndex) {
            case 0: // Circle
                return new Ellipse2D.Float(x, y, w, h);
            case 1: // Square
                return new Rectangle(x, y, w, h);
            case 2: // Triangle
                Polygon triangle = new Polygon();
                triangle.addPoint(size / 2, y);             // top
                triangle.addPoint(x, y + h);                // bottom left
                triangle.addPoint(x + w, y + h);            // bottom right
                return triangle;
            case 3: // Hexagon
                Polygon hex = new Polygon();
                for (int i = 0; i < 6; i++) {
                    double angle = Math.toRadians(60 * i - 30);
                    int cx = size / 2 + (int)(w / 2 * Math.cos(angle));
                    int cy = size / 2 + (int)(h / 2 * Math.sin(angle));
                    hex.addPoint(cx, cy);
                }
                return hex;
            case 4: // Diamond
                Polygon diamond = new Polygon();
                diamond.addPoint(size / 2, y);             // top
                diamond.addPoint(x, size / 2);             // left
                diamond.addPoint(size / 2, y + h);         // bottom
                diamond.addPoint(x + w, size / 2);         // right
                return diamond;
            case 5: // Pentagon
                Polygon pent = new Polygon();
                for (int i = 0; i < 5; i++) {
                    double angle = Math.toRadians(72 * i - 90);
                    int cx = size / 2 + (int)(w / 2 * Math.cos(angle));
                    int cy = size / 2 + (int)(h / 2 * Math.sin(angle));
                    pent.addPoint(cx, cy);
                }
                return pent;
            case 6: // Star
                return createStar(size / 2, size / 2, w / 2, w / 4, 5);
            case 7: // Cross
                int barWidth = w / 3;
                int barLength = w;
                Area cross = new Area(new Rectangle(x + (w - barWidth) / 2, y, barWidth, barLength));
                cross.add(new Area(new Rectangle(x, y + (h - barWidth) / 2, barLength, barWidth)));
                return cross;
            default:
                return new Rectangle(x, y, w, h);
        }
    }

    private static Color getColorByValue(int value) {
        int group = value / 4;
        switch (group) {
            case 0:
                return new Color(255, 140, 0); // Orange
            case 1:
                return new Color(50, 205, 50); // Green
            case 2:
                return new Color(70, 130, 180); // Steel Blue
            case 3:
                return new Color(220, 20, 60); // Crimson
            default:
                return Color.GRAY;
        }
    }

    private static Shape createStar(int centerX, int centerY, int outerRadius, int innerRadius, int points) {
        double angle = Math.PI / points;
        Polygon star = new Polygon();
        for (int i = 0; i < 2 * points; i++) {
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            double a = i * angle;
            int x = centerX + (int)(r * Math.sin(a));
            int y = centerY - (int)(r * Math.cos(a));
            star.addPoint(x, y);
        }
        return star;
    }
}