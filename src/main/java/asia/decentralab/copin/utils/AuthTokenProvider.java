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
    private static AuthTokenProvider instance;
    private static String cachedToken;
    private static boolean tokenInitialized = false;

    private final EnvironmentConfig config = EnvironmentConfig.getInstance();

    private AuthTokenProvider() {
    }

    public static synchronized AuthTokenProvider getInstance() {
        if (instance == null) {
            instance = new AuthTokenProvider();
        }
        return instance;
    }

    public String getToken() {
        if (!tokenInitialized || cachedToken == null) {
            performLoginFlow();
        }
        return cachedToken;
    }

    private void performLoginFlow() {
        logger.info("Starting 3-step authentication flow...");

        try {
            String email = config.getEmail();
            String otp = config.getDefaultOTP();
            String apiBaseUrl = config.getApiBaseUrl();

            // Step 1: Verify OTP
            logger.info("Step 1: Verifying OTP: {}", otp);
            Response verifyOtpResponse = verifyOTP(email, otp);
            validateResponse(verifyOtpResponse, "Verify OTP");

            String token = extractToken(verifyOtpResponse, "token");

            // Step 2: Login to Copin
            logger.info("Step 2: Logging into Copin");
            Response loginResponse = login(apiBaseUrl, token);
            validateResponse(loginResponse, "Login Copin");

            cachedToken = extractToken(loginResponse, "access_token");
            tokenInitialized = true;

            logger.info("✅ Authentication completed successfully");

        } catch (Exception e) {
            logger.error("❌ Authentication failed", e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    private Response verifyOTP(String email, String otpCode) {
        RequestSpecification request = RestAssured.given()
                .baseUri(config.getApiBaseUrl())
                .contentType("application/json")
                .header("privy-app-id", "cm7d2vu66037010jr8dm0thd9")
                .body(Map.of(
                        "email", email,
                        "code", otpCode,
                        "mode", "login-or-sign-up"
                ));

        return request.post(ApiEndpoints.VERIFY_OTP);
    }

    private Response login(String apiBaseUrl, String token) {
        RequestSpecification request = RestAssured.given()
                .baseUri(apiBaseUrl)
                .contentType("application/json")
                .body(Map.of("jwt", token));

        return request.post(ApiEndpoints.LOGIN);
    }

    private void validateResponse(Response response, String step) {
        int statusCode = response.getStatusCode();

        if (statusCode != 200 && statusCode != 201) {
            String errorMsg = String.format("%s failed with status %d: %s",
                    step, statusCode, response.getBody().asString());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    private String extractToken(Response response, String tokenField) {
        String token = response.jsonPath().getString(tokenField);

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token field '" + tokenField + "' not found");
        }

        return token;
    }

    public String refreshToken() {
        logger.info("Refreshing token...");
        tokenInitialized = false;
        cachedToken = null;
        return getToken();
    }

    public void clearToken() {
        cachedToken = null;
        tokenInitialized = false;
    }
}
