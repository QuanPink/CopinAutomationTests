package asia.decentralab.copin.api.clients;

import asia.decentralab.copin.utils.AuthTokenProvider;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class FlexibleApiClient extends BaseApiClient {

    public FlexibleApiClient() {
        super();
        logger.info("Flexible API Client initialized with base URL: {}", baseUrl);
    }

    /**
     * Get positions by filter with protocol and endpoint
     *
     * @param protocol Protocol type (e.g., "gns_ape", "gns_arb", "gns_base", "avantis_base")
     * @param endpoint Endpoint path (e.g., "/position/filter")
     * @return Response containing filtered positions
     */
    public Response getPositionsByFilter(String protocol, String endpoint) {
        // Combine protocol + endpoint
        String fullEndpoint = "/" + protocol + endpoint;
        logger.info("Getting positions for full endpoint: {}", fullEndpoint);

        // Create default body
        Map<String, Object> body = createDefaultFilterBody();

        return getBaseSpec()
                .header("Content-Type", "application/json")
                .header("authorization", AuthTokenProvider.getInstance().getToken())
                .body(body)
                .when()
                .post(fullEndpoint)
                .then()
                .extract()
                .response();
    }

    /**
     * Get position detail by ID with protocol and endpoint
     *
     * @param protocol   Protocol type (e.g., "gns_ape", "gns_arb", "gns_base", "avantis_base")
     * @param endpoint   Endpoint path with {id} placeholder (e.g., "/position/mongodb/detail/{id}")
     * @param positionId Position ID to get details for
     * @return Response containing position details
     */
    public Response getPositionDetail(String protocol, String endpoint, String positionId) {
        // Combine protocol + endpoint
        String fullEndpoint = "/" + protocol + endpoint;
        logger.info("Getting position detail for full endpoint: {} and ID: {}", fullEndpoint, positionId);

        return getBaseSpec()
                .header("authorization", AuthTokenProvider.getInstance().getToken())
                .pathParam("id", positionId)
                .when()
                .get(fullEndpoint)
                .then()
                .extract()
                .response();
    }

    /**
     * Create default body for filter requests
     */
    private Map<String, Object> createDefaultFilterBody() {
        Map<String, Object> body = new HashMap<>();

        // Default pagination
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("limit", 2);
        pagination.put("offset", 0);
        body.put("pagination", pagination);

        // Default sorting
        body.put("sortBy", "openBlockTime");
        body.put("sortType", "desc");

        return body;
    }
}