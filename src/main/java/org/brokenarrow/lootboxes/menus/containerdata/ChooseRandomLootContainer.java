package org.brokenarrow.lootboxes.menus.containerdata;

import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.CreateNewContainerMenu;
import org.brokenarrow.lootboxes.menus.MenuKeys;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.Facing;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;

public class ChooseRandomLootContainer extends MenuHolderPage<ItemStack> {

    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
    private final MenuTemplate guiTemplate;
    private final String containerName;

    public ChooseRandomLootContainer(String containerName) {
        super(Lootboxes.getInstance().getCustomLootContainersCache().getContainerItems());
        this.containerName = containerName;
        this.guiTemplate = Lootboxes.getInstance().getMenu("Random_loot_container");

        setUseColorConversion(true);
        setIgnoreItemCheck(true);

        if (guiTemplate != null) {
            setFillSpace(guiTemplate.getFillSlots());
            setMenuSize(guiTemplate.getinvSize("Random_loot_container"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), containerName));
            setMenuOpenSound(guiTemplate.getSound());
            this.setUseColorConversion(true);
        } else {
            setMenuSize(36);
            setTitle(() -> "could not load menu 'Random_loot_container'.");

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
                                TranslatePlaceHolders.getDisplayName(player, menuButton.getDisplayName()),
                                TranslatePlaceHolders.getLore(player, menuButton.getLore()))
                        .makeItemStack();
            }
        };
    }

    public boolean run(MenuButtonData button, ClickType click) {

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

        if (button.isActionTypeEqual("Custom_container")) {
            new CreateNewContainerMenu(MenuKeys.CHOOSE_RANDOM_LOOT_CONTAINER, containerName).menuOpen(player);
        }

        if (button.isActionTypeEqual("Back_button")) {
            new SettingsContainerData(containerName).menuOpen(player);
        }
        return false;
    }

    @Override
    public FillMenuButton<ItemStack> createFillMenuButton() {
        MenuButtonData button = this.guiTemplate.getMenuButton(-1);
        if (button == null) return null;

        return new FillMenuButton<>((player, menu, click, clickedItem, itemStack) -> {
            if (itemStack != null) {
                containerDataCache.write(containerName, containerBuilder -> {
                    if (click == ClickType.SHIFT_LEFT) {
                        containerBuilder.setRandomLootContainer(containerData ->
                                containerData.setContainer(itemStack)
                        );
                        return ButtonUpdateAction.ALL;
                    }
                    Facing type = getContainerFacing(click, itemStack.getType(), containerBuilder);
                    containerBuilder.setRandomLootContainer(containerData ->
                            containerData.setBlockFace(type)
                    );
                    return ButtonUpdateAction.ALL;
                });
                return ButtonUpdateAction.ALL;
            }
            return ButtonUpdateAction.NONE;
        }, (slot, itemStack) -> containerDataCache.read(containerName, containerBuilder -> {
            org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();

            ItemStack itemstack = null;
            if (itemStack != null)
                itemstack = new ItemStack(itemStack);
            if (itemstack == null)
                return null;
            final LootContainerData lootContainerData = containerDataCache.getCacheContainerData(containerName);
            ContainerData randomLootData = lootContainerData.getRandomLootData();
            final String displayName = TranslatePlaceHolders.getDisplayName(player, menuButton.getDisplayName(), bountifyCapitalized(itemstack.getType()),  randomLootData != null? randomLootData.getFacing() :"");

            return CreateItemUtily.of(menuButton.isGlow(), itemstack,
                    displayName,
                    TranslatePlaceHolders.getLore(player, menuButton.getLore(),
                            "",
                            randomLootData != null? randomLootData.getFacing() :"",
                            randomLootData != null? randomLootData.getContainerType() :""
                    ))
                    .setCopyOfItem(true)
                    .makeItemStack();
        }));
    }

    @Nullable
    private static Facing getContainerFacing(ClickType click, Material material, LootContainerData lootContainerData) {
        boolean isChest = material == Material.CHEST || material == Material.TRAPPED_CHEST;
        int ordinal = lootContainerData.getRandomLootContainerFacing().ordinal();
        if (click.isRightClick())
            ordinal = ordinal + 1;
        if (click.isLeftClick())
            ordinal = ordinal - 1;
        if (isChest && ordinal > Facing.values().length - 3)
            ordinal = Facing.values().length - 3;
        if (ordinal < 0)
            ordinal = isChest ? Facing.values().length - 3 : Facing.values().length - 1;
        Facing type = Facing.getFace(ordinal);
        if (type == null)
            type = Facing.getFace(0);
        return type;
    }
}

