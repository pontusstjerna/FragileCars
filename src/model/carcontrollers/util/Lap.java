package model.carcontrollers.util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Pontus on 2016-10-20.
 */
public class Lap implements Serializable {
    private final ArrayList<BotPoint> lap;
    private final int speedLimit;

    public Lap(ArrayList<BotPoint> lap, int speedLimit){
        this.lap = lap;
        this.speedLimit = speedLimit;
    }

    public ArrayList<BotPoint> getLap() {return lap;}

    public int getSpeedLimit(){return speedLimit;}
}
