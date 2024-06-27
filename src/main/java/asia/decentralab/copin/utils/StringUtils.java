package asia.decentralab.copin.utils;

import java.util.UUID;

public class StringUtils {
    public static String generateRandomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
