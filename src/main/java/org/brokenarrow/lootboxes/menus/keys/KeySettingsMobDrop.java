package org.brokenarrow.lootboxes.menus.keys;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.menus.EntityTypeListMenu;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.menus.MenuKeys.KEY_SETTINGS_MOBDROP;

public class KeySettingsMobDrop extends MenuHolder {
	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final SettingsData settingsData = Lootboxes.getInstance().getSettings().getSettingsData();
	private final String containerData;
	private final String keyName;
	private final MenuTemplate guiTemplate;

	public KeySettingsMobDrop(final String containerData, final String keyName) {
		this.containerData = containerData;
		this.keyName = keyName;
		//this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Key_Settings_MobDrop").placeholders(keyName, "");
		this.guiTemplate = Lootboxes.getInstance().getMenu("Key_settings_mob_drop");
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Key_settings_mob_drop"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
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
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (run(button, click))
					updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

				return CreateItemUtily.of(menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName()),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
						.setGlow(menuButton.isGlow())
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {

		if (button.isActionTypeEqual("Mob_drop_this_key")) {
			new EntityTypeListMenu(KEY_SETTINGS_MOBDROP, containerData, keyName, "").menuOpen(player);
		}
		if (button.isActionTypeEqual("Change_minimum")) {
			final KeyMobDropData data = keyDropData.getKeyMobDropData(containerData, keyName);
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
			keyDropData.putCachedKeyData(containerData, keyName, builder.build());
			return true;
		}
		if (button.isActionTypeEqual("Change_maximum")) {
			final KeyMobDropData data = keyDropData.getKeyMobDropData(containerData, keyName);
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
			keyDropData.putCachedKeyData(containerData, keyName, builder.build());
		}
		if (button.isActionTypeEqual("Change_chance")) {
			final KeyMobDropData data = keyDropData.getKeyMobDropData(containerData, keyName);
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
			keyDropData.putCachedKeyData(containerData, keyName, builder.build());
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
			new EditKey(containerData, keyName).menuOpen(player);
		}
		return false;
	}

}
