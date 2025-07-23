package org.brokenarrow.lootboxes.menus.keys;

import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.commandprompt.ChangeDisplayNameLore;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.menus.MaterialList;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.menus.MenuKeys.EDITKEY;
import static org.brokenarrow.lootboxes.menus.MenuKeys.EDIT_KEYS_FOR_OPEN_MENU;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.getPlaceholders;

public class EditKey extends MenuHolder {

	private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();
	private final SettingsData settings = Lootboxes.getInstance().getSettings().getSettingsData();
	private final MenuTemplate guiTemplate;
	private final String containerData;
	private final String keyName;

	public EditKey(String containerData, String keyName) {
		this.containerData = containerData;
		this.keyName = keyName;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Edit_key");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Edit_key"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),keyName));
			setMenuOpenSound(guiTemplate.getSound());
			setIgnoreItemCheck(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Edit_key'.");
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
				KeysData keysData = containerDataCacheInstance.getCacheKey(containerData, keyName);
				Object[] placeholders = new Object[0];
				if (keysData != null) {
					if (button.isActionTypeEqual("Change_amount"))
						placeholders = getPlaceholders(keysData.getAmountNeeded());
					if (button.isActionTypeEqual("Alter_display_name"))
						placeholders = getPlaceholders("",keysData.getDisplayName());
					if (button.isActionTypeEqual("Alter_lore"))
						placeholders = getPlaceholders("", keysData.getLore());
					if (button.isActionTypeEqual("Mob_drop_key")) {
						KeyMobDropData data = KeyDropData.getInstance().getKeyMobDropData(containerData, keyName);
						placeholders = getPlaceholders(data != null && data.getEntityTypes() != null ? data.getEntityTypes() : "");
					}
				}

				return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(),placeholders),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(),placeholders))
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {


		if (button.isActionTypeEqual("Change_item")) {
			new MaterialList(EDIT_KEYS_FOR_OPEN_MENU, keyName, containerData, "").menuOpen(player);
		}
		if (button.isActionTypeEqual("Change_amount")) {
			KeysData keysData = containerDataCacheInstance.getCacheKey(containerData, keyName);
			int amount = 0;
			if (click == ClickType.LEFT)
				amount += 1;
			if (click == ClickType.RIGHT)
				amount -= 1;
			if (click == ClickType.SHIFT_LEFT)
				amount += settings.getIncrease();
			if (click == ClickType.SHIFT_RIGHT)
				amount -= settings.getDecrease();
			int amountCached = keysData.getAmountNeeded() + amount;
			if (amountCached > 64)
				amountCached = 64;
			if (amountCached < 0)
				amountCached = 0;
			int finalAmountCached = amountCached;
			containerDataCacheInstance.setKeyData(containerData, keyName, keysDataWrapper -> keysDataWrapper.setAmountNeeded(finalAmountCached));
			//containerDataCacheInstance.setKeyData(KeysToSave.AMOUNT_NEEDED, amountCached, containerData, keyName);
			return true;
		}
		if (button.isActionTypeEqual("Alter_display_name")) {
			new ChangeDisplayNameLore(EDITKEY, containerData, keyName, false).start(player);
		}
		if (button.isActionTypeEqual("Alter_lore")) {
			new ChangeDisplayNameLore(EDITKEY, containerData, keyName, true).start(player);

		}
		if (button.isActionTypeEqual("Mob_drop_key")) {
			new KeySettingsMobDrop(containerData, keyName).menuOpen(player);
		}
		if (button.isActionTypeEqual("Forward_button")) {}
		if (button.isActionTypeEqual("Previous_button")) {}

		if (button.isActionTypeEqual("Back_button")) {
			new EditKeysToOpen(containerData).menuOpen(player);
		}
		return false;
	}

}