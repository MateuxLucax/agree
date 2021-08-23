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

    private static final Map<String, Image> cache   = new HashMap<>();
    private static final Map<String, Image> cache64 = new HashMap<>();

    public static Image getImage(String url) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }

        try {
            var img = ImageIO.read(new File(url));
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
            var img = ImageIO.read(new URL(url));
            cache.put(url, img);
            return new ImageIcon(img);
        } catch (Exception e) {
            System.out.println("Something went wrong while loading image." + e.getMessage());
        }

        return null;
    }

    public static ImageIcon getImageIcon64(String url) {
        if (cache64.containsKey(url)) {
            return new ImageIcon(cache64.get(url));
        }

        try {
            var img   = ImageIO.read(new URL(url));
            var img64 = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            cache64.put(url, img64);
            return new ImageIcon(img64);
        } catch (Exception e) {
            System.out.println("Something went wrong while loading image." + e.getMessage());
        }

        return null;
    }
}
