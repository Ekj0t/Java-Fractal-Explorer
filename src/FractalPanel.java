import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FractalPanel extends JPanel {

    private BufferedImage image;

    private int lastX;
    private int lastY;

    private double minRe = -2;
    private double maxRe = 2;
    private double minIm = -2;
    private double maxIm = 2;

    private int maxIter = 200;

    public FractalPanel() {

        int width = 800;
        int height = 800;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        renderFractal();

        addMouseWheelListener(e -> {
            double zoomFactor = (e.getPreciseWheelRotation() > 0) ? 1.2 : 0.8;
            zoomAt(e.getX(), e.getY(), zoomFactor);
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;

                double scaleRe = (maxRe - minRe) / image.getWidth();
                double scaleIm = (maxIm - minIm) / image.getHeight();

                minRe -= dx * scaleRe;
                maxRe -= dx * scaleRe;

                minIm -= dy * scaleIm;
                maxIm -= dy * scaleIm;

                lastX = e.getX();
                lastY = e.getY();

                new Thread(() -> {
                    renderFractal();
                    repaint();
                }).start();
            }
        });

    }

    private void zoomAt(int px, int py, double zoomFactor) {

        int width = image.getWidth();
        int height = image.getHeight();

        double mouseRe = minRe + px * (maxRe - minRe) / width;
        double mouseIm = minIm + py * (maxIm - minIm) / height;

        minRe = mouseRe + (minRe - mouseRe) * zoomFactor;
        maxRe = mouseRe + (maxRe - mouseRe) * zoomFactor;

        minIm = mouseIm + (minIm - mouseIm) * zoomFactor;
        maxIm = mouseIm + (maxIm - mouseIm) * zoomFactor;

        new Thread(() -> {
            renderFractal();
            repaint();
        }).start();
    }

    private void renderFractal() {

        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                double c_re = minRe + x * (maxRe - minRe) / width;
                double c_im = minIm + y * (maxIm - minIm) / height;

                int iter = Mandelbrot.getIterations(c_re, c_im, maxIter);

                int color;

                if (iter == maxIter) {
                    color = Color.BLACK.getRGB();
                } else {
                    color = Color.HSBtoRGB(iter / 256f, 1, iter / (iter + 8f));
                }

                image.setRGB(x, y, color);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}