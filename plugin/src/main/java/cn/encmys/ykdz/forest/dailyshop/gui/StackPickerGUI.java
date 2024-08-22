package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.StackPickerGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.StackPickerGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.PlayerRelatedGUI;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.GUIType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;

public class StackPickerGUI extends PlayerRelatedGUI {
    private final StackPickerGUIRecord guiRecord;
    private final ShopOrder order;
    private final String targetProductId;
    private int stack;

    public StackPickerGUI(Player player, ShopOrder order, String targetProductId, StackPickerGUIRecord guiRecord) {
        super(player);
        this.guiRecord = guiRecord;
        this.order = order;
        this.targetProductId = targetProductId;
        this.stack = order.getOrderedProducts().get(targetProductId);
    }

    @Override
    public Gui build(Player player) {
        Gui.Builder.Normal guiBuilder = Gui.normal()
                .setStructure(guiRecord.layout().toArray(new String[0]));

        if (guiRecord.icons() == null) {
            return guiBuilder.build();
        }

        for (IconRecord icon : guiRecord.icons()) {
            guiBuilder.addIngredient(icon.key(), NormalIconBuilder.build(icon, null, this, player));
        }

        return guiBuilder.build();
    }

    @Override
    protected Gui buildScrollGUI(Player player) {
        return null;
    }

    @Override
    protected Gui buildPagedGUI(Player player) {
        return null;
    }

    @Override
    public void loadContent(@Nullable Player player) {
    }

    @Override
    public void open() {
        StackPickerGUIRecord guiRecord = StackPickerGUIConfig.getGUIRecord();
        Window window = AnvilWindow.single()
                .setGui(build(player))
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

        DailyShop.PROFILE_FACTORY.getProfile(player).setViewingGuiType(GUIType.STACK_PICKER);

        getWindows().put(player.getUniqueId(), window);
        window.open();
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
