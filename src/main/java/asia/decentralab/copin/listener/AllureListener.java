package asia.decentralab.copin.listener;

import asia.decentralab.copin.browser.Driver;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class AllureListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onTestStart(ITestResult result) {
    }

    @Override
    public void onFinish(ITestContext context) {
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                Driver.getDriver()).getScreenshotAs(OutputType.BYTES)));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }
}
