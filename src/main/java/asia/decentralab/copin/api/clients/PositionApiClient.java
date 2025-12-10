package asia.decentralab.copin.api.clients;

import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionApiClient extends BaseApiClient {
    public PositionApiClient() {
        super();
    }

    /**
     * Get positions by filter with protocol and endpoint
     */
    public Response getPositionsByFilter(String protocol, String endpoint) {
        String fullEndpoint = buildEndpoint(protocol, endpoint);
        logger.info("Getting positions for endpoint: {}", fullEndpoint);

        Map<String, Object> body = createDefaultFilterBody();

        return getAuthenticatedSpec()
                .body(body)
                .when()
                .post(fullEndpoint)
                .then()
                .extract()
                .response();
    }

    public Response getPositionsByAccountAndProtocol(String protocol, String account, String endpoint) {
        String fullEndpoint = buildEndpoint(protocol, endpoint);
        logger.info("Getting positions for account: {} at endpoint: {}", account, fullEndpoint);

        Map<String, Object> body = createFilterBodyWithAccount(account);

        return getAuthenticatedSpec()
                .body(body)
                .when()
                .post(fullEndpoint)
                .then()
                .extract()
                .response();
    }

    /**
     * Get position detail by ID
     */
    public Response getPositionDetail(String protocol, String endpoint, String positionId) {
        String fullEndpoint = buildEndpoint(protocol, endpoint);
        logger.info("Getting position detail for endpoint: {} and ID: {}", fullEndpoint, positionId);

        return getAuthenticatedSpec()
                .pathParam("id", positionId)
                .when()
                .get(fullEndpoint)
                .then()
                .extract()
                .response();
    }

    /**
     * Build full endpoint from protocol and path
     */
    private String buildEndpoint(String protocol, String endpoint) {
        return "/" + protocol + endpoint;
    }

    /**
     * Create default body for filter requests
     */
    private Map<String, Object> createDefaultFilterBody() {
        Map<String, Object> body = new HashMap<>();

        // Default pagination
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("limit", 500);
        pagination.put("offset", 0);
        body.put("pagination", pagination);

        // Default sorting
        body.put("sortBy", "openBlockTime");
        body.put("sortType", "desc");

        return body;
    }

    private Map<String, Object> createFilterBodyWithAccount(String account) {
        Map<String, Object> body = new HashMap<>();

        // Default pagination
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("limit", 1000);
        pagination.put("offset", 0);
        body.put("pagination", pagination);

        List<Map<String, Object>> queries = new ArrayList<>();
        Map<String, Object> statusQuery = new HashMap<>();
        statusQuery.put("fieldName", "status");
        statusQuery.put("value", "CLOSE");
        queries.add(statusQuery);

        Map<String, Object> accountQuery = new HashMap<>();
        accountQuery.put("fieldName", "account");
        accountQuery.put("value", account);
        queries.add(accountQuery);

        body.put("queries", queries);

        // Default sorting
        body.put("sortBy", "closeBlockTime");
        body.put("sortType", "desc");

        return body;
    }
}
