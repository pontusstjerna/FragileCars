package model.carcontrollers;

import java.awt.*;

/**
 * Created by pontu on 2016-04-08.
 */
public interface CarController {
    void update(double deltaTime);
    void paint(Graphics2D g, double scale, int scaleX);
}
