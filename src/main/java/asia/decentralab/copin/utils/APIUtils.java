package asia.decentralab.copin.utils;

import asia.decentralab.copin.data.enumdata.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class APIUtils {

    public String sendRequest(String url, HttpMethod method, Map<String, String> headers, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url));

        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }
        switch (method) {
            case GET:
                requestBuilder.GET();
                break;
            case POST:
                if (body == null || body.isEmpty()) {
                    throw new IllegalArgumentException("Body cannot be null or empty for POST request");
                } else {
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
                }
                break;
            case PUT:
                if (body == null || body.isEmpty()) {
                    throw new IllegalArgumentException("Body cannot be null or empty for PUT request");
                } else {
                    requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body));
                }
                break;
            case DELETE:
                if (body == null || body.isEmpty()) {
                    requestBuilder.DELETE();
                } else {
                    requestBuilder.method("DELETE", HttpRequest.BodyPublishers.ofString(body));
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("API call failed with status code: " + response.statusCode() + " " + response.body());
        }

        return response.body();
    }
}