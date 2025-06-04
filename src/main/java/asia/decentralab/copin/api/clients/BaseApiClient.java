package asia.decentralab.copin.api.clients;

import asia.decentralab.copin.config.EnvironmentConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseApiClient {
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiClient.class);
    protected final EnvironmentConfig config = EnvironmentConfig.getInstance();
    protected final String baseUrl;
    protected RequestSpecification requestSpec;

    public BaseApiClient() {
        this.baseUrl = config.getApiBaseUrl();
        setupRequestSpec();
    }

    private void setupRequestSpec() {
        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON);

        this.requestSpec = specBuilder.build();
    }

    public void setAuthToken(String token) {
        if (token != null && !token.isEmpty()) {
            this.requestSpec = RestAssured.given(this.requestSpec)
                    .header("Authorization", "Bearer " + token);
            logger.info("Auth token set for API client");
        }
    }

    public void clearAuthToken() {
        setupRequestSpec();
        logger.info("Auth token cleared from API client");
    }

    protected RequestSpecification getBaseSpec() {
        return RestAssured.given(requestSpec);
    }
}
