package org.brokenarrow.lootboxes.menus.itemcreation;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.button.logic.ButtonUpdateAction;
import org.broken.arrow.menu.library.button.logic.FillMenuButton;
import org.broken.arrow.menu.library.holder.MenuHolderPage;
import org.broken.arrow.menu.library.utility.ServerVersion;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.CustomizeItem;
import org.brokenarrow.lootboxes.menus.loottable.EditCreateLootTable;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.getPlaceholders;

public class EditCreateItems extends MenuHolderPage<String> {
    private final LootItems lootItems = LootItems.getInstance();
    private final org.brokenarrow.lootboxes.lootdata.ItemData itemData = org.brokenarrow.lootboxes.lootdata.ItemData.getInstance();
    private final MenuTemplate guiTemplate;
    private final String lootTable;

    public EditCreateItems(final String lootTable) {
        super(LootItems.getInstance().getItems(lootTable));
        this.lootTable = lootTable;
        this.guiTemplate = Lootboxes.getInstance().getMenu("Edit_items_for_loot_table");

        setUseColorConversion(true);
        setIgnoreItemCheck(true);

        if (guiTemplate != null) {
            setFillSpace(guiTemplate.getFillSlots());
            setMenuSize(guiTemplate.getinvSize("Edit_items_for_loot_table"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), ""));
            setMenuOpenSound(guiTemplate.getSound());
            this.setUseColorConversion(true);
        } else {
            setMenuSize(36);
            setTitle(() -> "could not load menu 'Edit_items_for_loot_table'.");
        }
    }

    @Override
    public MenuButton getButtonAt(int slot) {
        MenuButtonData button = this.guiTemplate.getMenuButton(slot);
        if (button == null) return null;
        return new MenuButton() {
            @Override
            public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem) {
                if (run(button, click))
                    updateButton(this);
            }

            @Override
            public ItemStack getItem() {
                org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

                return CreateItemUtily.of(menuButton.isGlow(), menuButton.getMaterial(),
                                TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName()),
                                TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
                        .makeItemStack();
            }
        };
    }

    public boolean run(MenuButtonData button, ClickType click) {

        if (button.isActionTypeEqual("Save_items")) {
            new SaveItems(lootTable).menuOpen(player);
        }
        if (button.isActionTypeEqual("Forward_button")) {
            if (click.isLeftClick()) {
                nextPage();
            }
        }
        if (button.isActionTypeEqual("Previous_button")) {
            if (click.isLeftClick()) {
                previousPage();
            }
        }
        if (button.isActionTypeEqual("Search")) {
        }

        if (button.isActionTypeEqual("Back_button")) {
            new EditCreateLootTable().menuOpen(player);
        }
        return false;
    }

    @Override
    public FillMenuButton<String> createFillMenuButton() {
        MenuButtonData button = this.guiTemplate.getMenuButton(-1);
        if (button == null) return null;

        return new FillMenuButton<>((player, menu, click, clickedItem, itemPathKey) -> {
            if (itemPathKey != null) {
                final LootData data = lootItems.getCachedTableContents(lootTable).get(itemPathKey);
                //final ItemData itemData = cacheItemData.get(new GuiItem(clickedItem));
                if (data != null) {
                    if (click.isRightClick()) {
                        final ItemStack itemStack;
                        if (data.isHaveMetadata()) {
                            itemStack = itemData.getCacheItemData(lootTable, data.getItemDataPath());
                        } else {
                            if (data.getMaterial() == null) return ButtonUpdateAction.NONE;
                            itemStack = new ItemStack(data.getMaterial());
                        }
                        if (itemStack != null)
                            player.getInventory().addItem(itemStack);
                    }
                    else
                        new CustomizeItem(lootTable, itemPathKey).menuOpen(player);
                } else {
                    System.out.println("could not retrieve the item set.");
                }
            }
            return ButtonUpdateAction.NONE;
        }, (slot, itemPathKey) -> {
            org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

            if (itemPathKey != null) {
                final LootData data = lootItems.getCachedTableContents(lootTable).get(itemPathKey);
                if (data != null) {
                    final ItemStack itemStack;
                    if (data.isHaveMetadata()) {
                        itemStack = itemData.getCacheItemData(lootTable, data.getItemDataPath());
                    } else {
                        if (data.getMaterial() == null) return null;
                        itemStack = new ItemStack(data.getMaterial());
                    }
                    if (itemStack == null) return null;
                    final ItemStack clonedItem = itemStack.clone();

                    Object[] placeholders = getPlaceholders("",
                            data.isHaveMetadata() && clonedItem.hasItemMeta() && clonedItem.getItemMeta().hasDisplayName() ? clonedItem.getItemMeta().getDisplayName() : bountifyCapitalized(clonedItem.getType()),
                            data.getChance(),
                            data.getMinimum(),
                            data.getMaximum(),
                            data.isHaveMetadata());
                    String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), placeholders);
                    final ItemMeta itemMeta = clonedItem.getItemMeta();
                    clonedItem.setItemMeta(itemMeta);

                    final ItemStack guiItem = CreateItemUtily.of(false, clonedItem,
                                    displayName,
                                    TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), placeholders))
                            .setShowEnchantments(true).makeItemStack();
                    return guiItem;
                }
            }
            return null;
        });
    }

    public static class ItemData {
        private final ItemStack itemStack;
        private final String itemPathKey;

        public ItemData(final ItemStack itemStack, final String itemPathKey) {
            this.itemStack = itemStack;
            this.itemPathKey = itemPathKey;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public String getItemPathKey() {
            return itemPathKey;
        }
    }

    public static class GuiItem {

        ItemStack itemStack;

        public GuiItem(ItemStack itemStack) {
            this.itemStack = itemStack;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GuiItem guiItem = (GuiItem) o;
            ItemStack item = guiItem.itemStack;
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                ItemMeta metaObject = item.getItemMeta();
                if (metaObject == null)
                    return false;
                if (!itemMeta.getDisplayName().equals(metaObject.getDisplayName()))
                    return false;
                if (itemMeta.getLore() != null && !itemMeta.getLore().equals(metaObject.getLore()))
                    return false;
                if (ServerVersion.atLeast(ServerVersion.V1_14)) {
                    if (itemMeta.hasCustomModelData()) {
                        if (!metaObject.hasCustomModelData())
                            return false;
                        if (itemMeta.getCustomModelData() != metaObject.getCustomModelData()) {
                            return false;
                        }
                    }
                    if (itemMeta instanceof Damageable) {
                        if (!(metaObject instanceof Damageable))
                            return false;
                        if (((Damageable) itemMeta).getDamage() != ((Damageable) metaObject).getDamage())
                            return false;
                    }
                } else {
                    if (itemStack.getDurability() != item.getDurability())
                        return false;
                }
            }
            return itemStack.getType() == item.getType();
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + itemStack.hashCode();
            return  itemStack.hashCode();
        }

        @Override
        public String toString() {
           ItemStack itemStack =  this.itemStack;
            ItemMeta meta = itemStack.getItemMeta();
            return "GuiItem{" +
                    "Type=" + itemStack.getType() +"\n"+
                    "DisplayName" + (meta != null ? meta.getDisplayName() : "") +"\n"+
                    "lore" + (meta != null && meta.getLore() != null ? meta.getLore().toString() : "") +"\n"+
                    '}';
        }
    }
}
