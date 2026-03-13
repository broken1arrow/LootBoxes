package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.commandprompt.SearchInMenu;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.menus.keys.EditKey;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.LootDataSave;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;

public class MaterialList extends MenuHolderPage<Material> {
    private final LootItems lootItems = LootItems.getInstance();
    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();

    private final Object value;
    private final MenuKeys menuKey;
    private final String containerKey;
    private final MenuTemplate guiTemplate;
    private final ContainerDataBuilder data;

    public MaterialList(final MenuKeys menuKey, final Object value, final String containerKey, final String itemsToSearchFor) {
        super(Lootboxes.getInstance().getMatrialList().getMatrials(itemsToSearchFor));
        this.menuKey = menuKey;
        this.value = value;
        this.containerKey = containerKey;
        data = containerDataCache.getCacheContainerData(containerKey);
        this.guiTemplate = Lootboxes.getInstance().getMenu("Material_list");

        setUseColorConversion(true);
        setIgnoreItemCheck(true);

        if (guiTemplate != null) {
            setFillSpace(guiTemplate.getFillSlots());
            setMenuSize(guiTemplate.getinvSize("Material_list"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), ""));
            setMenuOpenSound(guiTemplate.getSound());
            this.setUseColorConversion(true);
        } else {
            setMenuSize(36);
            setTitle(() -> "could not load menu 'Material_list'.");
        }
        //setTitle(()-> guiTemplets.build().getGuiTitle("Matrial_List", this.getPageNumber() +1));
        setAutoTitleCurrentPage(false);
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

                return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
                                TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName()),
                                TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
                        .makeItemStack();
            }
        };
    }

    public boolean run(MenuButtonData button, ClickType click) {

        if (button.isActionTypeEqual("Search")) {
            if (click.isLeftClick())
                new SearchInMenu(MenuKeys.MATRIALLIST_MENU, menuKey, containerKey, value).start(player);
            else
                new MaterialList(menuKey, value, containerKey, "").menuOpen(player);
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
        if (button.isActionTypeEqual("Back_button")) {
            if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU)
                new AlterContainerDataMenu(containerKey).menuOpen(player);
            if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU)
                new EditKey(containerKey, (String) value).menuOpen(player);
            if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
                new CustomizeItem(containerKey, (String) value).menuOpen(player);
            }
            if (menuKey == MenuKeys.PARTICLE_SETTINGS) {
                new ParticleSettings(containerKey, value).menuOpen(player);
            }
        }
        return false;
    }

    @Override
    public FillMenuButton<Material> createFillMenuButton() {
        MenuButtonData button = this.guiTemplate.getMenuButton(-1);
        if (button == null) return null;

        return new FillMenuButton<>((player, menu, click, clickedItem, material) -> {
            if (material != null) {
                openEditMenuKey(player,click, material);
            }
            return ButtonUpdateAction.NONE;
        }, (slot, material) -> {
            org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();

            ItemStack itemstack = null;
            if (material != null)
                itemstack = new ItemStack(material);
            if (itemstack == null)
                return null;
            String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), "", bountifyCapitalized(itemstack.getType()));

            return CreateItemUtily.of(menuButton.isGlow(),itemstack,
                            displayName,
                            TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
                    .makeItemStack();
        });
    }

    private void openEditMenuKey(Player player, ClickType click, Material material) {
        containerDataCache.write(containerKey, builder -> {
            if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU) {
                builder.setIcon(material);
                new AlterContainerDataMenu(containerKey).menuOpen(player);
            }
            if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU) {
                builder.writeKeysData((String) value, keysDataWrapper -> keysDataWrapper.setItemType(material));
                new EditKey(containerKey, (String) value).menuOpen(player);

            }
            if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
                lootItems.setLootData(LootDataSave.ITEM, containerKey, (String) value, material);
                new CustomizeItem(containerKey, (String) value).menuOpen(player);
            }

            if (menuKey == MenuKeys.PARTICLE_SETTINGS) {
                final ParticleEffect particleEffect = data.getParticleEffect(value);
                Map<Object, ParticleEffect> particleEffectList = data.getParticleEffects();
                if (particleEffectList == null)
                    particleEffectList = new HashMap<>();

                if (particleEffect != null) {
                    final ParticleEffect.Builder particleBuilder = particleEffect.getBuilder();
                    if (click.isLeftClick())
                        particleBuilder.setMaterial(material);
                    if (click.isRightClick())
                        particleBuilder.setMaterial(null);

                    particleEffectList.put(value, particleBuilder.build());
                    builder.setParticleEffects(particleEffectList);
                }
                new ParticleSettings(containerKey, value).menuOpen(player);
            }
        });
    }
}