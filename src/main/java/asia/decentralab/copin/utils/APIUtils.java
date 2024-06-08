package asia.decentralab.copin.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class APIUtils {

    public String callApi(String url, String method, Map<String, String> headers, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url));

        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }

        switch (method.toUpperCase()) {
            case "GET":
                requestBuilder.GET();
                break;
            case "POST":
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
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
