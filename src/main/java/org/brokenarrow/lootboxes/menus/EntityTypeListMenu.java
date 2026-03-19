package org.brokenarrow.lootboxes.menus;

import org.apache.commons.lang.WordUtils;
import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.commandprompt.SearchInMenu;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.menus.keys.EditKey;
import org.brokenarrow.lootboxes.menus.keys.EntityTypeCachedMenu;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EntityTypeListMenu extends MenuHolderPage<EntityType> {

    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
    private final Lootboxes plugin = Lootboxes.getInstance();
    private final KeyDropData keyDropData = KeyDropData.getInstance();
    private final String containerKey;
    private final String value;
    private final MenuTemplate guiTemplate;
    private final MenuKeys menuKey;


    public EntityTypeListMenu(final MenuKeys menuKey, final String containerKey, final String value, final String entitySearchFor) {
        super(Lootboxes.getInstance().getMobList().getEntityTypeList(entitySearchFor));
        this.menuKey = menuKey;
        this.containerKey = containerKey;
        this.value = value;
        this.guiTemplate = Lootboxes.getInstance().getMenu("EntityType_list");


        setUseColorConversion(true);
        setIgnoreItemCheck(true);

        if (guiTemplate != null) {
            setAutoTitleCurrentPage(false);
            setFillSpace(guiTemplate.getFillSlots());
            setMenuSize(guiTemplate.getinvSize("EntityType_list"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), this.value, (this.getPageNumber() + 1)));
            this.setUseColorConversion(true);
        } else {
            setMenuSize(36);
            setTitle(() -> "menu settings 'EntityType_list'.");

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
                        .setGlow(menuButton.isGlow())
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

        if (button.isActionTypeEqual("Search")) {
            if (click.isLeftClick())
                new SearchInMenu(MenuKeys.ENTITY_TYPE_LISTMENU, menuKey, containerKey, value).start(player);
            else
                new EntityTypeListMenu(menuKey, containerKey, value, "").menuOpen(player);
        }
        if (button.isActionTypeEqual("Back_button")) {
            if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU)
                new AlterContainerDataMenu(containerKey).menuOpen(player);
            if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU)
                new EditKey(containerKey, value).menuOpen(player);
            if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
                new CustomizeItem(containerKey, value).menuOpen(player);
            }
            if (menuKey == MenuKeys.ENTITY_CACHED_TYPE_LISTMENU) {
                new EntityTypeCachedMenu(containerKey, value, "").menuOpen(player);
            }
        }
        return false;
    }

    @Override
    public FillMenuButton<EntityType> createFillMenuButton() {
        MenuButtonData button = this.guiTemplate.getMenuButton(-1);
        if (button == null) return null;

        return new FillMenuButton<>((player, menu, click, clickedItem, entityType) -> {
            if (entityType != null) {
                if (menuKey == MenuKeys.ENTITY_CACHED_TYPE_LISTMENU) {
                    containerDataCache.write(containerKey, containerData -> {
                        KeysData keysData = containerData.getKeysData(value);
                        if (keysData != null) {
                            if (click == ClickType.LEFT) {
                                final boolean createdMob = keyDropData.createMobLootData(entityType,containerKey, value);
                                if (!keysData.getEntityTypes().contains(entityType)) {
                                    keysData.addEntityType(entityType);
                                }
                            }
                            if (click == ClickType.RIGHT) {
                                keyDropData.removeKeyFromMob(entityType, value);
                                keysData.removeEntityType(entityType);
                            }
                        }
                    });
                    return ButtonUpdateAction.ALL;
                }
            }
            return ButtonUpdateAction.NONE;
        }, (slot, entityType) -> {
            if (entityType != null) {
                final String material = plugin.getMobList().getSpawnEggType(entityType);
                if (material == null) {
                    return CreateItemUtily.of(false, Material.SHEEP_SPAWN_EGG, "Entity: " + WordUtils.capitalizeFully(entityType.toString().replace("_", " ").toLowerCase()),
                                    TranslatePlaceHolders.getLore(player, new ArrayList<>()))
                            .makeItemStack();
                }
                return containerDataCache.read(containerKey, containerData -> {
                    org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();
                    String displayName = TranslatePlaceHolders.getDisplayName(player, menuButton.getDisplayName(), WordUtils.capitalizeFully(entityType.toString().replace("_", " ").toLowerCase()), material);
                    KeysData keysData = containerData.getKeysData(value);

                    if (keysData != null) {
                        boolean contains = keysData.getEntityTypes().contains(entityType);
                        if (contains) {
                            menuButton = button.getActiveButton();
                        }
                        if (menuButton == null) {
                            menuButton = button.getPassiveButton();
                        }
                        displayName = TranslatePlaceHolders.getDisplayName(player, menuButton.getDisplayName(), WordUtils.capitalizeFully(entityType.toString().replace("_", " ").toLowerCase()), material);
                        return CreateItemUtily.of(false, material, displayName, TranslatePlaceHolders.getLore(player, menuButton.getLore()))
                                .setGlow(contains)
                                .makeItemStack();
                    }
                    return CreateItemUtily.of(false, material, displayName, TranslatePlaceHolders.getLore(player, menuButton.getLore()))
                            .setGlow(menuButton.isGlow())
                            .makeItemStack();
                });
            }
            return null;
        });
    }
}
