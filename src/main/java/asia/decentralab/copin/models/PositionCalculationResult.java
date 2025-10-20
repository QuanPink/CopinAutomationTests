package asia.decentralab.copin.models;

public class PositionCalculationResult {
    public int orderIncreaseCount;
    public int orderMarginTransferCount;
    public int orderDecreaseCount;
    public int orderLiquidateCount;
    public int orderCount;
    public  int isOpenCount;
    public double collateral;
    public double lastCollateral;
    public double size;
    public double lastSize;
    public double avgPrice;
    public double closePrice;
    public double fee;
    public double funding;
    public double realisedPnl;
    public double realisedRoi;
    public double pnl;
    public double roi;
    public double leverage;
    public double totalDecreasePnl;
}