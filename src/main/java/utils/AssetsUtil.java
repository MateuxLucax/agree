package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class AssetsUtil {

    public static BufferedImage getImage(String path) {
        try {
            String filePath = System.getProperty("user.dir") + path;
            return ImageIO.read(new File(filePath));
        } catch (Exception e) {
            System.out.println("Something went wrong while loading image." + e.getMessage());
        }

        return null;
    }

}
