package model.carcontrollers.util;

import java.awt.*;

/**
 * Created by pontu on 2016-04-15.
 */
public class BotPoint extends Point {
    private double radius;

    public BotPoint(int x, int y, double radius){
        super(x,y);
        this.radius = radius;
    }

    public BotPoint(int x, int y){
        this(x,y,1);
    }

    public double getRadius(){
        return radius;
    }
}
