package asia.decentralab.copin.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class StringUtils {
    public static String generateRandomString() {
        return RandomStringUtils.randomAlphanumeric(20).replace("-", "");
    }
}
