package asia.decentralab.copin.api.clients;

import asia.decentralab.copin.api.auth.AuthTokenProvider;
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

    protected RequestSpecification getAuthenticatedSpec() {
        return RestAssured.given(requestSpec)
                .header("authorization", getAuthToken());
    }

    protected RequestSpecification getBaseSpec() {
        return RestAssured.given(requestSpec);
    }

    private String getAuthToken() {
        AuthTokenProvider authProvider = AuthTokenProvider.getInstance();

        if (!authProvider.isLoggedIn()) {
            throw new IllegalStateException("Authentication required. Ensure login is performed in test setup.");
        }

        return authProvider.getToken();
    }
}
