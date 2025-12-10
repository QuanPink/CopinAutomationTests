package asia.decentralab.copin.utils;

import java.util.Map;

public final class MapUtils {

    private MapUtils() {}

    public static double getDouble(Map<String, Object> map, String key) {
        Number number = getValueAs(map, key, Number.class);
        return number != null ? number.doubleValue() : 0.0;
    }

    public static long getLong(Map<String, Object> map, String key) {
        Number number = getValueAs(map, key, Number.class);
        return number != null ? number.longValue() : 0L;
    }

    public static int getInt(Map<String, Object> map, String key) {
        Number number = getValueAs(map, key, Number.class);
        return number != null ? number.intValue() : 0;
    }

    public static boolean getBoolean(Map<String, Object> map, String key) {
        Boolean value = getValueAs(map, key, Boolean.class);
        return value != null && value;
    }

    public static String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValueAs(Map<String, Object> map, String key, Class<T> type) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException(
                    String.format("Value for key '%s' is not a %s, got: %s",
                            key, type.getSimpleName(), value.getClass().getSimpleName()));
        }
        return (T) value;
    }
}