package asia.decentralab.copin.utils;

import asia.decentralab.copin.config.EnvironmentConfig;
import asia.decentralab.copin.constants.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AuthTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenProvider.class);

    private final EnvironmentConfig config = EnvironmentConfig.getInstance();
    private static volatile AuthTokenProvider instance;

    private volatile String cachedToken;
    private volatile boolean tokenInitialized = false;

    private AuthTokenProvider() {
    }

    public static AuthTokenProvider getInstance() {
        if (instance == null) {
            synchronized (AuthTokenProvider.class) {
                if (instance == null) {
                    instance = new AuthTokenProvider();
                }
            }
        }
        return instance;
    }

    public void initialize() {
        if (!tokenInitialized || cachedToken == null) {
            synchronized (this) {
                if (!tokenInitialized || cachedToken == null) {
                    performLoginFlow();
                }
            }
        }
    }

    public synchronized String getToken() {
        if (!tokenInitialized || cachedToken == null) {
            throw new IllegalStateException("Token not initialized. Call initialize() first.");
        }
        return cachedToken;
    }

    private void performLoginFlow() {
        logger.info("Starting 2-step authentication flow...");

        try {
            final String email = config.getEmail();
            final String otp = config.getDefaultOTP();
            final String apiBaseUrl = config.getApiBaseUrl();

            // Step 1: Verify OTP with external service
            logger.info("Step 1: Verifying OTP for email: {}***",
                    email.substring(0, Math.min(3, email.length())));

            final Response otpResponse = verifyOTP(email, otp);
            validateResponse(otpResponse, "OTP Verification");
            final String intermediateToken = extractToken(otpResponse, "token");

            // Step 2: Exchange for API access token
            logger.info("Step 2: Authenticating with Copin API");
            final Response loginResponse = authenticateWithCopin(apiBaseUrl, intermediateToken);
            validateResponse(loginResponse, "Login Copin");

            cachedToken = extractToken(loginResponse, "access_token");
            tokenInitialized = true;

            logger.info("Authentication completed successfully - token cached");

        } catch (Exception e) {
            logger.error("❌ Authentication failed", e);
            clearTokenState();
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    private Response verifyOTP(String email, String otpCode) {
        RequestSpecification request = RestAssured.given()
                .contentType("application/json")
                .header("privy-app-id", "cm7d2vu66037010jr8dm0thd9")
                .body(Map.of(
                        "email", email,
                        "code", otpCode,
                        "mode", "login-or-sign-up"
                ));

        return request.post(ApiEndpoints.VERIFY_OTP);
    }

    private Response authenticateWithCopin(String apiBaseUrl, String token) {
        RequestSpecification request = RestAssured.given()
                .baseUri(apiBaseUrl)
                .contentType("application/json")
                .body(Map.of("jwt", token));

        return request.post(ApiEndpoints.LOGIN);
    }

    private void validateResponse(final Response response, final String operationName) {
        final int statusCode = response.getStatusCode();

        if (statusCode != 200 && statusCode != 201) {
            final String errorMsg = String.format("%s failed (HTTP %d): %s",
                    operationName, statusCode,
                    response.getBody().asString());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    private String extractToken(final Response response, final String tokenFieldName) {
        try {
            final String token = response.jsonPath().getString(tokenFieldName);

            if (token == null || token.trim().isEmpty()) {
                throw new RuntimeException(
                        String.format("Token field '%s' is missing or empty in response", tokenFieldName)
                );
            }

            logger.debug("✅ Token extracted successfully from field: {}", tokenFieldName);
            return token;

        } catch (Exception e) {
            final String errorMsg = String.format("Failed to extract token from field '%s': %s",
                    tokenFieldName, e.getMessage());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    public synchronized void clearTokenState() {
        cachedToken = null;
        tokenInitialized = false;
    }
}
