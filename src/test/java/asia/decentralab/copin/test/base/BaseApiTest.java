package asia.decentralab.copin.test.base;

import asia.decentralab.copin.api.auth.AuthTokenProvider;
import asia.decentralab.copin.api.clients.PositionApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseApiTest.class);
    protected PositionApiClient apiClient;

    @BeforeClass
    public void setUpApiTest() {
        logger.info("Setting up API test suite");

        // Initialize authentication token
        AuthTokenProvider.getInstance().performLogin();
        logger.info("Authentication completed");

        // Initialize API client
        apiClient = new PositionApiClient();
        logger.info("API client initialized");
    }

    @AfterClass
    public void tearDownApiTest() {
        logger.info("API test suite completed");

        // Clear authentication state
        AuthTokenProvider.getInstance().clearTokenState();
        logger.info("✅ Authentication state cleared");
    }
}
