package asia.decentralab.copin.test.utils.validators;

import asia.decentralab.copin.utils.ValidationUtils;

import java.util.Map;

public class BaseOrderValidator {
    protected static void validateCommonFields(Map<String, Object> order) {
        ValidationUtils.assertNotNull(order.get("indexToken"), "indexToken should not be null");
        ValidationUtils.assertNotNull(order.get("account"), "account should not be null");
        ValidationUtils.assertNotNull(order.get("pair"), "pair should not be null");
        ValidationUtils.assertNotNull(order.get("type"), "type should not be null");
        ValidationUtils.assertNotNull(order.get("isLong"), "isLong should not be null");
    }

    public static void validateTradingOrders(Map<String, Object> order) {
        validateCommonFields(order);

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "sizeDeltaNumber"),
                0, 1000000000, "sizeDeltaNumber"
        );

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "priceNumber"),
                0, 300000, "priceNumber"
        );
    }
}