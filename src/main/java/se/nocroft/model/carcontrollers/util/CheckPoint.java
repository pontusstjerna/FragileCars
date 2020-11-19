package se.nocroft.model.carcontrollers.util;

import model.carcontrollers.BallsBot;

import java.awt.*;

/**
 * Created by pontu on 2016-04-08.
 */
public class CheckPoint {
    private Point turn;
    private double moved;
    private BallsBot.Dir dir;
    private double dTime;
    private double heading;

    public CheckPoint(Point turn, long distanceMoved, BallsBot.Dir dir, double dTime) {
        this.turn = turn;
        moved = distanceMoved;
        this.dir = dir;
        this.dTime = dTime;
    }

    public CheckPoint(int x, int y, double distanceMoved, BallsBot.Dir dir, double dTime) {
        turn = new Point(x, y);
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

    public BallsBot.Dir getDir() {
        return dir;
    }

    public double getDeltaTime() {
        return dTime;
    }
}
