package asia.decentralab.copin.utils.validators;

import asia.decentralab.copin.utils.ValidationUtils;
import java.util.Map;

public class OrderValidator {

    public static void validateCommonFields(Map<String, Object> order) {
        ValidationUtils.assertNotNull(order.get("indexToken"), "indexToken should not be null");
        ValidationUtils.assertNotNull(order.get("account"), "account should not be null");
        ValidationUtils.assertNotNull(order.get("pair"), "pair should not be null");
        ValidationUtils.assertNotNull(order.get("type"), "type should not be null");
        ValidationUtils.assertNotNull(order.get("isLong"), "isLong should not be null");
    }

    public static void validateTradingOrders(Map<String, Object> order) {
        boolean isOpen = Boolean.TRUE.equals(order.get("isOpen"));
        boolean isClose = Boolean.TRUE.equals(order.get("isClose"));

        validateCommonFields(order);

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "collateralDeltaNumber"),
                0, 10000000, "collateralDeltaNumber"
        );

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "collateralNumber"),
                0, 10000000, "collateralNumber"
        );

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "sizeDeltaNumber"),
                0, 1000000000, "sizeDeltaNumber"
        );

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "sizeNumber"),
                0, 1000000000, "sizeNumber"
        );

        if (isOpen) {
            double collateralDelta = ValidationUtils.getDoubleValue(order, "collateralDeltaNumber");
            double collateral = ValidationUtils.getDoubleValue(order, "collateralNumber");
            double sizeDelta = ValidationUtils.getDoubleValue(order, "sizeDeltaNumber");
            double size = ValidationUtils.getDoubleValue(order, "sizeNumber");
            ValidationUtils.assertCloseToValue(collateralDelta, collateral, 0.01, "OPEN order collateral consistency");
            ValidationUtils.assertCloseToValue(sizeDelta, size, 0.01, "OPEN order collateral consistency");
        }

        if (isClose) {
            double finalCollateral = ValidationUtils.getDoubleValue(order, "collateralNumber");
            double finalSize = ValidationUtils.getDoubleValue(order, "sizeNumber");

            ValidationUtils.assertEquals(finalCollateral, 0.0, "CLOSE order should result in zero collateral");
            ValidationUtils.assertEquals(finalSize, 0.0, "CLOSE order should result in zero size");
        }

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "priceNumber"),
                0, 300000, "priceNumber"
        );

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "leverage"),
                0, 10000, "leverage"
        );
    }

    public static void validateMarginTransferOrder(Map<String, Object> order) {
        validateCommonFields(order);

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "collateralDeltaNumber"),
                -1000000000, 1000000000, "collateralDeltaNumber for margin transfer"
        );

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "collateralNumber"),
                0, 1000000000, "collateralNumber"
        );
    }

    public static void validateOrderByType(Map<String, Object> order) {
        String orderType = (String) order.get("type");

        switch (orderType) {
            case "OPEN":
            case "INCREASE":
            case "DECREASE":
            case "CLOSE":
            case "LIQUIDATE":
                validateTradingOrders(order);
                break;
            case "MARGIN_TRANSFERRED":
                validateMarginTransferOrder(order);
                break;
            default:
                throw new IllegalArgumentException("Unknown order type: " + orderType);
        }
    }
}