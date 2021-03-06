package se.nocroft.view;

import se.nocroft.model.Racetrack;
import se.nocroft.util.ImageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public class UISurface extends JPanel {
    private Racetrack track;
    private BufferedImage guiBg, guiBgScaled;
    private double scale;
    private BufferedImage[] numbersBig;

    public UISurface(Racetrack track, double scale) {
        this.track = track;
        this.scale = scale;
        guiBg = ImageHandler.loadImage("gui_bg");
        guiBgScaled = scaleImage(guiBg);
        initNumbers();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        paintGuiBG(g2d);
        displayTime(g2d);
        paintCarConsoles(g2d);
        displayFPS(g2d);
        displayLaps(g2d);
    }

    private void paintGuiBG(Graphics2D g) {
        g.drawImage(guiBgScaled, offsetX(), 0, this);
    }

    private void displayTime(Graphics2D g) {
        if (track.getTime() < 0)
            return;

        g.drawImage(numbersBig[(int) (track.getTime() / (600 * 1000) % 10)], (int) ((60) * scale) + offsetX(),
                (int) (330 * scale), this);
        g.drawImage(numbersBig[(int) (track.getTime() / (60 * 1000) % 10)], (int) ((120) * scale) + offsetX(),
                (int) (330 * scale), this);
        g.drawImage(numbersBig[(int) (track.getTime() / (10 * 1000) % 6)], (int) ((205) * scale) + offsetX(),
                (int) (330 * scale), this);
        g.drawImage(numbersBig[(int) (track.getTime() / 1000 % 10)], (int) (268 * scale) + offsetX(),
                (int) (330 * scale), this);
    }

    Color txtColor = Color.white;
    Font font = new Font(null, 0, 20);

    private void displayLaps(Graphics2D g) {
        int startX = 35;
        double startY = guiBg.getHeight() * 0.435;
        int distY = 170;
        int height = 50;

        g.setColor(txtColor);
        g.setFont(font);
        for (int i = 0; i < track.getDrawableCars().length; i++) {
            g.drawString(track.getDrawableCars()[i].getName(), (int) (startX * scale) + offsetX(),
                    (int) ((startY + i * distY) * scale));

            g.drawString("Laps: " + track.getDrawableCars()[i].getLaps() + "/" + track.getMaxLaps(),
                    (int) (startX * scale) + offsetX(), (int) ((startY + i * distY + height) * scale));
            if (track.getDrawableCars()[i].getFinished() != 0)
                g.drawString("Finished!", (int) (startX * scale) + offsetX(),
                        (int) ((startY + i * distY + height * 2) * scale));
        }
    }

    private void displayFPS(Graphics2D g) {
        g.drawString("FPS: " + track.getFPS(), (int) (scale * 30) + offsetX(),
                (int) (guiBg.getHeight() * 0.99 * scale));
    }

    private final Color consoleColor = Color.black;

    private void paintCarConsoles(Graphics2D g) {
        int startX = 30;
        double startY = guiBg.getHeight() * 0.4;
        int width = 330;
        int height = 150;
        int padding = 23;

        g.setColor(consoleColor);

        for (int i = 0; i < track.getDrawableCars().length; i++) {
            g.fillRect((int) (startX * scale) + offsetX(), (int) ((startY + i * (height + padding)) * scale),
                    (int) (width * scale), (int) (height * scale));
        }
    }

    private BufferedImage scaleImage(BufferedImage unscaled) {
        int w = unscaled.getWidth();
        int h = unscaled.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(unscaled, after);

        return after;
    }

    private void initNumbers() {
        numbersBig = new BufferedImage[10];
        BufferedImage numbers = ImageHandler.loadImage("guiNumbers");
        int width = numbers.getWidth() / 10;
        for (int i = 0; i < numbersBig.length; i++) {
            numbersBig[i] = scaleImage(ImageHandler.cutImage(numbers, 0, i, width, numbers.getHeight()));
        }
    }

    private int offsetX() {
        return (int) ((MainWindow.WORLD_WIDTH / 4) - guiBgScaled.getWidth() * scale);
    }
}
