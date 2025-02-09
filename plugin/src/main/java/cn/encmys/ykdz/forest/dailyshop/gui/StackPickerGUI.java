package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.api.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.config.StackPickerGUIConfig;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.gui.StackPickerGUIRecord;
import cn.encmys.ykdz.forest.dailyshop.api.config.record.misc.IconRecord;
import cn.encmys.ykdz.forest.dailyshop.api.gui.GUI;
import cn.encmys.ykdz.forest.dailyshop.api.profile.enums.GUIType;
import cn.encmys.ykdz.forest.dailyshop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.dailyshop.item.builder.NormalIconBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.IngredientPreset;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public class StackPickerGUI extends GUI {
    @NotNull
    private final StackPickerGUIRecord guiRecord;
    @NotNull
    private final ShopOrder order;
    @NotNull
    private final String targetProductId;
    private int stack;
    @NotNull
    private final IngredientPreset iconPreset = buildIconPreset();

    public StackPickerGUI(@NotNull ShopOrder order, @NotNull String targetProductId, @NotNull StackPickerGUIRecord guiRecord) {
        this.guiRecord = guiRecord;
        this.order = order;
        this.targetProductId = targetProductId;
        this.stack = order.getOrderedProducts().get(targetProductId);
    }

    protected @NotNull IngredientPreset buildIconPreset() {
        IngredientPreset.Builder builder = IngredientPreset.builder();
        Stream.ofNullable(guiRecord.icons())
                .flatMap(Collection::stream)
                .forEach(iconRecord ->
                        builder.addIngredient(
                                iconRecord.key(),
                                NormalIconBuilder.build(iconRecord, null, null, null)
                        )
                );
        return builder.build();
    }

    public Gui build(Player player) {
        return Gui.normal()
                .setStructure(guiRecord.layout().toArray(new String[0]))
                .applyPreset(iconPreset)
                .build();
    }

    @Override
    public void open(@NotNull Player player) {
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
                    });
                }})
                .build(player);

//        DailyShop.PROFILE_FACTORY.getProfile(player).setViewingGuiType(GUIType.STACK_PICKER);

        windows.put(player.getUniqueId(), window);
        window.open();
    }

//    @Override
//    public void close() {
//        windows.remove(player.getUniqueId());
//        // https://github.com/NichtStudioCode/InvUI/discussions/85
//        DailyShop.INSTANCE.getServer().getScheduler().runTaskLater(DailyShop.INSTANCE, () -> DailyShop.PROFILE_FACTORY.getProfile(player).getCartGUI().open(), 1);
//    }

    public int getStack() {
        return stack;
    }
}
