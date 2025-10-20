package asia.decentralab.copin.test.utils.validators;

import asia.decentralab.copin.utils.ValidationUtils;

import java.util.Map;

public class BaseOrderValidator {
    protected static void validateCommonFields(Map<String, Object> order) {
        ValidationUtils.assertNotNull(order.get("id"), "order id should not be null");
        ValidationUtils.assertNotNull(order.get("account"), "order account should not be null");
        ValidationUtils.assertNotNull(order.get("protocol"), "order protocol should not be null");
        ValidationUtils.assertNotNull(order.get("indexToken"), "order indexToken should not be null");
        ValidationUtils.assertNotNull(order.get("pair"), "order pair should not be null");
        ValidationUtils.assertNotNull(order.get("type"), "order type should not be null");
        ValidationUtils.assertNotNull(order.get("blockTime"), "order blockTime should not be null");
        ValidationUtils.assertNotNull(order.get("blockNumber"), "order blockNumber should not be null");
    }

    public static void validateTradingOrders(Map<String, Object> order) {
        validateCommonFields(order);

        if (!"MARGIN_TRANSFERRED".equals(order.get("type"))) {
            ValidationUtils.assertNotNull(order.get("isLong"), "isLong should not be null");

            ValidationUtils.assertInRange(
                    ValidationUtils.getDoubleValue(order, "sizeDeltaNumber"),
                    0,  // min
                    100_000_000,
                    "sizeDeltaNumber"
            );

            ValidationUtils.assertInRange(
                    ValidationUtils.getDoubleValue(order, "sizeNumber"),
                    0,  // min
                    100_000_000,
                    "sizeNumber"
            );

            ValidationUtils.assertInRange(
                    ValidationUtils.getDoubleValue(order, "priceNumber"),
                    0,
                    1_000_000,
                    "priceNumber"
            );
        } else {
            ValidationUtils.assertInRange(
                    ValidationUtils.getDoubleValue(order, "collateralDeltaNumber"),
                    -1_000_000,
                    1_000_000,
                    "collateralDeltaNumber"
            );
            ValidationUtils.assertInRange(
                    ValidationUtils.getDoubleValue(order, "collateralNumber"),
                    0,
                    1_000_000,
                    "collateralNumber"
            );
        }
    }
}