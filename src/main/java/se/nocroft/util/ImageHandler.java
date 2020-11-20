package se.nocroft.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pontus on 2016-03-06.
 */
public class ImageHandler {
    public static final String filePath = "se/nocroft/model/data/images/";

    public static BufferedImage loadImage(String name) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = classLoader.getResourceAsStream(filePath + name + ".png");
            return ImageIO.read(is);
        } catch (IOException e) {
            System.out.println("Unable to load " + name + ".png.");
            e.getStackTrace();
            return null;
        }
    }

    public static BufferedImage cutImage(BufferedImage image, int state, int frame, int width, int height) {
        return image.getSubimage(frame * width, state * height, width, height);
    }
}
