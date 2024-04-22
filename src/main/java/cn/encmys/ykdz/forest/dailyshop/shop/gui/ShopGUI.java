package cn.encmys.ykdz.forest.dailyshop.shop.gui;

import cn.encmys.ykdz.forest.dailyshop.DailyShop;
import cn.encmys.ykdz.forest.dailyshop.api.product.Product;
import cn.encmys.ykdz.forest.dailyshop.builder.BaseItemDecorator;
import cn.encmys.ykdz.forest.dailyshop.config.ShopConfig;
import cn.encmys.ykdz.forest.dailyshop.product.factory.ProductFactory;
import cn.encmys.ykdz.forest.dailyshop.util.LogUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
        ScrollGui.Builder<Item> builder = ScrollGui.items()
                .setStructure(getSection().getStringList("layout").toArray(new String[0]));

        int scrollShift;
        if (getSection().getString("scroll-mode", "HORIZONTAL").equalsIgnoreCase("HORIZONTAL")) {
            builder.addIngredient(productIdentifier, Markers.CONTENT_LIST_SLOT_HORIZONTAL);
            scrollShift = getRowsWithProduct();
        } else {
            builder.addIngredient(productIdentifier, Markers.CONTENT_LIST_SLOT_VERTICAL);
            scrollShift = getColsWithProduct();
        }

        // Normal Icon
        ConfigurationSection iconsSection = getSection().getConfigurationSection("icons");
        for (String key : iconsSection.getKeys(false)) {
            char iconKey = key.charAt(0);
            ConfigurationSection icon = iconsSection.getConfigurationSection(String.valueOf(iconKey));
            String item = icon.getString("item", "DIRT");

            BaseItemDecorator iconBuilder = BaseItemDecorator.get(item, true);
            Item iconItem = null;

            if (iconBuilder == null) {
                LogUtils.warn("Icon " + iconKey + "in shop " + getShopId() + " has invalid base setting. Please check it.");
                return;
            } else {
                iconItem = iconBuilder
                        .setScrollShift(scrollShift)
                        .setAmount(icon.getInt("amount", 1))
                        .setName(icon.getString("name", null))
                        .setLore(icon.getStringList("lore"))
                        .setPeriod(icon.getLong("update-timer", 0L))
                        .setScroll(icon.getInt("scroll", 0))
                        .setCommands(new HashMap<>() {{
                            put(ClickType.LEFT, icon.getStringList("commands.left"));
                            put(ClickType.RIGHT, icon.getStringList("commands.right"));
                            put(ClickType.SHIFT_LEFT, icon.getStringList("commands.shift-left"));
                            put(ClickType.SHIFT_RIGHT, icon.getStringList("commands.shift-right"));
                            put(ClickType.DROP, icon.getStringList("commands.drop"));
                            put(ClickType.DOUBLE_CLICK, icon.getStringList("commands.double-click"));
                            put(ClickType.MIDDLE, icon.getStringList("commands.middle"));
                        }})
                        .setCustomModelData(icon.getInt("custom-model-data"))
                        .setItemFlags(icon.getStringList("item-flags"))
                        .setPatternsData(icon.getStringList("banner-patterns"))
                        .buildNormalIcon();
            }

            builder.addIngredient(iconKey, iconItem);
        }

        // Product Icon
        for (String productId : listedProduct) {
            Product product = productFactory.getProduct(productId);
            builder.addContent(product.getIconBuilder().buildProductIcon(getShopId(), product));
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

    public int getRowsWithProduct() {
        return (int) getSection().getStringList("layout").stream().filter(row -> row.contains("x")).count();
    }

    public int getColsWithProduct() {
        List<String> layout = getSection().getStringList("layout");
        int maxCols = layout.get(0).split(" ").length;
        int[] colCounts = new int[maxCols];

        for (String row : layout) {
            String[] cells = row.split(" ");
            for (int i = 0; i < cells.length; i++) {
                if (cells[i].equals("x")) {
                    colCounts[i]++;
                }
            }
        }

        int colCount = 0;
        for (int count : colCounts) {
            if (count > 0) {
                colCount++;
            }
        }
        return colCount;
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
