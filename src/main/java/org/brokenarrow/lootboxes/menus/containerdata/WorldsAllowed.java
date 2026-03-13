package org.brokenarrow.lootboxes.menus.containerdata;

import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.commandprompt.AddWorldPrompt;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.getPlaceholders;

public class WorldsAllowed extends MenuHolderPage<String> {
    private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
    private final MenuTemplate guiTemplate;
    private final String containerDataName;
    private ContainerDataBuilder containerDataBuilder;

    public WorldsAllowed(String containerDataName) {
        super(getWorlds(containerDataName));
        this.containerDataName = containerDataName;
        this.guiTemplate = Lootboxes.getInstance().getMenu("Worlds_Allowed");
        this.containerDataBuilder = ContainerDataCache.getInstance().getCacheContainerData(containerDataName);

        setUseColorConversion(true);
        setIgnoreItemCheck(true);
        if (guiTemplate != null) {
            setMenuSize(guiTemplate.getinvSize("Worlds_Allowed"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), ""));
            setMenuOpenSound(guiTemplate.getSound());
            setFillSpace(guiTemplate.getFillSlots());
            this.setUseColorConversion(true);
        } else {
            setMenuSize(36);
            setTitle(() -> "could not load menu 'Worlds_Allowed'.");
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

                return CreateItemUtily.of(menuButton.isGlow() && !menuButton.getMaterial().equalsIgnoreCase("chest"), menuButton.getMaterial(),
                                TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName()),
                                TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
                        .makeItemStack();
            }
        };
    }

    public boolean run(MenuButtonData button, ClickType click) {

        if (button.isActionTypeEqual("Select_world")) {
            new SelectWorld(containerDataName).menuOpen(player);
        }

        if (button.isActionTypeEqual("Type_world_name")) {
            new AddWorldPrompt(containerDataName, this.containerDataBuilder).start(player);
        }

        if (button.isActionTypeEqual("Back_button")) {
            new SettingsContainerData(containerDataName).menuOpen(player);
        }

        return false;
    }

    @Override
    public FillMenuButton<String> createFillMenuButton() {
        return new FillMenuButton<>((player1, menu, click, clickedItem, worldName) -> {
            final ContainerDataBuilder.Builder builder = this.containerDataBuilder.getBuilder();
            if (worldName == null) return ButtonUpdateAction.NONE;
            if (click.isLeftClick()) {
            } else {
                builder.removeWorld(worldName);
            }

            ContainerDataBuilder build = builder.build();
            containerDataCache.setContainerData(containerDataName, build);
            if (build.isSpawningContainerWithCooldown()) {
                containerDataCache.addContainerToSpawnTask(this.containerDataName, build.getCooldown());
            }
            this.containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);

            return ButtonUpdateAction.ALL;
        }, (slot, worldName) -> {
            final MenuButtonData button = this.guiTemplate.getMenuButton(-1);
            if (button == null) return null;
            if (worldName == null) return null;

            org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();
            final Object[] placeholders = setPlaceholders(worldName, containerDataBuilder);
            final boolean hasWorldSet = containerDataBuilder.contains(worldName);
            if (hasWorldSet)
            return CreateItemUtily.of(menuButton.isGlow(), menuButton.getMaterial(),
                            TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), placeholders),
                            TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), placeholders))
                    .makeItemStack();
            return null;
        });
    }

    private static List<String> getWorlds(String containerDataName) {
        ContainerDataBuilder cacheContainerData = ContainerDataCache.getInstance().getCacheContainerData(containerDataName);
        if (cacheContainerData == null)
            return new ArrayList<>();
        return new ArrayList<>(cacheContainerData.getWorlds());
    }

    public Object[] setPlaceholders(String worldName, ContainerDataBuilder containerDataBuilder) {
        Object[] placeholders = getPlaceholders(worldName != null ? worldName : "not exist or not loaded");
        return placeholders;
    }
}


