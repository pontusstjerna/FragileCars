package se.nocroft.model.misc;

import java.awt.*;

/**
 * Created by pontu on 2016-09-24.
 */
public class Skidmark {
    int x, y;
    int width;
    final int height = 15;
    double rotation;

    public Skidmark(int x, int y, int width, double rotation) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.rotation = rotation;
    }

    public void paint(Graphics2D g, double scale, int scaleX) {
        g.rotate(rotation, x * scale + scaleX, y * scale);
        g.fillRect((int) ((x - width / 2) * scale) + scaleX, (int) ((y - height / 2) * scale), (int) (width * scale),
                (int) (height * scale));
        g.rotate(-rotation, x * scale + scaleX, y * scale);
    }
}
