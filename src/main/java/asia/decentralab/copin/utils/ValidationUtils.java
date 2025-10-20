package asia.decentralab.copin.utils;

import org.testng.Assert;

import java.util.Map;

public class ValidationUtils {

    private static final double ZERO_THRESHOLD = 1E-10;

    public static void assertNotNull(Object value, String message) {
        Assert.assertNotNull(value, message);
    }

    public static void assertInRange(double value, double min, double max, String fieldName) {
        double normalizedValue = normalizeValue(value);

        Assert.assertTrue(normalizedValue >= min && normalizedValue <= max,
                fieldName + " should be between " + min + " and " + max + ". Actual value: " + normalizedValue);
    }

    public static void assertCloseToValue(double actual, double expected, double tolerance, String fieldName) {
        double normalizedActual = normalizeValue(actual);
        double normalizedExpected = normalizeValue(expected);

        double diff = Math.abs(normalizedActual - normalizedExpected);
        double allowedDiff = Math.abs(normalizedExpected * tolerance);

        // Handle zero case
        if (normalizedExpected == 0.0) {
            allowedDiff = Math.max(allowedDiff, ZERO_THRESHOLD);
        }

        Assert.assertTrue(diff <= allowedDiff,
                String.format("%s should be close to expected value. Expected: %s, Actual: %s, Diff: %s",
                        fieldName, normalizedExpected, normalizedActual, diff));
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

    public static void assertFalse(boolean condition, String message) {
        Assert.assertFalse(condition, message);
    }

    public static void assertEquals(Object actual, Object expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    public static void assertEquals(double actual, double expected, String message) {
        double normalizedActual = normalizeValue(actual);
        double normalizedExpected = normalizeValue(expected);

        Assert.assertEquals(normalizedActual, normalizedExpected, message);
    }

    private static double normalizeValue(double value) {
        return Math.abs(value) < ZERO_THRESHOLD ? 0.0 : value;
    }
}