package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.StackPickerGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.StackPickerGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.shop.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.PlayerRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.item.decorator.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.api.utils.LogUtils;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.dailyshop.item.decorator.BaseItemDecoratorImpl;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;

public class StackPickerGUI extends PlayerRelatedGUI {
    private final ShopOrder order;
    private final String targetProductId;
    private int stack;

    public StackPickerGUI(Player player, ShopOrder order, String targetProductId) {
        super(player);
        this.order = order;
        this.targetProductId = targetProductId;
        this.stack = order.getOrderedProducts().get(targetProductId);
    }

    @Override
    public Item buildNormalIcon(IconRecord record, Player player) {
        BaseItemDecorator decorator = BaseItemDecoratorImpl.get(record, true);
        if (decorator == null) {
            LogUtils.warn("Icon stack-picker.icons." + record + " in cart gui has invalid base setting. Please check it.");
            return null;
        }
        return NormalIconBuilder.build(decorator, null, player);
    }

    @Override
    public Gui buildGUI(Player player) {
        StackPickerGUIRecord guiRecord = StackPickerGUIConfig.getGUIRecord();

        Gui.Builder.Normal guiBuilder = Gui.normal()
                .setStructure(guiRecord.layout().toArray(new String[0]));

        if (guiRecord.icons() == null) {
            return guiBuilder.build();
        }

        for (IconRecord icon : guiRecord.icons()) {
            guiBuilder.addIngredient(icon.key(), buildNormalIcon(icon, player));
        }

        return guiBuilder.build();
    }

    @Override
    public void open() {
        StackPickerGUIRecord guiRecord = StackPickerGUIConfig.getGUIRecord();
        Window window = AnvilWindow.single()
                .setGui(buildGUI(player))
                .setTitle(guiRecord.title())
                .addRenameHandler((input) -> {
                    try {
                        int stack = Integer.parseInt(input);
                        if (stack < 0) {
                            return;
                        }
                        this.stack = stack;
                    } catch (NumberFormatException ignored) {
                    }
                })
                .setCloseHandlers(new ArrayList<>() {{
                    add(() -> {
                        if (stack < 0) {
                            return;
                        }
                        order.setStack(targetProductId, stack);
                        close();
                    });
                }})
                .build(player);

        window.open();

        getWindows().put(player.getUniqueId(), window);
    }

    @Override
    public void close() {
        windows.remove(player.getUniqueId());
        // https://github.com/NichtStudioCode/InvUI/discussions/85
        DailyShop.INSTANCE.getServer().getScheduler().runTaskLater(DailyShop.INSTANCE, () -> DailyShop.PROFILE_FACTORY.getProfile(player).getCartGUI().open(), 1);
    }

    public ShopOrder getOrder() {
        return order;
    }

    public String getTargetProductId() {
        return targetProductId;
    }

    public int getStack() {
        return stack;
    }
}
