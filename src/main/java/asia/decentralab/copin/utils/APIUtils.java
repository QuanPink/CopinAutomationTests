package asia.decentralab.copin.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class APIUtils {
    private static final String CONTENT_TYPE_JSON = "application/json";

    public static Response sendGetRequest(String baseUrl, String endpoint) {
        return RestAssured.given()
                .baseUri(baseUrl)
                .when()
                .get(endpoint);
    }

    public static Response sendPostRequest(String baseUrl, String endpoint, Object requestBody) {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Content-Type", CONTENT_TYPE_JSON)
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    public static Response sendPutRequest(String baseUrl, String endpoint, Object requestBody) {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Content-Type", CONTENT_TYPE_JSON)
                .body(requestBody)
                .when()
                .put(endpoint);
    }

    public static Response sendDeleteRequest(String baseUrl, String endpoint) {
        return RestAssured.given()
                .baseUri(baseUrl)
                .when()
                .delete(endpoint);
    }
}