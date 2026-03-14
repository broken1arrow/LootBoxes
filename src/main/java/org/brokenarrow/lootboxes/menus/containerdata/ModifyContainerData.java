package org.brokenarrow.lootboxes.menus.containerdata;

import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.commandprompt.CreateContainerDataName;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.menus.MainMenu;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModifyContainerData extends MenuHolderPage<String> {
    private final KeyDropData keyDropData = KeyDropData.getInstance();
    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();

    private final MenuTemplate guiTemplate;

    public ModifyContainerData() {
        super(Lootboxes.getInstance().getContainerDataCache().getContainerData());
        this.guiTemplate = Lootboxes.getInstance().getMenu("Containers_list");

        setUseColorConversion(true);
        setIgnoreItemCheck(true);

        if (guiTemplate != null) {
            setFillSpace(guiTemplate.getFillSlots());
            setMenuSize(guiTemplate.getinvSize("Containers_list"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(player, guiTemplate.getMenuTitle(), ""));
            setMenuOpenSound(guiTemplate.getSound());
            this.setUseColorConversion(true);
        } else {
            setMenuSize(36);
            setTitle(() -> "could not load menu 'Containers_list'.");
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
                org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();

                return CreateItemUtily.of(menuButton.isGlow(), menuButton.getMaterial(),
                                TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName()),
                                TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
                        .makeItemStack();
            }
        };
    }

    public boolean run(MenuButtonData button, ClickType click) {
        if (button.isActionTypeEqual("Create_container_data")) {
            new CreateContainerDataName(Material.AIR).start(player);
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
            new MainMenu().menuOpen(player);
        }
        return false;
    }

    @Override
    public FillMenuButton<String> createFillMenuButton() {
        MenuButtonData button = this.guiTemplate.getMenuButton(-1);
        if (button == null) return null;

        return new FillMenuButton<>((player, menu, click, clickedItem, containerKeyName) -> {

            if (containerKeyName != null) {
                if (click.isLeftClick())
                    new AlterContainerDataMenu(containerKeyName).menuOpen(player);
                if (click.isRightClick()) {
                    containerDataCache.removeCacheContainerData(containerKeyName);
                    keyDropData.removeKey(containerKeyName);
                    return ButtonUpdateAction.ALL;
                }
            }
            return ButtonUpdateAction.NONE;
        }, (slot, containerKeyName) -> {
            org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();
            if (containerKeyName != null) {
                final LootContainerData data = containerDataCache.getCacheContainerData(containerKeyName);
                if (data != null) {
                    String tableLinked = data.getLootTableLinked();
                    final String tableLink = tableLinked == null || tableLinked.isEmpty() ? "non" : tableLinked;
                    String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), containerKeyName, tableLink, data.getCooldown(), data.getIcon());

                    Material itemStack = null;
                    if (data.getIcon() == null || data.getIcon() == Material.AIR)
                        itemStack = Material.CHEST;
                    else
                        itemStack = data.getIcon();

                    return CreateItemUtily.of(false, itemStack,
                                    displayName,
                                    TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), containerKeyName, tableLink, data.getCooldown(), data.getIcon(),
                                            Lootboxes.getInstance().getSpawnLootContainer().haveCachedContainer(containerKeyName) ? "Active" : "Disable"))
                            .makeItemStack();
                }
            }
            return null;
        });
    }

}
