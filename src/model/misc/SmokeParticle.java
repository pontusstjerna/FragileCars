package model.misc;

import model.GameObject;

import java.awt.*;
import java.util.Random;

/**
 * Created by pontu on 2016-09-12.
 */
public class SmokeParticle {
    private double x, y;
    private int thickness = 30;

    private double timeExisted;
    private final double lifeTime;
    private final int spread = 150;
    private double initVel;
    private double velocity;
    private double heading;

    private Color color;
    private final int MAX_ALPHA = 100;
    private int gray = 0;
    private int alpha = MAX_ALPHA;

    private Random rand;

    public SmokeParticle(int x, int y, double velocity, double heading, Random rand){
        this.rand = rand;
        gray = rand.nextInt(30) + 30;
        color = new Color(gray,gray,gray,alpha);

        this.x = x;
        this.y = y;
        lifeTime = 1;
        initVel = velocity;
        this.heading = heading;
    }

    public void reSet(int x, int y, double velocity, double heading){
        gray = rand.nextInt(30) + 30;
        alpha = MAX_ALPHA;
        color = new Color(gray,gray,gray,alpha);
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
        y = (y - velocity * Math.cos(heading) * deltaTime);

        velocity = Math.max(initVel - (int)(initVel*timeExisted/lifeTime),1);

        //Fading with time
        alpha = Math.min((int)(10/timeExisted),MAX_ALPHA);
        //System.out.println("Alpha: " + alpha + " Time existed: " + timeExisted);
        color = new Color(gray,gray,gray,alpha);

    }

    public void paint(Graphics2D g, double scale, int scaleX) {
        g.setColor(color);
        g.fillRoundRect((int)(x*scale) + scaleX, (int)(y*scale), (int)(thickness*scale),
                (int)(thickness*scale), (int)(thickness*scale), (int)(thickness*scale));
    }
}
