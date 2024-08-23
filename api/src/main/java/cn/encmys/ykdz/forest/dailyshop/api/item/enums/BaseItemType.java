package cn.encmys.ykdz.forest.dailyshop.api.item.enums;

public enum BaseItemType {
    VANILLA(true),
    MMOITEMS(false),
    ITEMS_ADDER(true),
    MYTHIC_MOBS(true);

    private final boolean asyncBuildable;

    BaseItemType(boolean asyncBuildable) {
        this.asyncBuildable = asyncBuildable;
    }

    public boolean isAsyncBuildable() {
        return asyncBuildable;
    }
}
