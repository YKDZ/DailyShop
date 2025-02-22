package cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums;

public enum OrderType {
    BUY_FROM("buy-from"),
    BUY_ALL_FROM("buy-all-from"),
    SELL_TO("sell-to");

    private final String configKey;

    OrderType(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }
}
