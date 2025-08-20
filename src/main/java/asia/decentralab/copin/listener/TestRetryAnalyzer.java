package asia.decentralab.copin.listener;

import asia.decentralab.copin.config.EnvironmentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestRetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(TestRetryAnalyzer.class);
    private static final int MAX_RETRY_COUNT;

    // Map to track the retry count for each test method
    private static final Map<String, Integer> retryCountMap = new ConcurrentHashMap<>();

    static {
        int retryCount = 2; // Default value
        try {
            EnvironmentConfig config = EnvironmentConfig.getInstance();
            retryCount = config.getRetryCount();
        } catch (Exception e) {
            logger.error("Error loading retry config, using default: {}", e.getMessage());
        }
        MAX_RETRY_COUNT = retryCount;
    }

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) {
            if (isApiTest(result)) {
                logger.info("❌ API test failed - no retry: {}", result.getName());
                return false;
            }
            // Generate a unique key for each method
            String methodKey = result.getTestClass().getName() + "." + result.getMethod().getMethodName();

            // Get the retry count from the map, defaulting to 0
            int count = retryCountMap.getOrDefault(methodKey, 0);

            if (count < MAX_RETRY_COUNT) {
                count++;
                // Store it in the map.
                retryCountMap.put(methodKey, count);
                // Store it in both the map and attributes so that AllureListener can access it
                result.setAttribute("retry.count", count);

                logger.info("Retrying test: {} for the {} time", result.getName(), count);
                return true;
            } else {
                // Remove it from the map when the retry limit is reached
                retryCountMap.remove(methodKey);
                logger.info("Test {} failed after {} retries", result.getName(), count);
            }
        }
        return false;
    }

    private boolean isApiTest(ITestResult result) {
        String className = result.getTestClass().getName();
        String packageName = result.getTestClass().getRealClass().getPackage().getName();

        // Check if it's an API test based on package or class name
        boolean isApiPackage = packageName.contains("api") ||
                packageName.contains("service") ||
                packageName.contains("integration");

        boolean isApiClass = className.contains("Api") ||
                className.contains("Service") ||
                className.contains("Client") ||
                className.contains("Integration");

        // Check if test extends BaseApiTest
        boolean extendsBaseApiTest = false;
        try {
            Class<?> testClass = result.getTestClass().getRealClass();
            while (testClass != null) {
                if ("BaseApiTest".equals(testClass.getSimpleName())) {
                    extendsBaseApiTest = true;
                    break;
                }
                testClass = testClass.getSuperclass();
            }
        } catch (Exception e) {
            logger.debug("Could not check superclass hierarchy: {}", e.getMessage());
        }

        boolean isApi = isApiPackage || isApiClass || extendsBaseApiTest;
        logger.debug("Test {} is API test (no retry): {}", className, isApi);

        return isApi;
    }
}