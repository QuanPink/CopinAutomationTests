package asia.decentralab.copin.data;

import java.util.HashMap;
import java.util.Map;

public class ProtocolData {
    public interface ApiConfig {
        String getUrl();
        String getMethod();
        Map<String, String> getHeaders();
        String getBody(); // Thêm trường body
    }

    public static class GMXApiConfig implements ApiConfig {
        @Override
        public String getUrl() {
            return "https://api.copin.io/GMX/position/statistic/filter";
        }

        @Override
        public String getMethod() {
            return "POST";
        }

        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("accept", "application/json, text/plain, */*");
            headers.put("authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IjB4QTI3NTUxNGIxM0JDMUE2YWYxNGY1MEMyZEQxQ0Q4ZGM4QkJkOEJlMSIsInRpbWUiOjE3MTczNDA0ODU5MjksImFjY2VzcyI6ImFOd1lBaEhTTjIxNzE3MzQwNDg1OTI5IiwiaWF0IjoxNzE3MzQwNDg1LCJleHAiOjE3MTczNDA1NDV9.VHJLon5NPYR9rnhSi9VJX_fFaX7prrUBlpePGA-JiVM");
            headers.put("content-type", "application/json");
            headers.put("origin", "https://vip.copin.io");
            headers.put("priority", "u=1, i");
            headers.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36 Edg/125.0.0.0");
            return headers;
        }

        @Override
        public String getBody() {
            return "{\"pagination\":{\"limit\":20,\"offset\":0},\"queries\":[{\"fieldName\":\"type\",\"value\":\"D7\"}],\"ranges\":[{\"fieldName\":\"realisedPnl\",\"gte\":300},{\"fieldName\":\"orderPositionRatio\",\"lte\":3},{\"fieldName\":\"avgLeverage\",\"gte\":10},{\"fieldName\":\"maxDuration\",\"lte\":3600}],\"sortBy\":\"realisedPnl\",\"sortType\":\"desc\"}";
        }
    }

    public static class GNSApiConfig implements ApiConfig {
        @Override
        public String getUrl() {
            return "https://api.copin.io/GNS/position/statistic/filter";
        }

        @Override
        public String getMethod() {
            return "POST";
        }

        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("content-type", "application/json");
            headers.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36 Edg/125.0.0.0");
            return headers;
        }

        @Override
        public String getBody() {
            return "{\"pagination\":{\"limit\":20,\"offset\":0},\"queries\":[{\"fieldName\":\"type\",\"value\":\"FULL\"}],\"ranges\":[{\"fieldName\":\"realisedPnl\",\"gte\":300},{\"fieldName\":\"orderPositionRatio\",\"lte\":3},{\"fieldName\":\"avgLeverage\",\"gte\":10},{\"fieldName\":\"maxDuration\",\"lte\":3600}],\"sortBy\":\"realisedPnl\",\"sortType\":\"desc\"}";
        }
    }
}
