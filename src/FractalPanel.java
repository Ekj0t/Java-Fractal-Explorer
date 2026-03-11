import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FractalPanel extends JPanel {

    private final int THREADS = Runtime.getRuntime().availableProcessors();

    private int[][] iterations;

    private boolean colorCycling = true;

    private BufferedImage image;

    private float colorOffset = 0f;

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

        iterations = new int[image.getWidth()][image.getHeight()];

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

                renderAsync();
            }
        });

        setFocusable(true);

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_C) {

                    colorCycling = !colorCycling;

                    repaint();
                }
            }
        });

//        startPaletteCycling();
        startColorAnimation();

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

        renderAsync();
    }

    private void renderFractal() {

        int width = image.getWidth();
        int height = image.getHeight();

        int maxIter = 200;

        Thread[] threads = new Thread[THREADS];

        int rowsPerThread = height / THREADS;

        for (int t = 0; t < THREADS; t++) {

            int startY = t * rowsPerThread;
            int endY = (t == THREADS - 1) ? height : startY + rowsPerThread;

            threads[t] = new Thread(() -> {

                for (int x = 0; x < width; x++) {

                    for (int y = startY; y < endY; y++) {

                        double c_re = minRe + x * (maxRe - minRe) / width;
                        double c_im = minIm + y * (maxIm - minIm) / height;

                        int iter = Mandelbrot.getIterations(c_re, c_im, maxIter);

                        int color;

                        if (iter == maxIter) {
                            color = Color.BLACK.getRGB();
                        } else {
                            float hue = ((iter / 256f) + colorOffset) % 1.0f;
                            color = Color.HSBtoRGB(hue, 1f, iter / (iter + 8f));
                        }

                        iterations[x][y] = iter;
                    }
                }

            });

            threads[t].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
    }

    private void renderAsync() {

        new Thread(() -> {
            renderFractal();
            repaint();
        }).start();
    }

//    private void startPaletteCycling() {
//
//        new Thread(() -> {
//
//            while (true) {
//
//                colorOffset += 0.01f;
//
//                repaint();
//
//                try {
//                    Thread.sleep(40);
//                } catch (InterruptedException ignored) {}
//            }
//
//        }).start();
//    }

    private void startColorAnimation() {

        new Thread(() -> {

            while (true) {

                if (colorCycling) {
                    colorOffset += 0.002f;
                    repaint();
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
            }

        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                int iter = iterations[x][y];

                int color;

                if (iter == maxIter) {
                    color = Color.BLACK.getRGB();
                } else {

                    float hue = ((iter / 256f) + colorOffset) % 1f;
                    color = Color.HSBtoRGB(hue, 1f, iter / (iter + 8f));
                }

                image.setRGB(x, y, color);
            }
        }

        g.drawImage(image, 0, 0, null);
    }
}