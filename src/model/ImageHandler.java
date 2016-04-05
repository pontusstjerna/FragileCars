package model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Pontus on 2016-03-06.
 */
public class ImageHandler {
    public static final String filePath = "src\\model\\data\\images\\";

    public static BufferedImage loadImage(String name){
        try{
            return ImageIO.read(new File(filePath + name + ".png"));
        }catch(IOException e){
            System.out.println("Unable to load " + name + ".png.");
            e.getStackTrace();
            return null;
        }
    }
}
