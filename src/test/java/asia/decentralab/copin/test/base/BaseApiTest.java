package asia.decentralab.copin.test.base;

import asia.decentralab.copin.utils.AuthTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseApiTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseApiTest.class);

    /**
     * Get auth token để dùng trong tests
     * AuthTokenProvider sẽ tự động handle 3-step authentication và cache token
     *
     * @return Authentication token
     */
    protected String getAuthToken() {
        try {
            AuthTokenProvider tokenProvider = AuthTokenProvider.getInstance();
            return tokenProvider.getToken();
        } catch (Exception e) {
            logger.error("❌ Failed to get auth token", e);
            throw new RuntimeException("Failed to get auth token: " + e.getMessage());
        }
    }

    /**
     * Refresh token nếu cần (khi API return 401)
     *
     * @return New token
     */
    protected String refreshAuthToken() {
        logger.warn("🔄 Refreshing auth token...");
        try {
            AuthTokenProvider tokenProvider = AuthTokenProvider.getInstance();
            String newToken = tokenProvider.refreshToken();
            logger.info("✅ Auth token refreshed successfully");
            return newToken;
        } catch (Exception e) {
            logger.error("❌ Failed to refresh auth token", e);
            throw new RuntimeException("Failed to refresh auth token: " + e.getMessage());
        }
    }
}
