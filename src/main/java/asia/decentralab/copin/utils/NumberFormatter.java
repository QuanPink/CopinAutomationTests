package asia.decentralab.copin.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class NumberFormatter {

    public static Number parseStringToNumber(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        input = input.trim().replaceAll("[^0-9.-]", "");
        if (input.isEmpty() || input.equals("--")) {
            return 0;
        }

        try {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            return format.parse(input);
        } catch (ParseException e) {
            throw new NumberFormatException(e.getMessage());
        }
    }

    public static double roundToDecimalPlaces(double value, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(value * scale) / scale;
    }

    public static long calculateTimeBetweenNowAndTimestamp(long timestamp) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime targetDateTime = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        long totalMinutes = ChronoUnit.MINUTES.between(currentDateTime, targetDateTime);
        long totalHours = ChronoUnit.HOURS.between(currentDateTime, targetDateTime);
        long totalDays = ChronoUnit.DAYS.between(currentDateTime, targetDateTime);
        long totalMonths = ChronoUnit.MONTHS.between(currentDateTime, targetDateTime);

        if (Math.abs(totalMinutes) < 60) {
            return totalMinutes;
        } else if (Math.abs(totalHours) < 24) {
            return Math.round(totalMinutes / 60.0);
        } else if (Math.abs(totalDays) < 30) {
            return Math.round(totalHours / 24.0);
        } else if (Math.abs(totalMonths) < 12) {
            return Math.round(totalDays / 30.0);
        } else {
            return Math.round(totalMonths / 12.0);
        }
    }

    public static Number extractNumberFromString(String input, int index) {
        String[] parts = input.split("%");
        if (index >= 0 && index < parts.length) {
            return parseStringToNumber(parts[index]);
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    public static String convertSecondsToHumanReadable(double seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds cannot be negative");
        }

        if (seconds < 60) {
            return String.format("%.1fs", seconds);
        } else if (seconds < 3600) {
            double minutes = seconds / 60;
            return String.format("%.1fm", minutes);
        } else {
            double hours = seconds / 3600;
            return String.format("%.1fh", hours);
        }
    }
}