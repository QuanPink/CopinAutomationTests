package asia.decentralab.copin.utils;

import asia.decentralab.copin.config.EnvironmentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LarkNotifier {
    private static final Logger logger = LoggerFactory.getLogger(LarkNotifier.class);

    private static String getWebhookUrl() {
        EnvironmentConfig config = EnvironmentConfig.getInstance();
        return config.getLarkWebhookUrl();
    }

    public static void sendTestReport(String suiteName, int total, int passed, int failed, int skipped, String duration) {
        if (getWebhookUrl() == null || getWebhookUrl().isEmpty()) {
            logger.warn("LARK_WEBHOOK_URL not configured, skipping notification");
            return;
        }

        try {
            String status = failed > 0 ? "❌ FAILED" : "✅ PASSED";
            String color = failed > 0 ? "red" : "green";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String message = String.format(
                    "{\n" +
                            "  \"msg_type\": \"interactive\",\n" +
                            "  \"card\": {\n" +
                            "    \"header\": {\n" +
                            "      \"title\": {\n" +
                            "        \"content\": \"🧪 Test Report - %s\",\n" +
                            "        \"tag\": \"plain_text\"\n" +
                            "      },\n" +
                            "      \"template\": \"%s\"\n" +
                            "    },\n" +
                            "    \"elements\": [\n" +
                            "      {\n" +
                            "        \"tag\": \"div\",\n" +
                            "        \"text\": {\n" +
                            "          \"content\": \"**Suite:** %s\\n**Status:** %s\\n**Time:** %s\",\n" +
                            "          \"tag\": \"lark_md\"\n" +
                            "        }\n" +
                            "      },\n" +
                            "      {\n" +
                            "        \"tag\": \"hr\"\n" +
                            "      },\n" +
                            "      {\n" +
                            "        \"tag\": \"div\",\n" +
                            "        \"fields\": [\n" +
                            "          {\n" +
                            "            \"is_short\": true,\n" +
                            "            \"text\": {\n" +
                            "              \"content\": \"**✅ Passed**\\n%d\",\n" +
                            "              \"tag\": \"lark_md\"\n" +
                            "            }\n" +
                            "          },\n" +
                            "          {\n" +
                            "            \"is_short\": true,\n" +
                            "            \"text\": {\n" +
                            "              \"content\": \"**❌ Failed**\\n%d\",\n" +
                            "              \"tag\": \"lark_md\"\n" +
                            "            }\n" +
                            "          },\n" +
                            "          {\n" +
                            "            \"is_short\": true,\n" +
                            "            \"text\": {\n" +
                            "              \"content\": \"**⏭️ Skipped**\\n%d\",\n" +
                            "              \"tag\": \"lark_md\"\n" +
                            "            }\n" +
                            "          },\n" +
                            "          {\n" +
                            "            \"is_short\": true,\n" +
                            "            \"text\": {\n" +
                            "              \"content\": \"**📊 Total**\\n%d\",\n" +
                            "              \"tag\": \"lark_md\"\n" +
                            "            }\n" +
                            "          }\n" +
                            "        ]\n" +
                            "      },\n" +
                            "      {\n" +
                            "        \"tag\": \"div\",\n" +
                            "        \"text\": {\n" +
                            "          \"content\": \"**Duration:** %s\",\n" +
                            "          \"tag\": \"lark_md\"\n" +
                            "        }\n" +
                            "      }\n" +
                            "    ]\n" +
                            "  }\n" +
                            "}",
                    suiteName, color, suiteName, status, timestamp, passed, failed, skipped, total, duration
            );

            sendToLark(message);
            logger.info("✅ Test report sent to Lark successfully");

        } catch (Exception e) {
            logger.error("❌ Failed to send notification to Lark: {}", e.getMessage());
        }
    }

    public static void sendFailureCard(String protocol, String timestamp, List<String> failureDetails) {
        String webhookUrl = getWebhookUrl();

        if (webhookUrl == null || webhookUrl.isEmpty()) {
            logger.warn("⚠️  LARK_WEBHOOK_URL not configured");
            return;
        }

        logger.info("🚨 Sending failure card to Lark...");

        try {
            // Escape protocol name
            String safeProtocol = escapeJson(protocol);

            // Join failures với newline
            String allFailures = String.join("\n\n", failureDetails);
            String safeFailures = escapeJson(allFailures);

            String message = String.format(
                    "{\n" +
                            "  \"msg_type\": \"interactive\",\n" +
                            "  \"card\": {\n" +
                            "    \"header\": {\n" +
                            "      \"title\": {\n" +
                            "        \"content\": \"🚨 %s Failure\",\n" +
                            "        \"tag\": \"plain_text\"\n" +
                            "      },\n" +
                            "      \"template\": \"red\"\n" +
                            "    },\n" +
                            "    \"elements\": [\n" +
                            "      {\n" +
                            "        \"tag\": \"div\",\n" +
                            "        \"text\": {\n" +
                            "          \"content\": \"**Time:** %s\",\n" +
                            "          \"tag\": \"lark_md\"\n" +
                            "        }\n" +
                            "      },\n" +
                            "      {\n" +
                            "        \"tag\": \"hr\"\n" +
                            "      },\n" +
                            "      {\n" +
                            "        \"tag\": \"div\",\n" +
                            "        \"text\": {\n" +
                            "          \"content\": \"%s\",\n" +
                            "          \"tag\": \"lark_md\"\n" +
                            "        }\n" +
                            "      }\n" +
                            "    ]\n" +
                            "  }\n" +
                            "}",
                    safeProtocol, timestamp, safeFailures
            );

            sendToLark(message);
            logger.info("✅ Failure card sent to Lark successfully");

        } catch (Exception e) {
            logger.error("❌ Failed to send failure card: {}", e.getMessage(), e);
        }
    }

    private static void sendToLark(String jsonPayload) throws Exception {
        URL url = new URL(getWebhookUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP error code: " + responseCode);
        }
    }

    private static String escapeJson(String text) {
        if (text == null) return "";

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}