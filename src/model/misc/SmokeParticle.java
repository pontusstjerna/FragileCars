package model.misc;

import java.awt.*;
import java.util.Random;

/**
 * Created by pontu on 2016-09-12.
 */
public class SmokeParticle {
    private double x, y;
    private int thickness = 20;

    private double timeExisted;
    private final double lifeTime;
    private final int spread = 150;
    private double initVel;
    private double velocity;
    private double heading;

    private int colorIndex = 0;

    private Color[] colors;
    private final int MAX_ALPHA = 100;
    private int gray = 0;
    private int alpha = MAX_ALPHA;

    private Random rand;

    public SmokeParticle(int x, int y, double velocity, double heading, Color[] colors, Random rand){
        this.rand = rand;
        this.colors = colors;

        this.x = x;
        this.y = y;
        lifeTime = 1;
        initVel = velocity;
        this.heading = heading;
    }

    public void reSet(int x, int y, double velocity, double heading){
        timeExisted = 0;

        this.x = x;
        this.y = y;
        initVel = velocity;
        this.heading = heading;
    }

    public void update(double deltaTime) {
        if(deltaTime < 1){
            timeExisted += deltaTime;
        }

        x = x + (rand.nextInt(spread*2) - spread)*deltaTime;
        y = y + (rand.nextInt(spread*2) - spread)*deltaTime;

        x = (x + velocity * Math.sin(heading) * deltaTime);
        y = (y + velocity * Math.cos(heading) * deltaTime);

        velocity = Math.max(initVel - (int)(initVel*timeExisted/lifeTime),1);

        //Fading with time
        colorIndex = Math.min((int)((timeExisted/lifeTime)*(colors.length-1)), colors.length-1);
    }

    public void paint(Graphics2D g, double scale, int scaleX) {
        g.setColor(colors[colorIndex]);
        g.fillRoundRect((int)((x - thickness/2)*scale) + scaleX, (int)((y - thickness/2)*scale), (int)(thickness*scale),
                (int)(thickness*scale), (int)(thickness*scale), (int)(thickness*scale));
    }
}
