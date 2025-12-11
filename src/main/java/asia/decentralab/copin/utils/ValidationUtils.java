package asia.decentralab.copin.utils;

import org.testng.Assert;

import java.util.Map;

public class ValidationUtils {

    private static final double ZERO_THRESHOLD = 1E-10;

    public static void assertInRange(double value, double min, double max, String fieldName, String account) {
        Assert.assertTrue(value >= min && value <= max,
                String.format("%s should be between %s and %s. Actual value: %s, Account: %s",
                        fieldName, min, max, value, account));
    }

    public static void assertCloseToValue(double expected, double actual, double tolerance, String fieldName, String account) {
        double normalizedExpected = normalizeValue(expected);
        double normalizedActual = normalizeValue(actual);

        double diff = Math.abs(normalizedExpected - normalizedActual);
        double allowedDiff = Math.abs(normalizedExpected * tolerance);

        // Handle zero case
        if (normalizedExpected == 0.0) {
            allowedDiff = Math.max(allowedDiff, ZERO_THRESHOLD);
        }

        Assert.assertTrue(diff <= allowedDiff,
                String.format("%s mismatch. Expected: %s, Actual: %s, Diff: %s, Account: %s",
                        fieldName, normalizedExpected, normalizedActual, diff, account));
    }

    public static void assertEquals(double expected, double actual, String fieldName, String account) {
        double normalizedActual = normalizeValue(actual);
        double normalizedExpected = normalizeValue(expected);
        Assert.assertEquals(normalizedActual, normalizedExpected,
                String.format("%s mismatch. Expected: %s, Actual: %s, Account: %s",
                        fieldName, normalizedExpected, normalizedActual, account));
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    public static void assertTrue(boolean condition, String message) {
        Assert.assertTrue(condition, message);
    }

    public static void assertFalse(boolean condition, String message) {
        Assert.assertFalse(condition, message);
    }

    public static void assertNotNull(Map<String, Object> map, String message) {
        Assert.assertNotNull(map, message);
    }

    public static void assertNotNull(Object value, String message) {
        Assert.assertNotNull(value, message);
    }

    private static double normalizeValue(double value) {
        return Math.abs(value) < ZERO_THRESHOLD ? 0.0 : value;
    }
}