package cn.encmys.ykdz.forest.dailyshop.api.item.decorator;

import cn.encmys.ykdz.forest.dailyshop.api.item.BaseItem;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.enums.PropertyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class BaseItemDecorator {
    @NotNull
    protected BaseItem baseItem;
    @NotNull
    protected final Map<PropertyType, Object> properties = new EnumMap<>(PropertyType.class);

    public BaseItemDecorator(@NotNull BaseItem baseItem) {
        this.baseItem = baseItem;
    }

    public @NotNull BaseItemDecorator setProperty(@NotNull PropertyType type, @Nullable Object value) {
        properties.put(type, value);
        return this;
    }

    public <T> @Nullable T getProperty(@NotNull PropertyType type) {
        Object value = properties.get(type);
        if (value == null) return null;
        else if (type.getToken().getRawType().isInstance(value)) {
            return (T) type.getToken().getRawType().cast(value);
        }
        throw new IllegalArgumentException("Invalid type for config key: " + type);
    }

    public @NotNull BaseItem getBaseItem() {
        return baseItem;
    }
}