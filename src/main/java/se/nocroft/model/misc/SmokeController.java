package se.nocroft.model.misc;

import se.nocroft.model.GameObject;
import se.nocroft.model.cars.FragileCar;

import java.awt.*;
import java.util.Random;

/**
 * Created by pontu on 2016-09-12.
 */
public class SmokeController implements GameObject {

    FragileCar car;

    private final int MAX_SMOKE = 300;
    private int index = 0;
    private Random rand;

    // Round buffer
    private SmokeParticle[] smoke = new SmokeParticle[MAX_SMOKE];

    public SmokeController(FragileCar car) {
        this.car = car;
        rand = new Random();

        final int MAX_ALPHA = 50;
        int gray = rand.nextInt(30) + 30;
        Color[] colors = new Color[MAX_ALPHA];
        for (int i = 0; i < MAX_ALPHA; i++) {
            int alpha = Math.min((int) (MAX_ALPHA * 3.0 / i), MAX_ALPHA);
            colors[i] = new Color(gray, gray, gray, alpha);
            // System.out.println(alpha);
        }

        for (int i = 0; i < MAX_SMOKE; i++) {
            double acc = Math.max(200, car.getAcceleration() * 2 / 3);
            smoke[i] = new SmokeParticle(car.getRelX(20, 80), car.getRelY(20, 80), acc, -car.getHeading(), colors,
                    rand);
        }
    }

    @Override
    public void update(double deltaTime) {
        double acc = Math.max(200, car.getAcceleration() * 2 / 3);

        smoke[index].reSet(car.getRelX(20, 80), car.getRelY(20, 80), acc, -car.getHeading());

        index = (index + 1) % MAX_SMOKE;

        // Update all particles
        for (SmokeParticle s : smoke) {
            s.update(deltaTime);
        }
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

        // Paint all particles
        for (SmokeParticle s : smoke) {
            s.paint(g, scale, scaleX);
        }

        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
    }
}
