package utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

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
                System.out.println(e.getMessage());
            }
        }

        return file;
    }

    public static boolean writeToFile(File file, Object data) {
        try (var fileWriter = new FileWriter(file)) {
            fileWriter.write(new Gson().toJson(data));
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static <T> T readFromFile(File file, Type type) {
        try (var jsonReader = new JsonReader(new FileReader(file))) {
            return new Gson().fromJson(jsonReader, type);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
