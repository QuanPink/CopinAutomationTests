package asia.decentralab.copin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static double extractNumericValue(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0;
        }

        Pattern pattern = Pattern.compile("(-?[\\d,]+\\.?\\d*)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String number = matcher.group(1).replace(",", "");
            try {
                return Double.parseDouble(number);
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        return 0;
    }

    public static int extractLeftValue(String fractionText) {
        if (fractionText == null || fractionText.trim().isEmpty()) {
            return 0;
        }

        try {
            String cleanedText = fractionText.trim().replaceAll("^[^0-9-]*", "");
            String[] parts = cleanedText.split("/");
            return Integer.parseInt(parts[0].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static int extractRightValue(String fractionText) {
        if (fractionText == null || fractionText.trim().isEmpty()) {
            return 0;
        }

        try {
            String cleanedText = fractionText.trim().replaceAll("^[^0-9-]*", "");
            String[] parts = cleanedText.split("/");
            return Integer.parseInt(parts[1].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static double extractPercentageValue(String fractionText) {
        int left = extractLeftValue(fractionText);
        int right = extractRightValue(fractionText);

        if (right == 0) return 0;
        return ((double) left / right) * 100;
    }
}
