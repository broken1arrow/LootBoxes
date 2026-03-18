package org.brokenarrow.lootboxes.menus.keys;

import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.getPlaceholders;

public class KeySettingsMobDropMenu extends MenuHolder {
    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
    private final KeyDropData keyDropData = KeyDropData.getInstance();
    private final SettingsData settingsData = Lootboxes.getInstance().getSettings().getSettingsData();
    private final EntityType entityType;
    private final String containerKey;
    private final String keyName;
    private final MenuTemplate guiTemplate;
    private KeyMobDropData mobDropData;

    public KeySettingsMobDropMenu(final EntityType entityType, final String containerKey, final String keyName) {
        this.entityType = entityType;
        this.containerKey = containerKey;
        this.keyName = keyName;
        //this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Key_Settings_MobDrop").placeholders(keyName, "");
        this.guiTemplate = Lootboxes.getInstance().getMenu("Key_settings_mob_drop");
        mobDropData = keyDropData.getOrCreateMobDropForKey(entityType, containerKey, keyName);

        setUseColorConversion(true);
        setIgnoreItemCheck(true);

        if (guiTemplate != null) {
            setFillSpace(guiTemplate.getFillSlots());
            setMenuSize(guiTemplate.getinvSize("Key_settings_mob_drop"));
            setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), ""));
        } else {
            setMenuSize(36);
            setTitle(() -> "could not load menu 'Key_settings_mob_drop'.");

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

                Object[] placeholders = new Object[0];

                if (button.isActionTypeEqual("Mob_drop_this_key")) {
                    java.util.List<EntityType> entityTypes = containerDataCache.read(containerKey, containerData -> {
                        KeysData keysData = containerData.getKeysData(keyName);
                        return keysData != null ? keysData.getEntityTypes() : new ArrayList<>();
                    });

                    if (entityTypes == null || entityTypes.isEmpty())
                        placeholders = getPlaceholders("No entities set");
                    else
                        placeholders = getPlaceholders(entityTypes);
                }
                if (button.isActionTypeEqual("Change_chance"))
                    placeholders = getPlaceholders(mobDropData != null ? mobDropData.getChance() : 0, settingsData.getIncrease(), settingsData.getDecrease());

                if (button.isActionTypeEqual("Change_minimum"))
                    placeholders = getPlaceholders(mobDropData != null ? mobDropData.getMinimum() : 0, settingsData.getIncrease(), settingsData.getDecrease());

                if (button.isActionTypeEqual("Change_maximum"))
                    placeholders = getPlaceholders(mobDropData != null ? mobDropData.getMaximum() : 0, settingsData.getIncrease(), settingsData.getDecrease());


                return CreateItemUtily.of(menuButton.isGlow(), menuButton.getMaterial(),
                                TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), placeholders),
                                TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), placeholders))
                        .makeItemStack();
            }
        };
    }

    public boolean run(MenuButtonData button, ClickType click) {

        if (button.isActionTypeEqual("Mob_drop_this_key")) {
            //new EntityTypeListMenu(KEY_SETTINGS_MOBDROP, containerData, keyName, "").menuOpen(player);
        }
        if (button.isActionTypeEqual("Change_minimum")) {
            final KeyMobDropData data = keyDropData.getOrCreateMobDropForKey(entityType, containerKey, keyName);
            final KeyMobDropData.Builder builder = data.getBuilder();

            int amount = 0;
            if (click == ClickType.LEFT)
                amount += 1;
            if (click == ClickType.RIGHT)
                amount -= 1;
            if (click == ClickType.SHIFT_LEFT)
                amount += settingsData.getIncrease();
            if (click == ClickType.SHIFT_RIGHT)
                amount -= settingsData.getDecrease();
            int minimum = data.getMinimum() + amount;

            if (minimum < 0)
                minimum = 0;
            builder.setMinimum(minimum);
            keyDropData.putCachedKeyData(entityType, keyName, builder.build());
            this.mobDropData = keyDropData.getOrCreateMobDropForKey(entityType, containerKey, keyName);
            return true;
        }
        if (button.isActionTypeEqual("Change_maximum")) {
            final KeyMobDropData data = keyDropData.getOrCreateMobDropForKey(entityType, containerKey, keyName);
            final KeyMobDropData.Builder builder = data.getBuilder();

            int amount = 0;
            if (click == ClickType.LEFT)
                amount += 1;
            if (click == ClickType.RIGHT)
                amount -= 1;
            if (click == ClickType.SHIFT_LEFT)
                amount += settingsData.getIncrease();
            if (click == ClickType.SHIFT_RIGHT)
                amount -= settingsData.getDecrease();
            int maximum = data.getMaximum() + amount;

            if (maximum < 0)
                maximum = 0;
            builder.setMaximum(maximum);
            keyDropData.putCachedKeyData(entityType, keyName, builder.build());
            this.mobDropData = keyDropData.getOrCreateMobDropForKey(entityType, containerKey, keyName);
            return true;
        }
        if (button.isActionTypeEqual("Change_chance")) {
            final KeyMobDropData data = keyDropData.getOrCreateMobDropForKey(entityType, containerKey, keyName);
            final KeyMobDropData.Builder builder = data.getBuilder();

            int amount = 0;
            if (click == ClickType.LEFT)
                amount += 1;
            if (click == ClickType.RIGHT)
                amount -= 1;
            if (click == ClickType.SHIFT_LEFT)
                amount += settingsData.getIncrease();
            if (click == ClickType.SHIFT_RIGHT)
                amount -= settingsData.getDecrease();
            int chance = data.getChance() + amount;
            if (chance > 100)
                chance = 100;
            if (chance < 0)
                chance = 0;
            builder.setChance(chance);
            keyDropData.putCachedKeyData(entityType, keyName, builder.build());
            this.mobDropData = keyDropData.getOrCreateMobDropForKey(entityType, containerKey, keyName);
            return true;
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
            new EntityTypeCachedMenu(containerKey, keyName, "").menuOpen(player);
        }
        return false;
    }

}
