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
    private final int lifeTime;

    private Color color;
    private int gray = 0;
    private int alpha = 255;

    private Random rand;

    public SmokeParticle(int x, int y, int lifeTime, Random rand){
        this.rand = rand;
        gray = rand.nextInt(30) + 30;
        color = new Color(gray,gray,gray,alpha);

        this.x = x;
        this.y = y;
        this.lifeTime = rand.nextInt(lifeTime);
    }


    @Override
    public void update(double deltaTime) {
        if(deltaTime < 1){
            timeExisted += deltaTime;
        }

        int factor = (int)(255*timeExisted/lifeTime);

        //Fading with time
        alpha = Math.max(255 - (int)(255*timeExisted/lifeTime),0);
        color = new Color(gray,gray,gray,alpha);
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {
        g.setColor(color);
        g.fillRect((int)(x*scale) + scaleX, (int)(y*scale), (int)(width*scale), (int)(height*scale));
    }
}
