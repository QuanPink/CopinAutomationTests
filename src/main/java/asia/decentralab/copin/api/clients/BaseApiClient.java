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
    protected final String xApiKey;
    protected RequestSpecification requestSpec;

    public BaseApiClient() {
        this.baseUrl = config.getApiBaseUrl();
        this.xApiKey = config.getXApiKey();

        if (xApiKey == null || xApiKey.isEmpty() || "123456".equals(xApiKey)) {
            logger.warn("X_API_KEY is not configured properly!");
        }

        setupRequestSpec();
        logger.info("{} initialized with base URL: {}", this.getClass().getSimpleName(), baseUrl);
    }

    private void setupRequestSpec() {
        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON);

        this.requestSpec = specBuilder.build();
    }

    protected RequestSpecification getAuthenticatedSpec() {
        return RestAssured.given(requestSpec)
                .header("x-api-key", xApiKey);
    }
}
