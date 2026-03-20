package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.library.menu.CheckItemsInsideMenu;
import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.menus.containerdata.ChooseRandomLootContainer;
import org.brokenarrow.lootboxes.settings.CustomLootContainersCache;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CreateNewContainerMenu extends MenuHolder {
    private final MenuTemplate guiTemplate;
    private final MenuKeys menuKey;
    private final String containerKey;

    public CreateNewContainerMenu(MenuKeys menuKey, final String containerKey) {
        this.menuKey = menuKey;
        this.containerKey = containerKey;
        this.guiTemplate = Lootboxes.getInstance().getMenu("Create_container");
        setUseColorConversion(true);
        setIgnoreItemCheck(true);

        if (guiTemplate != null) {
            setFillSpace(guiTemplate.getFillSlots());
            setMenuSize(guiTemplate.getinvSize("Create_container"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), ""));
            setMenuOpenSound(guiTemplate.getSound());
            this.setUseColorConversion(true);
        } else {
            setMenuSize(36);
            setTitle(() -> "could not load menu 'Create_container'.");
        }
        setSlotsYouCanAddItems(true);
    }

    @Override
    public MenuButton getButtonAt(int slot) {
        MenuButtonData button = this.guiTemplate.getMenuButton(slot);
        if (button == null) return null;
        return new MenuButton() {
            @Override
            public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem) {
                if (run(button, menu, click))
                    updateButton(this);
            }

            @Override
            public ItemStack getItem() {
                org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();

                return CreateItemUtily.of(menuButton.isGlow(), menuButton.getMaterial(),
                                TranslatePlaceHolders.getDisplayName(player, menuButton.getDisplayName()),
                                TranslatePlaceHolders.getLore(player, menuButton.getLore()))
                        .makeItemStack();
            }
        };
    }

    public boolean run(MenuButtonData button, Inventory menu, ClickType click) {

        if (button.isActionTypeEqual("Save_items")) {
            CustomLootContainersCache customLootContainer = Lootboxes.getInstance().getCustomLootContainersCache();
            CheckItemsInsideMenu insideInventory = Lootboxes.getInstance().getMenuApi().getCheckItemsInsideInventory();

            insideInventory.setSlotsToCheck(guiTemplate.getFillSlots());
            final Map<Integer, ItemStack> items = insideInventory.getItemsFromSetSlots(menu, null, false);
            if (items == null || items.isEmpty()) return false;
            for (final ItemStack item : items.values()) {
                if (item == null) continue;
                customLootContainer.addContainer(item);
            }
        }
        if (button.isActionTypeEqual("Back_button")) {
            if (menuKey == MenuKeys.CHOOSE_RANDOM_LOOT_CONTAINER)
                new ChooseRandomLootContainer(containerKey).menuOpen(player);
            if (menuKey == MenuKeys.CHOOSE_CONTAINER)
                new ChooseContainer(containerKey).menuOpen(player);
        }
        return false;
    }
}
