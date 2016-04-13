package model.carcontrollers;

import java.awt.*;

/**
 * Created by pontu on 2016-04-08.
 */
public class CheckPoint {
    private Point turn;
    private long moved;
    private boolean turnedLeft;

    public CheckPoint(Point turn, long distanceMoved, boolean turnedLeft){

    }

    public CheckPoint(int x, int y, long distanceMoved, boolean turnedLeft){
        turn = new Point(x,y);
    }

    public Point getPoint() {
        return turn;
    }

    public long getMoved() {
        return moved;
    }

    public boolean isTurnedLeft() {
        return turnedLeft;
    }
}
