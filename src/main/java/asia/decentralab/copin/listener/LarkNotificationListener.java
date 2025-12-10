package asia.decentralab.copin.listener;

import asia.decentralab.copin.utils.LarkNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LarkNotificationListener implements ITestListener {
    private static final Logger logger = LoggerFactory.getLogger(LarkNotificationListener.class);
    private Instant suiteStartTime;

    // ✅ ADD: Collect failures để gom lại
    private final List<String> failureDetails = new ArrayList<>();
    private String currentProtocol = "";

    @Override
    public void onStart(ITestContext context) {
        suiteStartTime = Instant.now();
        failureDetails.clear(); // Reset
        logger.info("📊 Test suite started: {}", context.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            String testName = result.getTestClass().getRealClass().getSimpleName() + "." + result.getName();

            Throwable throwable = result.getThrowable();
            String errorMessage = "Unknown error";

            if (throwable != null) {
                if (throwable.getMessage() != null) {
                    errorMessage = throwable.getMessage();
                } else {
                    errorMessage = throwable.getClass().getSimpleName() + " occurred";
                }
            }

            logger.error("❌ Test failed: {} - {}", testName, errorMessage);

            // Extract parameters: [protocol, timeValue]
            String protocol = extractProtocol(result);
            String timeValue = extractTimeValue(result);
            currentProtocol = protocol;

            String failureDetail = formatFailureDetail(protocol, timeValue, errorMessage);
            failureDetails.add(failureDetail);

        } catch (Exception e) {
            logger.error("Failed to process test failure: {}", e.getMessage(), e);
            failureDetails.add("[Unknown] Failed to process error: " + e.getMessage());
        }
    }

    private String extractProtocol(ITestResult result) {
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            return parameters[0].toString();
        }
        return "Unknown";
    }

    private String extractTimeValue(ITestResult result) {
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 1) {
            return parameters[1].toString();
        }
        return "N/A";
    }

    private String formatFailureDetail(String protocol, String timeValue, String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            return String.format("[%s | %s] Unknown error", protocol, timeValue);
        }

        String fieldName = "unknown";
        String expected = "N/A";
        String actual = "N/A";
        String account = "";

        // Format 1: "fieldName account should be between X and Y. Actual value: Z"
        if (errorMessage.contains("should be between")) {
            int shouldIdx = errorMessage.indexOf(" should be between");
            if (shouldIdx > 0) {
                String prefix = errorMessage.substring(0, shouldIdx);
                int accountStart = prefix.lastIndexOf(" 0x");
                if (accountStart == -1) {
                    accountStart = prefix.lastIndexOf(" dydx");
                }
                if (accountStart > 0) {
                    account = prefix.substring(accountStart + 1).trim();
                    fieldName = prefix.substring(0, accountStart).trim();
                } else {
                    fieldName = prefix.trim();
                }
            }

            // Extract range
            int betweenIdx = errorMessage.indexOf("between ") + 8;
            int andIdx = errorMessage.indexOf(" and ", betweenIdx);
            int dotIdx = errorMessage.indexOf(".", andIdx);
            if (andIdx > betweenIdx && dotIdx > andIdx) {
                String min = errorMessage.substring(betweenIdx, andIdx).trim();
                String max = errorMessage.substring(andIdx + 5, dotIdx).trim();
                expected = min + " ~ " + max;
            }

            // Extract actual
            int actualIdx = errorMessage.indexOf("Actual value: ");
            if (actualIdx > 0) {
                String actualPart = errorMessage.substring(actualIdx + 14);
                // Remove trailing "expected [true] but found [false]" if present
                int expectedIdx = actualPart.indexOf(" expected");
                if (expectedIdx > 0) {
                    actual = actualPart.substring(0, expectedIdx).trim();
                } else {
                    actual = actualPart.trim();
                }
            }

            String accountSuffix = account.isEmpty() ? "" : " - " + account;
            return String.format("[%s | %s] %s -> [%s], Actual: %s%s",
                    protocol, timeValue, fieldName, expected, actual, accountSuffix);
        }
        // Format 2: "fieldName account mismatch. Expected: X, Actual: Y"
        else if (errorMessage.contains(" mismatch.")) {
            int mismatchIdx = errorMessage.indexOf(" mismatch.");
            if (mismatchIdx > 0) {
                String prefix = errorMessage.substring(0, mismatchIdx);
                int accountStart = prefix.lastIndexOf(" 0x");
                if (accountStart == -1) {
                    accountStart = prefix.lastIndexOf(" dydx");
                }
                if (accountStart > 0) {
                    account = prefix.substring(accountStart + 1).trim();
                    fieldName = prefix.substring(0, accountStart).trim();
                } else {
                    fieldName = prefix.trim();
                }
            }

            int expIdx = errorMessage.indexOf("Expected: ") + 10;
            int expEnd = errorMessage.indexOf(",", expIdx);
            if (expEnd > expIdx) {
                expected = errorMessage.substring(expIdx, expEnd).trim();
            }

            int actIdx = errorMessage.indexOf("Actual: ") + 8;
            String actualPart = errorMessage.substring(actIdx);
            // Remove trailing " expected [true] but found [false]" or ", Diff: X"
            int endIdx = actualPart.indexOf(" expected");
            if (endIdx == -1) {
                endIdx = actualPart.indexOf(", Diff");
            }
            if (endIdx > 0) {
                actual = actualPart.substring(0, endIdx).trim();
            } else {
                actual = actualPart.trim();
            }

            String accountSuffix = account.isEmpty() ? "" : " - " + account;
            return String.format("[%s | %s] %s → Expected %s, Actual %s%s",
                    protocol, timeValue, fieldName, expected, actual, accountSuffix);
        }
        // Format 3: Fallback
        else {
            return String.format("[%s | %s] %s", protocol, timeValue, errorMessage);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        try {
            Duration duration = Duration.between(suiteStartTime, Instant.now());
            String formattedDuration = formatDuration(duration);

            int passed = context.getPassedTests().size();
            int failed = context.getFailedTests().size();
            int skipped = context.getSkippedTests().size();
            int total = passed + failed + skipped;

            logger.info("📊 Test suite completed: {} | Total: {}, Passed: {}, Failed: {}, Skipped: {}",
                    context.getName(), total, passed, failed, skipped);

            // Send failure alert if any
            if (!failureDetails.isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                LarkNotifier.sendFailureCard(
                        "TRADER STATISTICS",
                        timestamp,
                        failureDetails
                );
            }

            // Send summary report
            LarkNotifier.sendTestReport(
                    "Data Validation",
                    total,
                    passed,
                    failed,
                    skipped,
                    formattedDuration
            );

        } catch (Exception e) {
            logger.error("Failed in onFinish: {}", e.getMessage(), e);
        }
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
}