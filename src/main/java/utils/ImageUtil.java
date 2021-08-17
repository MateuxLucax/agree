package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class ImageUtil {

    public static BufferedImage getImage(String url) {
        try {
            return ImageIO.read(new File(url));
        } catch (Exception e) {
            System.out.println("Something went wrong while loading image." + e.getMessage());
        }

        return null;
    }

    public static ImageIcon getImageIcon(String url) {
        try {
            return new ImageIcon(ImageIO.read(new URL(url)));
        } catch (Exception e) {
            System.out.println("Something went wrong while loading image." + e.getMessage());
        }

        return null;
    }
}
