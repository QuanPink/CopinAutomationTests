package asia.decentralab.copin.listener;

import asia.decentralab.copin.browser.WebDriverManager;
import asia.decentralab.copin.config.EnvironmentConfig;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.UUID;

public class AllureListener implements ITestListener {

    private static final EnvironmentConfig config = EnvironmentConfig.getInstance();

    @Override
    public void onStart(ITestContext context) {
        System.out.println("Starting test suite: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Finished test suite: " + context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Starting test: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test passed: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test failed: " + result.getName());

        // Chụp ảnh màn hình khi test thất bại - nếu đã bật tính năng này trong config
        if (config.isScreenshotOnFailure()) {
            WebDriver driver = WebDriverManager.getDriver();
            if (driver != null) {
                takeScreenshot(driver, result.getName());
            } else {
                System.err.println("WebDriver is null, cannot take screenshot");
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test skipped: " + result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("Test failed but within success percentage: " + result.getName());
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    private byte[] takeScreenshot(WebDriver driver, String testName) {
        String screenshotName = testName + "_" + UUID.randomUUID().toString();
        System.out.println("Taking screenshot: " + screenshotName);
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            return new byte[0];
        }
    }
}