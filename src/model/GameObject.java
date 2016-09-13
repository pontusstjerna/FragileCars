package model;

import java.awt.*;

/**
 * Created by pontu on 2016-04-08.
 */
public interface GameObject {
    void update(double deltaTime);
    void paint(Graphics2D g, double scale, int scaleX);
}
