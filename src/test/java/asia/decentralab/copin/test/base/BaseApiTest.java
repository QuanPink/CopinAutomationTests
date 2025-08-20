package asia.decentralab.copin.test.base;

import asia.decentralab.copin.api.clients.FlexibleApiClient;
import asia.decentralab.copin.utils.AuthTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseApiTest.class);
    protected FlexibleApiClient apiClient;

    @BeforeClass
    public void setUpApiTest() {
        logger.info("🔌 Setting up API test suite");
        try {
            AuthTokenProvider.getInstance().initialize();
            logger.info("Authentication successful - token obtained");

            apiClient = new FlexibleApiClient();
            String authToken = AuthTokenProvider.getInstance().getToken();
            apiClient.setAuthToken(authToken);
            logger.info("API client configured");
        } catch (Exception e) {
            logger.error("❌ Authentication setup failed: {}", e.getMessage());
            throw new RuntimeException("Cannot proceed with API tests without valid authentication", e);
        }
    }

    @AfterClass
    public void tearDownApiTest() {
        logger.info("🔌 API test suite completed");
        try {
            AuthTokenProvider.getInstance().clearTokenState();
            logger.info("Authentication cache cleared");
        } catch (Exception e) {
            logger.warn("⚠️ Failed to clear auth cache: {}", e.getMessage());
        }
    }
}
