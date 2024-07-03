package asia.decentralab.copin.utils;

import asia.decentralab.copin.data.ProtocolData;
import asia.decentralab.copin.data.enumdata.HttpMethod;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIUtils {
    private static final Gson gson = new Gson();

    public static String sendRequest(ProtocolData protocolData) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(protocolData.getUrl()));

            if (protocolData.getHeader() != null) {
                protocolData.getHeader().forEach((key, value) -> requestBuilder.header(key, value.toString()));
            }

            switch (HttpMethod.valueOf(protocolData.getMethod())) {
                case GET:
                    requestBuilder.GET();
                    break;
                case POST:
                    String requestBodyJson = gson.toJson(protocolData.getBody());
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBodyJson));
                    break;
                case PUT:
                    String putBodyJson = gson.toJson(protocolData.getBody());
                    requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(putBodyJson));
                    break;
                case DELETE:
                    if (protocolData.getBody() == null) {
                        requestBuilder.DELETE();
                    } else {
                        String deleteBodyJson = gson.toJson(protocolData.getBody());
                        requestBuilder.method("DELETE", HttpRequest.BodyPublishers.ofString(deleteBodyJson));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported method: " + protocolData.getMethod());
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("API call failed with status code: " + response.statusCode() + " " + response.body());
            }
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}