package asia.decentralab.copin.utils.calculators;

import asia.decentralab.copin.models.PositionCalculationResult;
import asia.decentralab.copin.utils.MapUtils;

import java.util.List;
import java.util.Map;

public class PositionCalculator {

    public static PositionCalculationResult calculatePosition(List<Map<String, Object>> orders) {
        PositionCalculationResult result = new PositionCalculationResult();

        for (Map<String, Object> order : orders) {
            boolean shouldStop = processOrder(order, result);
            if (shouldStop) {
                break;
            }
        }

        // Calculate derived fields
        result.realisedRoi = result.collateral != 0 ? (result.realisedPnl / result.collateral) * 100 : 0;
        result.pnl = result.realisedPnl - result.fee + result.funding;
        result.roi = result.collateral != 0 ? (result.pnl / result.collateral) * 100 : 0;
        result.leverage = result.collateral != 0 ? result.size / result.collateral : 0;
        result.orderCount = result.orderIncreaseCount + result.orderDecreaseCount + result.orderMarginTransferCount + result.orderLiquidateCount;

        return result;
    }

    private static boolean processOrder(Map<String, Object> order, PositionCalculationResult result) {
        boolean isOpen = Boolean.TRUE.equals(order.get("isOpen"));
        boolean isClose = Boolean.TRUE.equals(order.get("isClose"));
        if (isOpen) result.isOpenCount++;

        String type = (String) order.get("type");
        double collateralDelta = MapUtils.getDouble(order, "collateralDeltaNumber");
        double sizeDelta = MapUtils.getDouble(order, "sizeDeltaNumber");
        double price = MapUtils.getDouble(order, "priceNumber");
        double feeNumber = MapUtils.getDouble(order, "feeNumber");
        double fundingNumber = order.get("fundingNumber") != null ?
                MapUtils.getDouble(order, "fundingNumber") : 0;

        switch (type) {
            case "OPEN":
            case "INCREASE":
                result.collateral += collateralDelta;
                result.lastCollateral += collateralDelta;
                result.size += sizeDelta;
                result.lastSize += sizeDelta;

                result.avgPrice = result.avgPrice == 0 ? price :
                        ((price * sizeDelta) + (result.avgPrice * (result.lastSize - sizeDelta))) / result.lastSize;

                result.fee += feeNumber;
                result.funding += fundingNumber;
                result.orderIncreaseCount++;
                break;
            case "MARGIN_TRANSFERRED":
                if (collateralDelta > 0) {
                    result.collateral += collateralDelta;
                }
                result.orderMarginTransferCount++;
                break;
            case "DECREASE":
            case "CLOSE":
            case "LIQUIDATE":
                result.lastCollateral -= collateralDelta;
                result.lastSize -= sizeDelta;
                boolean isFinalOrder = type.equals("CLOSE") || type.equals("LIQUIDATE") || isClose;
                if (isFinalOrder) {
                    result.realisedPnl += calculatePnlMultiplier(order, result.avgPrice) * sizeDelta;
                    result.closePrice = price;
                } else {
                    result.totalDecreasePnl += calculatePnlMultiplier(order, result.avgPrice) * sizeDelta;
                }
                result.fee += feeNumber;
                result.funding += fundingNumber;
                result.orderDecreaseCount++;
                return isFinalOrder;
        }
        return false;
    }

    public static double calculatePnlMultiplier(Map<String, Object> order, double avgPrice) {
        boolean isLong = Boolean.TRUE.equals(order.get("isLong"));
        double priceNumber = MapUtils.getDouble(order, "priceNumber");
        if (avgPrice == 0) {
            throw new IllegalArgumentException("avgPrice must not be zero");
        }
        return isLong ? (priceNumber / avgPrice - 1) : (1 - priceNumber / avgPrice);
    }
}