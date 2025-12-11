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

        // Format 1: "fieldName should be between X and Y. Actual value: Z, Account: W"
        if (errorMessage.contains("should be between")) {
            int shouldIdx = errorMessage.indexOf(" should be between");
            if (shouldIdx > 0) {
                fieldName = errorMessage.substring(0, shouldIdx).trim();
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
                int actualEnd = errorMessage.indexOf(", Account:", actualIdx);
                if (actualEnd > actualIdx) {
                    actual = errorMessage.substring(actualIdx + 14, actualEnd).trim();
                } else {
                    String actualPart = errorMessage.substring(actualIdx + 14);
                    int expectedIdx = actualPart.indexOf(" expected");
                    if (expectedIdx > 0) {
                        actual = actualPart.substring(0, expectedIdx).trim();
                    } else {
                        actual = actualPart.trim();
                    }
                }
            }

            // Extract account
            int accIdx = errorMessage.indexOf(", Account: ");
            if (accIdx > 0) {
                String accountPart = errorMessage.substring(accIdx + 11);
                int endIdx = accountPart.indexOf(" expected");
                if (endIdx > 0) {
                    account = accountPart.substring(0, endIdx).trim();
                } else {
                    account = accountPart.trim();
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
                fieldName = errorMessage.substring(0, mismatchIdx).trim();
            }

            int expIdx = errorMessage.indexOf("Expected: ") + 10;
            int expEnd = errorMessage.indexOf(", Actual: ", expIdx);
            if (expEnd > expIdx) {
                expected = errorMessage.substring(expIdx, expEnd).trim();
            }

            int actIdx = errorMessage.indexOf(", Actual: ") + 10;
            int actEnd = errorMessage.indexOf(", Account:", actIdx);
            if (actEnd > actIdx) {
                actual = errorMessage.substring(actIdx, actEnd).trim();
            } else {
                String actualPart = errorMessage.substring(actIdx);
                int endIdx = actualPart.indexOf(" expected");
                if (endIdx == -1) {
                    endIdx = actualPart.indexOf(", Diff");
                }
                if (endIdx > 0) {
                    actual = actualPart.substring(0, endIdx).trim();
                } else {
                    actual = actualPart.trim();
                }
            }

            // Extract account
            int accIdx = errorMessage.indexOf(", Account: ");
            if (accIdx > 0) {
                String accountPart = errorMessage.substring(accIdx + 11);
                // Remove trailing " expected [...] but found [...]"
                int endIdx = accountPart.indexOf(" expected");
                if (endIdx > 0) {
                    account = accountPart.substring(0, endIdx).trim();
                } else {
                    account = accountPart.trim();
                }
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