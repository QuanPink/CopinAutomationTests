package asia.decentralab.copin.utils;

import org.testng.Assert;
import java.util.Map;

public class ValidationUtils {

    public static void assertNotNull(Object value, String message) {
        Assert.assertNotNull(value, message);
    }

    public static void assertInRange(double value, double min, double max, String fieldName) {
        Assert.assertTrue(value > min && value < max,
                fieldName + " should be between " + min + " and " + max);
    }

    public static void assertCloseToValue(double actual, double expected, double tolerance, String fieldName) {
        double diff = Math.abs(actual - expected);
        double allowedDiff = Math.abs(expected * tolerance);
        Assert.assertTrue(diff <= allowedDiff,
                fieldName + " should be close to expected value. Expected: " + expected + ", Actual: " + actual);
    }

    public static double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);

        if (value == null) {
            throw new IllegalArgumentException("Value for key '" + key + "' is null");
        }

        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("Value for key '" + key + "' is not a Number, got: " + value.getClass());
        }

        return ((Number) value).doubleValue();
    }

    public static int getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        throw new IllegalArgumentException("Value for key '" + key + "' is not a Number");
    }

    public static void assertTrue(boolean condition, String message) {
        Assert.assertTrue(condition, message);
    }

    public static void assertEquals(Object actual, Object expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    public static void assertEquals(double actual, double expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }
}