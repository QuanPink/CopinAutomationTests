package asia.decentralab.copin.data.enumdata;

import lombok.Getter;

@Getter
public enum StatisticValue {
    RUNTIME_ALL("Runtime (All)"),
    LAST_TRADE("Last Trade"),
    MARKETS("Markets"),
    PNL("PnL"),
    TOTAL_GAIN("Total Gain"),
    TOTAL_LOSS("Total Loss"),
    TOTAL_PAID_FEES("Total Paid Fees"),
    TOTAL_VOLUME("Total Volume"),
    AVG_VOLUME("Avg Volume"),
    AVG_ROI("Avg ROI"),
    MAX_ROI("Max ROI"),
    TRADES("Trades"),
    WINS("Wins"),
    LOSES("Loses"),
    LIQUIDATIONS("Liquidations"),
    WIN_RATE("Win Rate"),
    PROFIT_RATE("Profit Rate"),
    L_S_RATE("L/S Rate"),
    ORDER_POS_RATIO("Order/Pos Ratio"),
    PNL_RATIO("PnL Ratio"),
    PROFIT_FACTOR("Profit Factor"),
    AVG_LEVERAGE("Avg Leverage"),
    MAX_LEVERAGE("Max Leverage"),
    MIN_LEVERAGE("Min Leverage"),
    AVG_DURATION("Avg Duration"),
    MAX_DURATION("Max Duration"),
    MIN_DURATION("Min Duration"),
    MAX_DRAW_DOWN("Max Drawdown"),
    MAX_DRAW_DOWN_PNL("Max Drawdown PnL"),
    IGNORE("");

    private final String value;

    StatisticValue(String value) {
        this.value = value;
    }
}
