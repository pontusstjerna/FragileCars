package model.carcontrollers;

import java.awt.*;

/**
 * Created by pontu on 2016-04-08.
 */
public class CheckPoint {
    private Point turn;
    private double moved;
    private AfraidBot.Dir dir;
    private double dTime;
    private double heading;

    public CheckPoint(Point turn, long distanceMoved, AfraidBot.Dir dir, double dTime){
        this.turn = turn;
        moved = distanceMoved;
        this.dir = dir;
        this.dTime = dTime;
    }

    public CheckPoint(int x, int y, double distanceMoved, AfraidBot.Dir dir, double dTime){
        turn = new Point(x,y);
        moved = distanceMoved;
        this.dir = dir;
        this.dTime = dTime;
    }

    public Point getPoint() {
        return turn;
    }

    public double getMoved() {
        return moved;
    }

    public AfraidBot.Dir getDir(){
        return dir;
    }

    public double getDeltaTime(){
        return dTime;
    }
}
