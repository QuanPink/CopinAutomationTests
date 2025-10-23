package asia.decentralab.copin.api.clients;

import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraderStatisticApiClient extends BaseApiClient {
    public TraderStatisticApiClient() {
        super();
        logger.info("Position API Client initialized with base URL: {}", baseUrl);
    }

    public Response getTraderStatisticByProtocol(String protocol, String time,  String endpoint) {
        String fullEndpoint = buildEndpoint(protocol, endpoint);
        logger.info("Getting positions for endpoint: {}", fullEndpoint);

        Map<String, Object> body = createFilterBodyWithTimeFrame(time);

        return getAuthenticatedSpec()
                .body(body)
                .when()
                .post(fullEndpoint)
                .then()
                .extract()
                .response();
    }

    private String buildEndpoint(String protocol, String endpoint) {
        return "/" + protocol + endpoint;
    }

    private Map<String, Object> createFilterBodyWithTimeFrame(String time) {
        Map<String, Object> body = new HashMap<>();

        // Default pagination
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("limit", 500);
        pagination.put("offset", 0);
        body.put("pagination", pagination);

        List<Map<String, Object>> queries = new ArrayList<>();
        Map<String, Object> statusQuery = new HashMap<>();
        statusQuery.put("fieldName", "type");
        statusQuery.put("value", time);
        queries.add(statusQuery);

        body.put("queries", queries);

        body.put("sortBy", "realisedPnl");
        body.put("sortType", "desc");

        return body;
    }
}
