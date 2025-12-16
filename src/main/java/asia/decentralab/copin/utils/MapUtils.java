package asia.decentralab.copin.utils;

import java.util.List;
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

    public static List<Map<String, Object>> sortPositionsByCloseTime(List<Map<String, Object>> positions) {
        positions.sort((p1, p2) -> {
            // Sort by closeBlockTime desc
            String time1 = (String) p1.get("closeBlockTime");
            String time2 = (String) p2.get("closeBlockTime");
            if (time1 == null && time2 == null) return 0;
            if (time1 == null) return 1;
            if (time2 == null) return -1;
            int timeCompare = time2.compareTo(time1);
            if (timeCompare != 0) return timeCompare;

            // Then by closeBlockNumber desc
            long block1 = getLong(p1, "closeBlockNumber");
            long block2 = getLong(p2, "closeBlockNumber");
            int blockCompare = Long.compare(block2, block1);
            if (blockCompare != 0) return blockCompare;

            // Then by logId desc
            long log1 = getLong(p1, "logId");
            long log2 = getLong(p2, "logId");
            return Long.compare(log2, log1);
        });

        return positions;
    }
}