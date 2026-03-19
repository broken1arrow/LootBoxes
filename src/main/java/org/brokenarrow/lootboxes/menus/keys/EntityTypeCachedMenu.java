package org.brokenarrow.lootboxes.menus.keys;

import org.apache.commons.lang.WordUtils;
import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.commandprompt.SearchInMenu;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.menus.EntityTypeListMenu;
import org.brokenarrow.lootboxes.menus.MenuKeys;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityTypeCachedMenu extends MenuHolderPage<EntityType> {

    private final Lootboxes plugin = Lootboxes.getInstance();
    private final KeyDropData keyDropData = KeyDropData.getInstance();
    private final ContainerDataCache containerCache = Lootboxes.getInstance().getContainerDataCache();
    private final String containerKey;
    private final String keyUniqueName;
    private final MenuTemplate guiTemplate;


    public EntityTypeCachedMenu(final String containerKey, final String keyUniqueName, final String entitySearchFor) {
        super(getEntityTypes(containerKey, entitySearchFor));

        this.containerKey = containerKey;
        this.keyUniqueName = keyUniqueName;
        this.guiTemplate = Lootboxes.getInstance().getMenu("Entity_type_select_list");

        setUseColorConversion(true);
        setIgnoreItemCheck(true);

        if (guiTemplate != null) {
            setAutoTitleCurrentPage(false);
            setFillSpace(guiTemplate.getFillSlots());
            setMenuSize(guiTemplate.getinvSize("Entity_type_select_list"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), this.keyUniqueName));
            this.setUseColorConversion(true);
        } else {
            setMenuSize(36);
            setTitle(() -> "Menu settings: 'Entity_type_select_list'.");

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
        if (button.isActionTypeEqual("Mob_drop_this_key")) {
            new EntityTypeListMenu(MenuKeys.ENTITY_CACHED_TYPE_LISTMENU, containerKey, keyUniqueName, "").menuOpen(player);
        }
        if (button.isActionTypeEqual("Previous_button")) {
            if (click.isLeftClick()) {
                previousPage();
            }
        }
        if (button.isActionTypeEqual("Search")) {
            if (click.isLeftClick())
                new SearchInMenu(MenuKeys.ENTITY_CACHED_TYPE_LISTMENU, null, containerKey, keyUniqueName).start(player);
            else
                new EntityTypeCachedMenu(containerKey, keyUniqueName, "").menuOpen(player);
        }
        if (button.isActionTypeEqual("Back_button")) {
            new EditKey(containerKey, keyUniqueName).menuOpen(player);
        }
        return false;
    }

    @Override
    public FillMenuButton<EntityType> createFillMenuButton() {
        MenuButtonData button = this.guiTemplate.getMenuButton(-1);
        if (button == null) return null;

        return new FillMenuButton<>((player, menu, click, clickedItem, entityType) -> {
            if (entityType != null) {
                return containerCache.write(containerKey, containerData -> {
                    final KeysData keysData = containerData.getKeysData(this.keyUniqueName);
                    if (click.isRightClick() && keysData != null) {
                        keysData.removeEntityType(entityType);
                        keyDropData.removeKeyFromMob(entityType, keyUniqueName);
                        return ButtonUpdateAction.ALL;
                    }
                    if (click.isLeftClick() && keysData != null) {
                        new KeySettingsMobDropMenu(entityType, containerKey, keyUniqueName).menuOpen(player);
                    }
                    return ButtonUpdateAction.NONE;
                });
            }
            return ButtonUpdateAction.NONE;
        }, (slot, entityType) -> {
            if (entityType != null) {
                return containerCache.read(containerKey, containerData -> {
                    KeysData keysData = containerData.getKeysData(keyUniqueName);
                    if (keysData == null || !keysData.getEntityTypes().contains(entityType))
                        return null;
                    final String material = plugin.getMobList().getSpawnEggType(entityType);

                    org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();
                    String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), WordUtils.capitalizeFully(entityType.toString().replace("_", " ").toLowerCase()), material);

                    return CreateItemUtily.of(false, material, displayName, TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
                            .setGlow(menuButton.isGlow())
                            .makeItemStack();
                });
            }
            return null;
        });
    }

    private static List<EntityType> getEntityTypes(final String container, final String entityToSearchFor) {
        if (entityToSearchFor == null || entityToSearchFor.isEmpty())
            return Lootboxes.getInstance().getContainerDataCache().read(container, (Function<LootContainerData, List<EntityType>>) containerData ->
                    containerData.getKeysData().values().stream()
                            .flatMap(data -> data.getEntityTypes().stream())
                            .distinct()
                            .collect(Collectors.toList()));

        final String searchTag = entityToSearchFor.toUpperCase();
        return Lootboxes.getInstance().getContainerDataCache().read(container, (Function<LootContainerData, List<EntityType>>) containerData ->
                containerData.getKeysData().values().stream()
                        .flatMap(data -> data.getEntityTypes().stream())
                        .filter(type -> type.name().contains(searchTag))
                        .distinct()
                        .collect(Collectors.toList())
        );
    }
}
