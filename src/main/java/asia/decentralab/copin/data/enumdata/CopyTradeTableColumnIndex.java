package asia.decentralab.copin.data.enumdata;

import lombok.Getter;

@Getter
public enum CopyTradeTableColumnIndex {
    RUN_COLUMN_INDEX(1),
    LABEL_COLUMN_INDEX(2),
    TRADER_ADDRESS_COLUMN_INDEX(3),
    MARGIN_ADDRESS_COLUMN_INDEX(4),
    LEVERAGE_ADDRESS_COLUMN_INDEX(5);

    private final int value;

    CopyTradeTableColumnIndex(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

