package model.misc;

import model.GameObject;

import java.awt.*;
import java.util.Random;

/**
 * Created by pontu on 2016-09-12.
 */
public class SmokeParticle implements GameObject {
    private int x, y;
    private int width = 10;
    private int height = 10;

    private double timeExisted;
    private final double lifeTime;
    final int maxSpread = 300;
    private int spread = maxSpread;

    private Color color;
    private int gray = 0;
    private int alpha = 255;

    private Random rand;

    public SmokeParticle(int x, int y, Random rand){
        this.rand = rand;
        gray = rand.nextInt(30) + 30;
        color = new Color(gray,gray,gray,alpha);

        this.x = x;
        this.y = y;
        lifeTime = 5;
    }


    @Override
    public void update(double deltaTime) {
        if(deltaTime < 1){
            timeExisted += deltaTime;
        }

        x = (int)(x + (rand.nextInt(spread*2) - spread)*deltaTime);
        y = (int)(y + (rand.nextInt(spread*2) - spread)*deltaTime);

        spread = Math.max(maxSpread - (int)(maxSpread*timeExisted/lifeTime),1);

        //Fading with time
        alpha = Math.max(255 - (int)(255*timeExisted/lifeTime),0);
        color = new Color(gray,gray,gray,alpha);

        //TODO: Make x and y doubles so that speed of particle can slow down the longer it lives, so the smoke remains on track

    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {
        g.setColor(color);
        g.fillRoundRect((int)(x*scale) + scaleX, (int)(y*scale), (int)(width*scale),
                (int)(height*scale), (int)(width*scale), (int)(height*scale));
    }
}
