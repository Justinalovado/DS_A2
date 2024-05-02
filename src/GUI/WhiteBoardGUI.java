package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class WhiteBoardGUI extends JPanel {
    private BufferedImage img;

    private Color color;
    private float strokeWidth;
    private BasicStroke stroke;
    private DrawMode mode;

    private Shape previewShape = null;
    private Point start = null;
    private Point end = null;


    enum DrawMode{
        FREE_DRAW, ERASE, LINE, RECTANGLE, CIRCLE, ELLIPSE, TEXT
    }

    public WhiteBoardGUI(){
        // init states
        this.color = Color.black;
        this.stroke = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        this.mode = DrawMode.RECTANGLE;

        // initialize canvas
        this.img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = this.img.createGraphics();
        g2d.fillRect(0, 0, 800, 600);
        g2d.dispose();

        // attach listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                start = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseRelease(e);
                previewShape = null;
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseDrag(e);
            }
        });
    }

    private void updatePreviewShape(Point a, Point b) {
        if (mode == DrawMode.LINE){
            previewShape = new Line2D.Double(a.x, a.y, b.x, b.y);
        } else {
            int x = Math.min(a.x, b.x);
            int y = Math.min(a.y, b.y);
            int width = Math.abs(a.x - b.x);
            int height = Math.abs(a.y - b.y);
            if (mode == DrawMode.RECTANGLE){
                previewShape = new Rectangle2D.Double(x, y, width, height);
            } else if (mode == DrawMode.ELLIPSE){
                previewShape = new Ellipse2D.Double(x, y, width, height);
            } else if (mode == DrawMode.CIRCLE){
                int diameter = Math.min(width, height);
                previewShape = new Ellipse2D.Double(x, y, diameter, diameter);
            }
        }
        repaint();
    }


    private void onMouseDrag(MouseEvent e){
        if (mode == DrawMode.FREE_DRAW || mode == DrawMode.ERASE){
            quickDrawShape(start, e.getPoint());
            start = e.getPoint();
        } else if (mode == DrawMode.TEXT) {
            // pass (optional: preview txt box)
        } else {
            // show preview -> by updating previewShape
            updatePreviewShape(start, e.getPoint());
        }
    }

    private void onMouseRelease(MouseEvent e){
        if (mode == DrawMode.FREE_DRAW || mode == DrawMode.ERASE){
            quickDrawShape(start, e.getPoint());
        } else if (mode == DrawMode.TEXT) {
            // draw text
        } else {
            quickDrawShape(start, e.getPoint());
        }
    }

    /**
     * local variation of drawShape, using default color & stroke
     * Should not handle drawing text
     */
    private void quickDrawShape(Point a, Point b){
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(color);
        if (mode == DrawMode.FREE_DRAW || mode == DrawMode.ERASE || mode == DrawMode.LINE){
            if (mode == DrawMode.ERASE) g2d.setPaint(Color.WHITE);
            g2d.drawLine(a.x, a.y, b.x, b.y);
        } else {
            int x = Math.min(a.x, b.x);
            int y = Math.min(a.y, b.y);
            int width = Math.abs(a.x - b.x);
            int height = Math.abs(a.y - b.y);
            if (mode == DrawMode.RECTANGLE){
                g2d.draw(new Rectangle2D.Double(x, y, width, height));
            } else if (mode == DrawMode.ELLIPSE){
                g2d.draw(new Ellipse2D.Double(x, y, width, height));
            } else if (mode == DrawMode.CIRCLE){
                int diameter = Math.min(width, height);
                g2d.draw(new Ellipse2D.Double(x, y, diameter, diameter));
            }
        }
        g2d.dispose();
        repaint();
    }

    /**
     * TODO: Not in use, for remote access, drawing shape
     */
    private void drawShape(Point a, Point b, Color color, BasicStroke stroke, DrawMode shape){
        return;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
        if (previewShape != null){
            Graphics2D g2 = (Graphics2D) g;
            g2.draw(previewShape);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Minimal Whiteboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new WhiteBoardGUI());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
