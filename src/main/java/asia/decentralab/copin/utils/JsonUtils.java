package asia.decentralab.copin.utils;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;

public class JsonUtils {

    private static final Gson gson = new Gson();

    public static <T> T readJsonFile(String filePath, Class<T> clazz){
        try (FileReader reader = new FileReader(filePath)){
            return gson.fromJson(reader, clazz);
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Failed to read JSON from file", e);
        }
    }
}
