package cn.encmys.ykdz.forest.hyphashop.api.shop.order.enums;

public enum SettlementResult {
    INVALID_CUSTOMER("failure.customer"),
    DUPLICATED("failure.duplicated"),
    CANCELLED("failure.cancelled"),
    UNKNOWN("failure.unknown"),
    EMPTY("failure.empty"),
    TRANSITION_DISABLED("failure.disabled"),
    NOT_ENOUGH_MONEY("failure.money"),
    NOT_ENOUGH_PRODUCT("failure.product"),
    NOT_ENOUGH_INVENTORY_SPACE("failure.inventory-space"),
    NOT_ENOUGH_PLAYER_STOCK("failure.player-stock"),
    NOT_ENOUGH_GLOBAL_STOCK("failure.global-stock"),
    NOT_ENOUGH_MERCHANT_BALANCE("failure.merchant-balance"),
    NOT_LISTED("failure.not-listed"),
    PARTIAL_SUCCESS("partial-success"),
    SUCCESS("success");

    private final String configKey;

    SettlementResult(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }
}
