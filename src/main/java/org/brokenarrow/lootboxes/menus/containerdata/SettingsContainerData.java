package org.brokenarrow.lootboxes.menus.containerdata;

import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.CenterMode;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.commandprompt.SetPermission;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.BountifyStrings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.LocationWrapper;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.getPlaceholders;


public class SettingsContainerData extends MenuHolder {

    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
    private final Settings settings = Lootboxes.getInstance().getSettings();
    private final MenuTemplate guiTemplate;
    private final String containerKey;
    private LootContainerData lootContainerData;

    public SettingsContainerData(String containerKey) {
        this.guiTemplate = Lootboxes.getInstance().getMenu("Settings_container_data");
        this.containerKey = containerKey;
        this.lootContainerData = containerDataCache.getCacheContainerData(containerKey);

        setUseColorConversion(true);
        setIgnoreItemCheck(true);
        if (guiTemplate != null) {
            setMenuSize(guiTemplate.getinvSize("Settings_container_data"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), ""));
            setMenuOpenSound(guiTemplate.getSound());
            this.setUseColorConversion(true);
        } else {
            setMenuSize(36);
            setTitle(() -> "could not load menu 'Settings_container_data'.");
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
                    updateButtons();
            }

            @Override
            public ItemStack getItem() {
                return containerDataCache.read(containerKey, containerData -> {
                    org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();
                    Object[] placeholders = setPlaceholders(button, containerData);
                    menuButton = getActiveButton(button, containerData);

                    if (menuButton == null)
                        menuButton = button.getPassiveButton();

                    return CreateItemUtily.of(menuButton.isGlow(), menuButton.getMaterial(),
                                    TranslatePlaceHolders.getDisplayName(player, menuButton.getDisplayName(), placeholders),
                                    TranslatePlaceHolders.getLore(player, menuButton.getLore(), placeholders))
                            .makeItemStack();
                });
            }
        };
    }

    public boolean run(MenuButtonData button, ClickType click) {
        LootContainerData lootContainerData = this.lootContainerData;
        int decrease = settings.getSettingsData().getDecrease();
        int increase = settings.getSettingsData().getIncrease();
        return this.containerDataCache.write(containerKey, containerBuilder -> {
            if (lootContainerData != null) {
                boolean buttonMatch = false;
                switch (button.getActionType()) {
                    case "Container_type":
                        new ChooseRandomLootContainer(containerKey).menuOpen(player);
                        break;
                    case "Show_title":
                        containerBuilder.setShowTitle(click.isLeftClick());
                        buttonMatch = true;
                        break;
                    case "Glow":
                        containerBuilder.setContainerShallGlow(click.isLeftClick());
                        buttonMatch = true;
                        break;
                    case "Random_spawn":
                        containerBuilder.setRandomSpawn(click.isLeftClick());
                        buttonMatch = true;
                        break;
                    case "Loot_on_timer":
                        containerBuilder.setSpawningContainerWithCooldown(click.isLeftClick());
                        buttonMatch = true;
                        break;
                    case "Attempts":
                        int attempts = lootContainerData.getAttempts();

                        if (click.isRightClick())
                            attempts += 1;
                        if (click.isLeftClick())
                            attempts -= 1;
                        if (attempts < 1)
                            attempts = 1;
                        containerBuilder.setAttempts(attempts);
                        buttonMatch = true;
                        break;
                    case "Min_radius":
                        int minRadius = lootContainerData.getMinRadius();

                        if (click.isRightClick())
                            minRadius += click.isShiftClick() ? increase : 1;
                        if (click.isLeftClick())
                            minRadius -= click.isShiftClick() ? decrease : 1;
                        if (minRadius < 1)
                            minRadius = 0;
                        containerBuilder.setMinRadius(minRadius);
                        buttonMatch = true;
                        break;
                    case "Max_radius":
                        int maxRadius = lootContainerData.getMaxRadius();
                        if (click.isRightClick())
                            maxRadius += click.isShiftClick() ? increase : 1;
                        if (click.isLeftClick())
                            maxRadius -= click.isShiftClick() ? decrease : 1;
                        if (maxRadius < 1)
                            maxRadius = 0;
                        containerBuilder.setMaxRadius(maxRadius);
                        buttonMatch = true;
                        break;
                    case "Spawn_center":
                    case "Player_set_loc":
                    case "World_center":
                        containerBuilder.selectCenterMode(click);
                        buttonMatch = true;
                        break;
                    case "Spawn_On_Surface":
                        containerBuilder.setSpawnOnSurface(click.isLeftClick());
                        buttonMatch = true;
                        break;
                    case "Permission":
                        if (click.isLeftClick()) {
                            new SetPermission(containerKey).start(player);
                            return true;
                        }
                        buttonMatch = true;
                        containerBuilder.setPermissionForRandomSpawn("");
                        break;
                    case "Select_worlds":
                        new WorldsAllowed(containerKey).menuOpen(player);
                        return false;
                    case "Back_button":
                        new ModifyContainerData().menuOpen(player);
                        break;
                }
                if (buttonMatch) {
                    if (containerBuilder.isSpawningContainerWithCooldown()) {
                        containerDataCache.addContainerToSpawnTask(this.containerKey, containerBuilder.getCooldown());
                    }
                    if (containerBuilder.isRandomSpawn()) {
                        Lootboxes.getInstance().getSpawnLootContainer().setRandomSpawnedContainer();
                    }
                    if (player.getLocation().getWorld() != null) {
                        if (containerBuilder.getCenterMode() == CenterMode.WORLD_ORIGIN)
                            containerBuilder.setSpawnLocation(new LocationWrapper(player.getLocation().getWorld().getSpawnLocation(), false));
                        else if (containerBuilder.getCenterMode() == CenterMode.PLAYER_ORIGIN)
                            containerBuilder.setSpawnLocation(new LocationWrapper(player.getLocation(), false));
                    } else
                        containerBuilder.setSpawnLocation(null);

                    return true;
                }
            }
            if (button.isActionTypeEqual("Forward_button")) {
            }
            if (button.isActionTypeEqual("Previous_button")) {
            }
            if (button.isActionTypeEqual("Search")) {
            }

            if (button.isActionTypeEqual("Back_button")) {
                new AlterContainerDataMenu(containerKey).menuOpen(player);
            }
            return false;
        });
    }

    public org.broken.arrow.library.menu.button.manager.utility.MenuButton getActiveButton(MenuButtonData button, LootContainerData lootContainerData) {

        if (lootContainerData.isRandomSpawn()) {
            switch (button.getActionType()) {
                case "Random_spawn":
                case "Attempts":
                case "Min_radius":
                case "Max_radius":
                case "World_center":
                case "Player_set_loc":
                case "Spawn_center":
                case "Spawn_On_Surface":
                    return button.getActiveButton();
                case "Select_worlds":
                    CenterMode centerMode = lootContainerData.getCenterMode();
                    if (centerMode == CenterMode.PLAYER_FOLLOW)
                        return button.getActiveButton();
            }
        }
        return null;
    }

    public Object[] setPlaceholders(MenuButtonData button, LootContainerData lootContainerData) {
        Object[] placeholders = getPlaceholders("");
        if (button.isActionTypeEqual("Container_type")) {
            ContainerData randomLootData = lootContainerData.getRandomLootData();
            if (randomLootData == null) {
                placeholders = getPlaceholders("", "", "");
            } else {
                org.bukkit.inventory.ItemStack container = randomLootData.getContainer();
                String displayName = "";
                if (container != null) {
                    if (container.hasItemMeta()) {
                        ItemMeta itemMeta = container.getItemMeta();
                        if (itemMeta.hasDisplayName()) {
                            displayName = itemMeta.getDisplayName();
                        } else {
                            displayName = BountifyStrings.bountifyCapitalized(container.getType());
                        }
                    } else {
                        displayName = BountifyStrings.bountifyCapitalized(container.getType());
                    }
                }
                placeholders = getPlaceholders("", displayName,
                        BountifyStrings.bountifyCapitalized(randomLootData.getFacing()));
            }
        }

        if (button.isActionTypeEqual("Show_title"))
            placeholders = getPlaceholders("", lootContainerData.isShowTitle());

        if (button.isActionTypeEqual("Glow"))
            placeholders = getPlaceholders("", lootContainerData.isContainerShallGlow());

        if (button.isActionTypeEqual("Random_spawn"))
            placeholders = getPlaceholders(lootContainerData.isRandomSpawn());

        if (button.isActionTypeEqual("Loot_on_timer"))
            placeholders = getPlaceholders("", lootContainerData.isSpawningContainerWithCooldown());

        if (button.isActionTypeEqual("Attempts"))
            placeholders = getPlaceholders("", lootContainerData.getAttempts());

        if (button.isActionTypeEqual("Min_radius"))
            placeholders = getPlaceholders(lootContainerData.getMinRadius(), lootContainerData.getMinRadius());

        if (button.isActionTypeEqual("Max_radius"))
            placeholders = getPlaceholders("", lootContainerData.getMaxRadius());

        final CenterMode centerMode = lootContainerData.getCenterMode();
        if (button.isActionTypeEqual("Spawn_center")) {
            placeholders = getPlaceholders(BountifyStrings.bountifyCapitalized(centerMode), lootContainerData.getSpawnLocation() != null ? lootContainerData.getSpawnLocation().toString() : "", centerMode.getDescription());
        }
        if (button.isActionTypeEqual("World_center"))
            placeholders = getPlaceholders(BountifyStrings.bountifyCapitalized(centerMode), lootContainerData.getSpawnLocation() != null ? lootContainerData.getSpawnLocation().toString() : "");

        if (button.isActionTypeEqual("Player_set_loc"))
            placeholders = getPlaceholders(BountifyStrings.bountifyCapitalized(centerMode), lootContainerData.getSpawnLocation() != null ? lootContainerData.getSpawnLocation().toString() : "");

        if (button.isActionTypeEqual("Spawn_On_Surface"))
            placeholders = getPlaceholders(lootContainerData.isSpawnOnSurface());

        if (button.isActionTypeEqual("Permission"))
            placeholders = getPlaceholders(lootContainerData.getPermissionForRandomSpawn() != null ? lootContainerData.getPermissionForRandomSpawn() : "");

        if (button.isActionTypeEqual("Select_worlds"))
            placeholders = getPlaceholders(lootContainerData.getWorlds());

        return placeholders;
    }

}
