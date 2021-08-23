package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageUtil {

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage getImage(String url) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }

        try {
            BufferedImage img = ImageIO.read(new File(url));
            cache.put(url, img);
            return img;
        } catch (Exception e) {
            System.out.println("Something went wrong while loading image." + e.getMessage());
        }

        return null;
    }

    public static ImageIcon getImageIcon(String url) {
        if (cache.containsKey(url)) {
            return new ImageIcon(cache.get(url));
        }

        try {
            BufferedImage img = ImageIO.read(new URL(url));
            cache.put(url, img);
            return new ImageIcon(img);
        } catch (Exception e) {
            System.out.println("Something went wrong while loading image." + e.getMessage());
        }

        return null;
    }

    public static ImageIcon getImageIcon(String url, int width, int height) {
        if (cache.containsKey(url)) {
            return new ImageIcon(cache.get(url).getScaledInstance(width, height, Image.SCALE_SMOOTH));
        }

        try {
            BufferedImage img = ImageIO.read(new URL(url));
            cache.put(url, img);
            return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.out.println("Something went wrong while loading image." + e.getMessage());
        }

        return null;
    }
}
