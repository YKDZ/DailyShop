package cn.encmys.ykdz.forest.dailyshop.gui;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.IconBuilder;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.factory.ProductFactory;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.*;

public class ShopGUI {
    private static final ProductFactory productFactory = DailyShop.getProductFactory();
    private static final char productIdentifier = 'x';
    private final Map<UUID, Window> windows = new HashMap<>();
    private final String shopId;
    private final ConfigurationSection section;
    private Gui gui;

    public ShopGUI(String shopId, ConfigurationSection section) {
        this.shopId = shopId;
        this.section = section;
    }

    public void build(List<String> listedProduct) {
        ScrollGui.Builder<@NotNull Item> builder = ScrollGui.items()
                .setStructure(getSection().getStringList("layout").toArray(new String[0]));

        if (getSection().getString("scroll-mode", "HORIZONTAL").equalsIgnoreCase("HORIZONTAL")) {
            builder.addIngredient(productIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        } else {
            builder.addIngredient(productIdentifier, Markers.CONTENT_LIST_SLOT_VERTICAL);
        }

        // Normal Icon
        ConfigurationSection iconsSection = getSection().getConfigurationSection("icons");
        for (String key : iconsSection.getKeys(false)) {
            char iconKey = key.charAt(0);
            String item = iconsSection.getString(iconKey + ".item", "DIRT");

            builder.addIngredient(iconKey, IconBuilder.get(item)
                    .setAmount(iconsSection.getInt(iconKey + ".amount", 1))
                    .setName(iconsSection.getString(iconKey + ".name", null))
                    .setLore(iconsSection.getStringList(iconKey + ".lore"))
                    .setPeriod(iconsSection.getLong(iconKey + ".update-timer", 0L))
                    .setScroll(iconsSection.getInt(iconKey + ".scroll", 0))
                    .setCommands(new HashMap<>() {{
                        put(ClickType.LEFT, iconsSection.getStringList(iconKey + ".commands.left"));
                        put(ClickType.RIGHT, iconsSection.getStringList(iconKey + ".commands.right"));
                        put(ClickType.SHIFT_LEFT, iconsSection.getStringList(iconKey + ".commands.shift-left"));
                        put(ClickType.SHIFT_RIGHT, iconsSection.getStringList(iconKey + ".commands.shift-right"));
                        put(ClickType.DROP, iconsSection.getStringList(iconKey + ".commands.drop"));
                        put(ClickType.DOUBLE_CLICK, iconsSection.getStringList(iconKey + ".commands.double-click"));
                        put(ClickType.MIDDLE, iconsSection.getStringList(iconKey + ".commands.middle"));
                    }})
                    .setCustomModelData(iconsSection.getInt(iconKey + ".custom-model-data"))
                    .setItemFlags(iconsSection.getStringList(iconKey + ".item-flags"))
                    .build());
        }

        // Product Icon
        for (String productId : listedProduct) {
            Product product = productFactory.getProduct(productId);
            builder.addContent(product.getProductIconBuilder().build(getShopId(), product));
        }

        gui = builder.build();
    }

    public void open(Player player) {
        Window window = Window.single()
                .setGui(getGui())
                .setViewer(player)
                .setTitle(PlaceholderAPI.setPlaceholders(player, ShopConfig.getGUITitle(getShopId())))
                .build();

        window.setCloseHandlers(new ArrayList<>() {{
            add(() -> getWindows().remove(window));
        }});

        getWindows().put(player.getUniqueId(), window);
        window.open();
    }

    public void closeAll() {
        for (Window window : getWindows().values()) {
            window.close();
        }
        windows.clear();
    }

    public void close(Player player) {
        UUID uuid = player.getUniqueId();
        getWindows().get(uuid).close();
        getWindows().remove(uuid);
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public String getShopId() {
        return shopId;
    }

    public Map<UUID, Window> getWindows() {
        return windows;
    }

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    public Gui getGui() {
        return gui;
    }
}
