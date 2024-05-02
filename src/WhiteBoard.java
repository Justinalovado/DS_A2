//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.image.BufferedImage;
//
//public class WhiteBoard extends JPanel {
//    private BufferedImage canvasImage;
//    private Graphics2D g2d;
//    private int currentX, currentY, oldX, oldY;
//    private DrawingMode mode = DrawingMode.FREE_DRAW;
//    private Color drawColor = Color.BLACK;
//
//    public WhiteBoard() {
//        setLayout(new BorderLayout());
//        initCanvas();
//        initToolPanel();
//    }
//
//    private void initCanvas() {
//        canvasImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
//        g2d = canvasImage.createGraphics();
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setColor(drawColor);
//        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//
//        JPanel canvasPanel = new JPanel() {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                g.drawImage(canvasImage, 0, 0, null);
//            }
//        };
//        canvasPanel.setPreferredSize(new Dimension(800, 600));
//        canvasPanel.addMouseListener(new MouseAdapter() {
//            public void mousePressed(MouseEvent e) {
//                System.out.println("Mouse pressed");
//                oldX = e.getX();
//                oldY = e.getY();
//            }
//
//            public void mouseReleased(MouseEvent e) {
//                System.out.println("Mouse released: Mode=" + mode);
//                if (mode == DrawingMode.CIRCLE || mode == DrawingMode.RECTANGLE || mode == DrawingMode.OVAL || mode == DrawingMode.LINE) {
//                    drawShape(e);
//                }
//            }
//        });
//        canvasPanel.addMouseMotionListener(new MouseMotionAdapter() {
//            public void mouseDragged(MouseEvent e) {
//                currentX = e.getX();
//                currentY = e.getY();
//                if (mode == DrawingMode.FREE_DRAW || mode == DrawingMode.ERASE) {
//                    drawFreeform(e);
//                }
//                oldX = currentX;
//                oldY = currentY;
//            }
//        });
//        add(canvasPanel, BorderLayout.CENTER);
//    }
//
//    private void initToolPanel() {
//        JPanel toolPanel = new JPanel();
//        JButton colorButton = new JButton("Color");
//        colorButton.addActionListener(e -> {
//            Color newColor = JColorChooser.showDialog(this, "Choose a color", drawColor);
//            if (newColor != null) {
//                drawColor = newColor;
//                g2d.setColor(drawColor);
//            }
//        });
//
//        JComboBox<DrawingMode> modeComboBox = new JComboBox<>(DrawingMode.values());
//        modeComboBox.addActionListener(e -> mode = (DrawingMode) modeComboBox.getSelectedItem());
//
//        toolPanel.add(colorButton);
//        toolPanel.add(modeComboBox);
//        add(toolPanel, BorderLayout.SOUTH);
//    }
//
//    private void drawFreeform(MouseEvent e) {
//        g2d.drawLine(oldX, oldY, currentX, currentY);
//        repaint();
//    }
//
//    private void drawShape(MouseEvent e) {
//        switch (mode) {
//            case CIRCLE:
//                System.out.println("Drawing circle");
//                int diameter = Math.max(Math.abs(e.getX() - oldX), Math.abs(e.getY() - oldY));
//                g2d.drawOval(oldX, oldY, diameter, diameter);
//                break;
//            case RECTANGLE:
//                g2d.drawRect(Math.min(oldX, e.getX()), Math.min(oldY, e.getY()),
//                        Math.abs(e.getX() - oldX), Math.abs(e.getY() - oldY));
//                break;
//            case OVAL:
//                g2d.drawOval(Math.min(oldX, e.getX()), Math.min(oldY, e.getY()),
//                        Math.abs(e.getX() - oldX), Math.abs(e.getY() - oldY));
//                break;
//            case LINE:
//                g2d.drawLine(oldX, oldY, e.getX(), e.getY());
//                break;
//        }
//        repaint();
//    }
//
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Whiteboard");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(new WhiteBoard());
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
//
//    enum DrawingMode {
//        FREE_DRAW, ERASE, CIRCLE, RECTANGLE, OVAL, LINE
//    }
//}

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class WhiteBoard extends JPanel {
    private BufferedImage background;
    private Shape tmpShape;
    private Point startPoint = null;
    private Graphics2D g2d;

    public WhiteBoard() {
        this.background = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
//        setBackground(Color.WHITE);
//        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.g2d = this.background.createGraphics();
        this.g2d.setBackground(Color.WHITE);
//        this.g2d.setPaint(Color.WHITE);
//        g2d.drawImage(background,0, 0, null);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                g2d.setStroke(new BasicStroke(3.0f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drawEllipse(startPoint, e.getPoint());
                startPoint = null;
                repaint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawTemporaryEllipse(startPoint, e.getPoint());
            }
        });
    }

    private void drawTemporaryEllipse(Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.abs(start.x - end.x);
        int height = Math.abs(start.y - end.y);
        this.tmpShape = new Ellipse2D.Double(x, y, width,height);
        repaint();
    }

    private void drawEllipse(Point start, Point end) {

        g2d.setColor(Color.BLACK);
        if (start != null && end != null) {
            int x = Math.min(start.x, end.x);
            int y = Math.min(start.y, end.y);
            int width = Math.abs(start.x - end.x);
            int height = Math.abs(start.y - end.y);
            g2d.drawOval(x, y, width, height);
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0,0, null);
        if(tmpShape != null){
            Graphics2D gd = (Graphics2D) g;
            gd.draw(this.tmpShape);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Minimal Whiteboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new WhiteBoard());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}



