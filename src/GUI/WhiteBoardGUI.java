package GUI;

import driver.Announcer;

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

    private boolean isManager;
    private boolean drawLock = false;

    public WhiteBoardGUI(boolean isManager){
        this.isManager = isManager;
        setLayout(new BorderLayout());
        initStates();
        initCanvas();
        initListeners();
        initToolPanel();
    }

    // inits /////////////////////////////////////////////////////////////////////////////////
    private void initListeners() {
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

    private void initStates() {
        // init states
        this.color = Color.black;
        this.strokeWidth = 2;
        this.stroke = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        this.mode = DrawMode.FREE_DRAW;
    }

    public void initCanvas() {
        // initialize canvas
        this.img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = this.img.createGraphics();
        g2d.fillRect(0, 0, 800, 600);
        g2d.dispose();
    }

    private void initToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton setColorBtn = new JButton("Color");
        setColorBtn.addActionListener(e ->{
            Color newColor = JColorChooser.showDialog(this, "Pick a color", color);
            if (newColor != null){
                color = newColor;
            }
        });

        JComboBox<DrawMode> drawModeMenu = new JComboBox<>(DrawMode.values());
        drawModeMenu.addActionListener(e -> {
            mode = (DrawMode) drawModeMenu.getSelectedItem();
        });

        JSlider strokeWidthSelector = new JSlider(2,30,2);
        strokeWidthSelector.addChangeListener(e -> {
            strokeWidth = strokeWidthSelector.getValue();
        });


        if (isManager) toolPanel.add(new MenuGUI(this));

        toolPanel.add(setColorBtn);
        toolPanel.add(drawModeMenu);
        toolPanel.add(strokeWidthSelector);
        add(toolPanel, BorderLayout.NORTH);
    }

    private void onMouseDrag(MouseEvent e){
        if (drawLock) return;
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
        if (drawLock) return;
        if (mode == DrawMode.FREE_DRAW || mode == DrawMode.ERASE){
            quickDrawShape(start, e.getPoint());
        } else if (mode == DrawMode.TEXT) {
            String input = JOptionPane.showInputDialog(this, "Enter your text:", "Input Dialog", JOptionPane.PLAIN_MESSAGE);
            if (input != null) {
                drawTxt(e.getPoint(), input);
            }
        } else {
            quickDrawShape(start, e.getPoint());
        }
    }

    // inits /////////////////////////////////////////////////////////////////////////////////
    // ops /////////////////////////////////////////////////////////////////////////////////

    private void drawTxt(Point p, String txt){
        Graphics2D g2d = getBrush();
        g2d.drawString(txt, p.x, p.y);
        Announcer.broadCaster.broadcastDrawTxt(p, color, txt);
        g2d.dispose();
        repaint();
    }

    public void updateDrawTxt(Point p, Color c, String txt){
        Graphics2D g2d = getBrush(c);
        g2d.drawString(txt, p.x, p.y);
        g2d.dispose();
        repaint();
    }

    private void updatePreviewShape(Point a, Point b) {
        this.stroke = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
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

    /**
     * local variation of drawShape, using default color & stroke
     * Should not handle drawing text
     */
    private void quickDrawShape(Point a, Point b){
        Graphics2D g2d = getBrush();
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
        Announcer.broadCaster.broadcastDrawShape(a, b, strokeWidth, color, mode);
        g2d.dispose();
        repaint();
    }

    /**
     * for remote access, drawing shape
     */
    public void drawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape){
        SwingUtilities.invokeLater(() -> {
            Graphics2D g2d = getBrush(strokeWidth, color);
            if (shape == DrawMode.FREE_DRAW || shape == DrawMode.ERASE || shape == DrawMode.LINE) {
                if (shape == DrawMode.ERASE) g2d.setPaint(Color.WHITE);
                g2d.drawLine(a.x, a.y, b.x, b.y);
            } else {
                int x = Math.min(a.x, b.x);
                int y = Math.min(a.y, b.y);
                int width = Math.abs(a.x - b.x);
                int height = Math.abs(a.y - b.y);
                if (shape == DrawMode.RECTANGLE) {
                    g2d.draw(new Rectangle2D.Double(x, y, width, height));
                } else if (shape == DrawMode.ELLIPSE) {
                    g2d.draw(new Ellipse2D.Double(x, y, width, height));
                } else if (shape == DrawMode.CIRCLE) {
                    int diameter = Math.min(width, height);
                    g2d.draw(new Ellipse2D.Double(x, y, diameter, diameter));
                }
            }
            g2d.dispose();
            repaint();
        });
    }

    public void overhaulBoard(BufferedImage img){
        this.img = img;
        repaint();
    }

    // ops /////////////////////////////////////////////////////////////////////////////////
    // util ////////////////////////////////////////////////////////////////////////////////////////
    private Graphics2D getBrush(){
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(color);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        return g2d;
    }

    private Graphics2D getBrush(Color color){
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(color);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        return g2d;
    }

    private Graphics2D getBrush(float strokeWidth){
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(color);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        return g2d;
    }

    private Graphics2D getBrush(float strokeWidth, Color color){
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(color);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        return g2d;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
        if (previewShape != null){
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(color);
            g2.setStroke(stroke);
            g2.draw(previewShape);
        }
    }

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    public void setDrawLock(boolean bool){
        drawLock = bool;
    }

    public boolean getDrawLock(){
        return drawLock;
    }
}
