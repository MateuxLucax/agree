package utils;

import java.io.File;
import java.io.IOException;

public class JsonDatabaseUtil {

    public static File getFile(String path) {
        var databaseDirectory = System.getProperty("user.dir") + "/database/";
        var databaseDirectoryFile = new File(databaseDirectory);
        if (!databaseDirectoryFile.exists()) databaseDirectoryFile.mkdirs();

        var file = new File(databaseDirectory + path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
