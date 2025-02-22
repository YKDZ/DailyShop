package cn.encmys.ykdz.forest.hyphashop.gui;

import cn.encmys.ykdz.forest.hyphascript.context.Context;
import cn.encmys.ykdz.forest.hyphashop.api.HyphaShop;
import cn.encmys.ykdz.forest.hyphashop.api.gui.GUI;
import cn.encmys.ykdz.forest.hyphashop.api.shop.order.ShopOrder;
import cn.encmys.ykdz.forest.hyphashop.config.record.gui.StackPickerGUIRecord;
import cn.encmys.ykdz.forest.hyphashop.item.builder.NormalIconBuilder;
import cn.encmys.ykdz.forest.hyphashop.scheduler.Scheduler;
import cn.encmys.ykdz.forest.hyphashop.utils.ScriptUtils;
import cn.encmys.ykdz.forest.hyphashop.utils.VarUtils;
import cn.encmys.ykdz.forest.hyphautils.HyphaAdventureUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.IngredientPreset;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.*;
import java.util.stream.Stream;

public class StackPickerGUI extends GUI {
    @NotNull
    protected final static Map<UUID, Window> windows = new HashMap<>();
    @NotNull
    private final StackPickerGUIRecord record;
    @NotNull
    private final ShopOrder order;
    @NotNull
    private final String targetProductId;
    private int stack;

    public StackPickerGUI(@NotNull ShopOrder order, @NotNull String targetProductId, @NotNull StackPickerGUIRecord record) {
        this.record = record;
        this.order = order;
        this.targetProductId = targetProductId;
        this.stack = order.getOrderedProducts().get(targetProductId);
    }

    protected IngredientPreset buildIconPreset() {
        IngredientPreset.Builder builder = IngredientPreset.builder();
        Stream.ofNullable(record.icons())
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(entry ->
                        builder.addIngredient(
                                entry.getKey(),
                                NormalIconBuilder.build(entry.getValue(), null)
                        )
                );
        return builder.build();
    }

    public Gui build(@NotNull Player player) {
        return Gui.normal()
                .setStructure(record.layout().toArray(new String[0]))
                .applyPreset(buildIconPreset())
                .build();
    }

    @Override
    public void open(@NotNull Player player) {
        Window window = AnvilWindow.single()
                .setGui(build(player))
                .setTitleSupplier(() -> {
                    Context ctx = ScriptUtils.buildContext(
                            Context.GLOBAL_CONTEXT,
                            VarUtils.extractVars(player, null)
                    );
                    return HyphaAdventureUtils.getComponentFromMiniMessage(ScriptUtils.evaluateString(ctx, record.title()));
                })
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
                        // https://github.com/NichtStudioCode/InvUI/discussions/85
                        HyphaShop.INSTANCE.getServer().getScheduler().runTaskLater(HyphaShop.INSTANCE, () -> HyphaShop.PROFILE_FACTORY.getProfile(player).getCartGUI().open(player), 1);
                    });
                }})
                .build(player);

        window.open();

        // GUI 开启的一瞬间也是一次刷新标题
        // 故加上与间隔等长的 delay
        Scheduler.runAsyncTaskAtFixedRate((task) -> {
            if (!window.isOpen()) {
                task.cancel();
                return;
            }
            window.updateTitle();
        }, record.timeUpdatePeriod(), record.timeUpdatePeriod());

//        HyphaShop.PROFILE_FACTORY.getProfile(player).setViewingGuiType(GUIType.STACK_PICKER);

        windows.put(player.getUniqueId(), window);
    }

    @Override
    public void closeAll() {
        windows.forEach((uuid, window) -> window.close());
        windows.clear();
    }

    @Override
    public void close(@NotNull Player player) {
        windows.remove(player.getUniqueId());
    }

    public int getStack() {
        return stack;
    }
}
