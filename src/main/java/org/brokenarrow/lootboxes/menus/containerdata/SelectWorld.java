package org.brokenarrow.lootboxes.menus.containerdata;

import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.getPlaceholders;

public class SelectWorld extends MenuHolderPage<World> {
    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
    private final MenuTemplate guiTemplate;
    private final String containerKey;
    private LootContainerData lootContainerData;

    public SelectWorld(String containerKey) {
        super(Bukkit.getWorlds());
        System.out.println("Bukkit.getWorlds() " + Bukkit.getWorlds());
        this.containerKey = containerKey;
        this.guiTemplate = Lootboxes.getInstance().getMenu("Select_World");
        this.lootContainerData = containerDataCache.getCacheContainerData(containerKey);

        setUseColorConversion(true);
        setIgnoreItemCheck(true);
        if (guiTemplate != null) {
            setMenuSize(guiTemplate.getinvSize("Select_World"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), ""));
            setFillSpace(guiTemplate.getFillSlots());
            setMenuOpenSound(guiTemplate.getSound());
            this.setUseColorConversion(true);
        } else {
            setMenuSize(36);
            setTitle(() -> "could not load menu 'Select_World'.");
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

        if (button.isActionTypeEqual("Back_button")) {
            new WorldsAllowed(containerKey).menuOpen(player);
        }

        return false;
    }

    @Override
    public FillMenuButton<World> createFillMenuButton() {
        return new FillMenuButton<>((player1, menu, click, clickedItem, world) -> {
            return containerDataCache.write(containerKey, containerBuilder -> {
                if (world == null) return ButtonUpdateAction.NONE;

                if (click.isLeftClick()) {
                    containerBuilder.addWorld(world.getName());
                } else {
                    containerBuilder.removeWorld(world.getName());
                }
                if (containerBuilder.isSpawningContainerWithCooldown()) {
                    containerDataCache.addContainerToSpawnTask(this.containerKey, containerBuilder.getCooldown());
                }
                return ButtonUpdateAction.ALL;
            });
        }, (slot, world) -> {
            final MenuButtonData button = this.guiTemplate.getMenuButton(-1);
            if (button == null) return null;
            if (world == null) return null;
            return containerDataCache.read(containerKey, containerBuilder -> {
                org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = null;
                final Object[] placeholders = setPlaceholders(button, world);
                final boolean hasWorldSet = containerBuilder.contains(world);
                if (hasWorldSet)
                    menuButton = button.getActiveButton();
                if (menuButton == null)
                    menuButton = button.getPassiveButton();

                return CreateItemUtily.of(hasWorldSet, menuButton.getMaterial(),
                                TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), placeholders),
                                TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), placeholders))
                        .makeItemStack();
            });
        });
    }


    public Object[] setPlaceholders(MenuButtonData button, World world) {
        Object[] placeholders = getPlaceholders(world != null ? world.getName() : "not exist or not loaded");
        return placeholders;
    }
}
