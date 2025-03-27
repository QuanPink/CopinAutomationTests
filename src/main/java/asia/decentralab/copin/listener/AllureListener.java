package asia.decentralab.copin.listener;

import asia.decentralab.copin.browser.WebDriverManager;
import asia.decentralab.copin.config.EnvironmentConfig;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.UUID;

public class AllureListener implements ITestListener {
    private static final Logger logger = LoggerFactory.getLogger(AllureListener.class);
    private static final EnvironmentConfig config = EnvironmentConfig.getInstance();

    @Override
    public void onStart(ITestContext context) {
        logger.info("Starting test suite: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Finished test suite: {}", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Starting test: {}", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test passed: {}", result.getName());

        // Mark a test as flaky if it requires a retry but eventually passes
        int retryCount = getRetryCount(result);
        if (retryCount > 0) {
            Allure.label("flaky", "true");
            Allure.step("Test passed after " + retryCount + " retry attempts");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.info("Test failed: {}", result.getName());

        // Add information about the retry attempt
        int retryCount = getRetryCount(result);
        if (retryCount > 0) {
            Allure.step("Retry attempt: " + retryCount);
        }

        // Include error details in the report
        if (result.getThrowable() != null) {
            Allure.addAttachment("Exception Message",
                    result.getThrowable().getMessage());
        }

        // Take a screenshot on test failure if this feature is enabled in the config
        if (config.isScreenshotOnFailure()) {
            WebDriver driver = WebDriverManager.getDriver();
            if (driver != null) {
                takeScreenshot(driver, result.getName());
            } else {
                logger.error("WebDriver is null, cannot take screenshot");
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.info("Test skipped: {}", result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.info("Test failed but within success percentage: {}", result.getName());
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    private byte[] takeScreenshot(WebDriver driver, String testName) {
        String screenshotName = testName + "_" + UUID.randomUUID();
        logger.info("Taking screenshot: {}", screenshotName);
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            logger.error("Failed to take screenshot: {}", e.getMessage());
            return new byte[0];
        }
    }

    // Method to get the current retry count
    private int getRetryCount(ITestResult result) {
        Object count = result.getAttribute("retry.count");
        int retryCount = count == null ? 0 : (Integer) count;
        logger.debug("Current retry count for test '{}': {}", result.getName(), retryCount);
        return retryCount;
    }
}