package asia.decentralab.copin.test.utils.validators;

import java.util.Map;

import static asia.decentralab.copin.utils.MapUtils.getDouble;
import static asia.decentralab.copin.utils.ValidationUtils.assertInRange;
import static asia.decentralab.copin.utils.ValidationUtils.assertNotNull;

public class BaseOrderValidator {
    protected static void validateCommonFields(Map<String, Object> order) {
        assertNotNull(order.get("id"), "order id should not be null");
        assertNotNull(order.get("account"), "order account should not be null");
        assertNotNull(order.get("protocol"), "order protocol should not be null");
        assertNotNull(order.get("indexToken"), "order indexToken should not be null");
        assertNotNull(order.get("pair"), "order pair should not be null");
        assertNotNull(order.get("type"), "order type should not be null");
        assertNotNull(order.get("blockTime"), "order blockTime should not be null");
        assertNotNull(order.get("blockNumber"), "order blockNumber should not be null");
    }

    public static void validateTradingOrders(Map<String, Object> order) {
        validateCommonFields(order);

        String account = (String) order.get("account");

        if (!"MARGIN_TRANSFERRED".equals(order.get("type"))) {
            assertNotNull(order.get("isLong"), "isLong should not be null");

            assertInRange(getDouble(order, "sizeDeltaNumber"), 0, 100_000_000,
                    "sizeDeltaNumber", account
            );

            assertInRange(getDouble(order, "sizeNumber"), 0, 100_000_000,
                    "sizeNumber", account
            );

            assertInRange(getDouble(order, "priceNumber"), 0, 1_000_000,
                    "priceNumber", account
            );
        } else {
            assertInRange(getDouble(order, "collateralDeltaNumber"), -1_000_000, 1_000_000,
                    "collateralDeltaNumber", account
            );
            assertInRange(getDouble(order, "collateralNumber"), 0, 1_000_000,
                    "collateralNumber", account
            );
        }
    }
}