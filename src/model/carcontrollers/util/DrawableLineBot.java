package model.carcontrollers.util;

import model.carcontrollers.DrawableBot;

import java.util.List;

/**
 * Created by Pontus on 2016-04-15.
 */
public interface DrawableLineBot extends DrawableBot{
    List<WallLine> getWallLines();
}
