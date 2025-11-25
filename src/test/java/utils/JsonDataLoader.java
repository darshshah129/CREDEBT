package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;

public class JsonDataLoader {

    private static Map<String, Object> data;

    public static Map<String, Object> loadDBValues() {
        if (data == null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                File file = new File("db_values.json");
                data = mapper.readValue(file, Map.class);
            } catch (Exception e) {
                throw new RuntimeException("Unable to load DB JSON: " + e.getMessage());
            }
        }
        return data;
    }

    public static Object get(String key) {
        return loadDBValues().get(key);
    }

    public static double getDouble(String key) {
        return Double.parseDouble(get(key).toString());
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key).toString());
    }
}
