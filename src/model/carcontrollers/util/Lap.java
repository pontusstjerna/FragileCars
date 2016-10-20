package model.carcontrollers.util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Pontus on 2016-10-20.
 */
public class Lap implements Serializable {
    private final String trackName;
    private final String carName;
    private final ArrayList<BotPoint> lap;


    public Lap(String trackName, String carName, ArrayList<BotPoint> lap){
        this.trackName = trackName;
        this.carName = carName;
        this.lap = lap;
    }

    public String getTrackName(){return trackName;}

    public String getCarName() {return carName;}

    public ArrayList<BotPoint> getLap() {return lap;}
}
