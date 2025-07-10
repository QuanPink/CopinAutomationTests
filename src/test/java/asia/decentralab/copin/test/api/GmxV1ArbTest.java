package asia.decentralab.copin.test.api;

import asia.decentralab.copin.api.clients.FlexibleApiClient;
import asia.decentralab.copin.test.base.BaseApiTest;
import asia.decentralab.copin.utils.AuthTokenProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class GmxV1ArbTest extends BaseApiTest {
    private FlexibleApiClient apiClient;

    @BeforeClass
    public void setUp() {
        apiClient = new FlexibleApiClient();
        String authToken = AuthTokenProvider.getInstance().getToken();
        apiClient.setAuthToken(authToken);
    }

    @Test
    public void testGetPositionsByFilter() {
        try {
            Response responsePositions = apiClient.getPositionsByFilter("gns", "/position/filter");
            List<String> ids = responsePositions.jsonPath().getList("data.id", String.class);

            for (String id : ids) {
                Response responsePositionDetail = apiClient.getPositionDetail("gns", "/position/detail/{id}", id);

                Map<String, Object> positionMap = responsePositionDetail.jsonPath().getMap("");
                List<Map<String, Object>> orders = (List<Map<String, Object>>) (List<?>) responsePositionDetail.jsonPath().getList("orders", Map.class);
                validatePosition(positionMap, orders);

                for (Map<String, Object> order : responsePositionDetail.jsonPath().getList("orders", Map.class)) {
                    validateOrders(order);
                }

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void validateOrders(Map<String, Object> order) {
        Assert.assertNotNull(order.get("indexToken"), "indexToken should be not null");
        Assert.assertNotNull(order.get("account"), "account should be not null");
        Assert.assertNotNull(order.get("pair"), "pair should be not null");

        switch ((String) order.get("type")) {
            case "OPEN":
            case "INCREASE":
                Boolean isOpenType = order.get("type").equals("OPEN") || Boolean.TRUE.equals(order.get("isOpen"));

                Assert.assertTrue(getDoubleValue(order, "collateralDeltaNumber") > 0 &&
                        getDoubleValue(order, "collateralDeltaNumber") < 1000000000, " should be between 0 and ");

                Assert.assertTrue(getDoubleValue(order, "sizeDeltaNumber") > 0 &&
                        getDoubleValue(order, "sizeDeltaNumber") < 1000000000, " should be between 0 and ");

                if (isOpenType) {
//                    Assert.assertEquals(getDoubleValue(order, "collateralDeltaNumber"),
//                            getDoubleValue(order, "collateralNumber"), "invalid collateralDeltaNumber");

//                    Assert.assertEquals(getDoubleValue(order, "sizeDeltaNumber"),
//                            getDoubleValue(order, "sizeNumber"), "invalid sizeDeltaNumber");
                } else {
                    Assert.assertTrue(getDoubleValue(order, "sizeDeltaNumber") >
                            getDoubleValue(order, "sizeNumber"), "invalid sizeDeltaNumber");

                    Assert.assertTrue(getDoubleValue(order, "collateralNumber") > 0,
                            "invalid sizeDeltaNumber");
                    Assert.assertTrue(getDoubleValue(order, "sizeNumber") > 0,
                            "invalid sizeDeltaNumber");
                }

                Assert.assertTrue(getDoubleValue(order, "priceNumber") > 0 &&
                        getDoubleValue(order, "priceNumber") < 1000000000, " should be between 0 and ");

                Assert.assertNotNull(order.get("isLong"), "pair should be not null");

                Assert.assertTrue(getDoubleValue(order, "leverage") > 0 &&
                        getDoubleValue(order, "leverage") < 10000, " should be between 0 and ");

                break;

            case "MARGIN_TRANSFERRED":
                Assert.assertTrue(getDoubleValue(order, "collateralDeltaNumber") > 0 &&
                        getDoubleValue(order, "collateralDeltaNumber") < 1000000000, " should be between 0 and ");

                Assert.assertTrue(getDoubleValue(order, "collateralNumber") > 0 &&
                        getDoubleValue(order, "sizeDeltaNumber") < 1000000000, " should be between 0 and ");
                break;

            case "DECREASE":
            case "CLOSE":
            case "LIQUIDATE":
                Boolean isCloseType = order.get("type").equals("CLOSE") || order.get("type").equals("LIQUIDATION") || Boolean.TRUE.equals(order.get("isClose"));

                Assert.assertTrue(getDoubleValue(order, "collateralDeltaNumber") > 0 &&
                        getDoubleValue(order, "collateralDeltaNumber") < 1000000000, " should be between 0 and ");

                Assert.assertTrue(getDoubleValue(order, "sizeDeltaNumber") > 0 &&
                        getDoubleValue(order, "sizeDeltaNumber") < 1000000000, " should be between 0 and ");

                if (isCloseType) {
                    Assert.assertTrue(getDoubleValue(order, "collateralDeltaNumber") >
                            getDoubleValue(order, "collateralNumber"), "invalid collateralDeltaNumber");

                    Assert.assertTrue(getDoubleValue(order, "sizeDeltaNumber") >
                            getDoubleValue(order, "sizeNumber"), "invalid sizeDeltaNumber");

                    Assert.assertEquals(getDoubleValue(order, "collateralNumber"), 0,
                            "invalid collateralNumber");
                    Assert.assertEquals(getDoubleValue(order, "sizeNumber"), 0,
                            "invalid sizeNumber");
                } else {
                    Assert.assertTrue(getDoubleValue(order, "sizeDeltaNumber") <=
                            getDoubleValue(order, "sizeNumber"), "invalid sizeDeltaNumber");

                    Assert.assertTrue(getDoubleValue(order, "collateralNumber") > 0,
                            "invalid sizeDeltaNumber");
                    Assert.assertTrue(getDoubleValue(order, "sizeNumber") > 0,
                            "invalid sizeDeltaNumber");
                }

                Assert.assertTrue(getDoubleValue(order, "priceNumber") > 0 &&
                        getDoubleValue(order, "priceNumber") < 1000000000, " should be between 0 and ");

                Assert.assertNotNull(order.get("isLong"), "pair should be not null");

                Assert.assertTrue(getDoubleValue(order, "leverage") > 0 &&
                        getDoubleValue(order, "leverage") < 10000, " should be between 0 and ");

                break;
        }
    }

    public void validatePosition(Map<String, Object> position, List<Map<String, Object>> orders) {
        PositionCalculationResult calc = calculatePosition(orders);

        // First type order
        Assert.assertEquals(orders.get(0).get("isOpen"), true, "First order isOpen should be true");
        Assert.assertEquals(calc.isOpenCount, 1, "isOpenCount should be 1");

        // Collateral
        double collateral = calc.collateral;
        double lastCollateral = calc.lastCollateral;
        Assert.assertTrue(Math.abs(collateral - getDoubleValue(position, "collateral")) <= Math.abs(collateral * 0.01) && collateral > 0, "Collateral close to position.collateral and > 0");
        Assert.assertTrue(Math.abs(lastCollateral - getDoubleValue(position, "lastCollateral")) <= Math.abs(lastCollateral * 0.01) && lastCollateral >= 0, "lastCollateral close to position.lastCollateral and >= 0");

        // Size
        double size = calc.size;
        double lastSize = calc.lastSize;
        Assert.assertTrue(Math.abs(size - getDoubleValue(position, "size")) <= Math.abs(size * 0.01) && size > 0, "Size close to position.size and > 0");
        Assert.assertTrue(Math.abs(lastSize - getDoubleValue(position, "lastSize")) <= Math.abs(lastSize * 0.01) && lastSize >= 0, "lastSize close to position.lastSize and >= 0");

        // Average Price
        double avgPrice = calc.avgPrice;
        Assert.assertTrue(Math.abs(avgPrice - getDoubleValue(position, "averagePrice")) <= Math.abs(avgPrice * 0.01) && avgPrice > 0, "avgPrice close to position.averagePrice and > 0");

        // Leverage
        Assert.assertTrue(Math.abs((size / collateral) - getDoubleValue(position, "leverage")) <= Math.abs((size / collateral) * 0.01) && (size / collateral) > 0, "leverage close to position.leverage and > 0");

        // Is Long
        Assert.assertEquals(orders.get(0).get("isLong"), position.get("isLong"), "isLong should match");

        // Order count
        int orderIncreaseCount = calc.orderIncreaseCount;
        int orderDecreaseCount = calc.orderDecreaseCount;
        int orderMarginTransferCount = calc.orderMarginTransferCount;
        int orderLiquidateCount = calc.orderLiquidateCount;
        Assert.assertEquals(orderIncreaseCount + orderDecreaseCount + orderMarginTransferCount + orderLiquidateCount, getIntValue(position, "orderCount"), "Order count should match and > 0");
        Assert.assertTrue(orderIncreaseCount == getIntValue(position, "orderIncreaseCount") && orderIncreaseCount > 0, "orderIncreaseCount should match and > 0");
        Assert.assertEquals(orderDecreaseCount + orderLiquidateCount, getIntValue(position, "orderDecreaseCount"), "orderDecreaseCount should match");

        // If status == CLOSE
        if ("CLOSE".equals(position.get("status"))) {
            double realisedPnl = calc.realisedPnl;
            double pnl = calc.pnl;
            double realisedRoi = calc.realisedRoi;
            double roi = calc.roi;
            Assert.assertTrue(Math.abs(realisedPnl - getDoubleValue(position, "realisedPnl")) <= Math.abs(realisedPnl * 0.1), "realisedPnl close to position.realisedPnl");
            Assert.assertTrue(Math.abs(pnl - getDoubleValue(position, "pnl")) <= Math.abs(pnl * 0.1), "pnl close to position.pnl");
            Assert.assertTrue(Math.abs(round2(realisedRoi) - getDoubleValue(position, "realisedRoi")) <= Math.abs(realisedRoi * 0.1), "realisedRoi close to position.realisedRoi");
            Assert.assertTrue(Math.abs(roi - getDoubleValue(position, "roi")) <= Math.abs(roi * 0.1), "roi close to position.roi");

            // Last type order
            Map<String, Object> lastOrder = orders.get(orders.size() - 1);
            String expectedLastOrderType = Boolean.TRUE.equals(position.get("isLiquidate")) ? "LIQUIDATE" : "CLOSE";
            Assert.assertEquals(lastOrder.get("type"), expectedLastOrderType, "Last order type should match");
            Assert.assertEquals(lastOrder.get("isClose"), true, "Last order isClose should be true");
        }

        // Fee & Funding
        double fee = calc.fee;
        double funding = calc.funding;
        Assert.assertTrue(Math.abs(fee - getDoubleValue(position, "fee")) <= Math.abs(fee * 0.01) && fee >= 0, "fee close to position.fee and >= 0");
        //Assert.assertTrue(Math.abs(funding - getDoubleValue(position, "funding")) <= Math.abs(funding * 0.01), "funding close to position.funding");
    }

    // Helper for int values
    private int getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        throw new IllegalArgumentException("Value for key '" + key + "' is not a Number");
    }

    // Helper for rounding
    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public PositionCalculationResult calculatePosition(List<Map<String, Object>> orders) {
        double collateral = 0, lastCollateral = 0, size = 0, lastSize = 0, avgPrice = 0;
        double fee = 0, funding = 0, realisedPnl = 0;
        int isOpenCount = 0, orderIncreaseCount = 0, orderMarginTransferCount = 0;
        int orderDecreaseCount = 0, orderCloseCount = 0, orderLiquidateCount = 0;

        for (Map<String, Object> order : orders) {
            validateOrders(order);

            boolean isOpen = Boolean.TRUE.equals(order.get("isOpen"));
            if (isOpen) isOpenCount++;

            String type = (String) order.get("type");
            double collateralDelta = getDoubleValue(order, "collateralDeltaNumber");
            double sizeDelta = getDoubleValue(order, "sizeDeltaNumber");
            double price = getDoubleValue(order, "priceNumber");
            double feeNumber = getDoubleValue(order, "feeNumber");
            double fundingNumber = order.get("fundingNumber") != null ? getDoubleValue(order, "fundingNumber") : 0;

            switch (type) {
                case "OPEN":
                case "INCREASE":
                    collateral += collateralDelta;
                    lastCollateral += collateralDelta;
                    size += sizeDelta;
                    lastSize += sizeDelta;
                    avgPrice = avgPrice == 0
                            ? price
                            : ((price * sizeDelta) + (avgPrice * (lastSize - sizeDelta))) / lastSize;
                    fee += feeNumber;
                    funding += fundingNumber;
                    orderIncreaseCount++;
                    break;
                case "MARGIN_TRANSFERRED":
                    if (collateralDelta > 0) {
                        collateral += collateralDelta;
                    }
                    lastCollateral += collateralDelta;
                    orderMarginTransferCount++;
                    break;
                case "DECREASE":
                case "CLOSE":
                case "LIQUIDATE":
                    lastCollateral -= collateralDelta;
                    lastSize -= sizeDelta;
                    realisedPnl += calculatePnlMultiplier(order, avgPrice) * sizeDelta;
                    fee += feeNumber;
                    funding += fundingNumber;
                    if ("DECREASE".equals(type)) orderDecreaseCount++;
                    else if ("CLOSE".equals(type)) orderCloseCount++;
                    else orderLiquidateCount++;
                    boolean isClose = Boolean.TRUE.equals(order.get("isClose")) || "CLOSE".equals(type) || "LIQUIDATE".equals(type);
                    if (isClose) break;
                    break;
            }
        }

        PositionCalculationResult result = new PositionCalculationResult();
        result.collateral = collateral;
        result.lastCollateral = lastCollateral;
        result.size = size;
        result.lastSize = lastSize;
        result.avgPrice = avgPrice;
        result.fee = fee;
        result.funding = funding;
        result.realisedPnl = realisedPnl;
        result.isOpenCount = isOpenCount;
        result.orderIncreaseCount = orderIncreaseCount;
        result.orderMarginTransferCount = orderMarginTransferCount;
        result.orderDecreaseCount = orderDecreaseCount;
        result.orderCloseCount = orderCloseCount;
        result.orderLiquidateCount = orderLiquidateCount;
        result.realisedRoi = collateral != 0 ? (realisedPnl / collateral) * 100 : 0;
        result.pnl = realisedPnl - fee + funding;
        result.roi = collateral != 0 ? (result.pnl / collateral) * 100 : 0;
        result.leverage = collateral != 0 ? size / collateral : 0;
        return result;
    }

    public static class PositionCalculationResult {
        public double collateral;
        public double lastCollateral;
        public double size;
        public double lastSize;
        public double avgPrice;
        public double fee;
        public double funding;
        public double realisedPnl;
        public int isOpenCount;
        public int orderIncreaseCount;
        public int orderMarginTransferCount;
        public int orderDecreaseCount;
        public int orderCloseCount;
        public int orderLiquidateCount;
        public double realisedRoi;
        public double pnl;
        public double roi;
        public double leverage;
    }

    public static double calculatePnlMultiplier(Map<String, Object> order, double avgPrice) {
        boolean isLong = Boolean.TRUE.equals(order.get("isLong"));
        double priceNumber = getDoubleValue(order, "priceNumber");
        if (avgPrice == 0) {
            throw new IllegalArgumentException("avgPrice must not be zero");
        }
        return isLong ? (priceNumber / avgPrice - 1) : (1 - priceNumber / avgPrice);
    }

    public static double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);

        // Null check
        if (value == null) {
            throw new IllegalArgumentException("Value for key '" + key + "' is null");
        }

        // Type check
        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("Value for key '" + key + "' is not a Number, got: " + value.getClass());
        }

        return ((Number) value).doubleValue();
    }
}
