package asia.decentralab.copin.data.enumdata;

public enum UserDropdownItem {
    COPY_YOUR_ADDRESS("Copy Your Address"),
    TRADER_FAVORITES("Trader Favorites"),
    COPY_MANAGEMENT("Copy Management"),
    COPY_POSITION("Copy Position"),
    ACTIVITY_LOGS("Activity Logs"),
    WALLET_MANAGEMENT("Wallet Management"),
    MY_SUBSCRIPTION("My Subscription"),
    ALERT_LIST("Alert List"),
    REFERRAL("Referral"),
    LOGOUT("Logout");

    private String menuText;

    UserDropdownItem(String menuText) {
        this.menuText = menuText;
    }

    public String getValue() {
        return menuText;
    }
}
