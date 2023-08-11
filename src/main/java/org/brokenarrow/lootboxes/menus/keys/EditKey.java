package org.brokenarrow.lootboxes.menus.keys;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.commandprompt.ChangeDisplaynameLore;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeysToSave;
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
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Edit_key"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
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
			containerDataCacheInstance.setKeyData(KeysToSave.AMOUNT_NEEDED, amountCached, containerData, keyName);
			return true;
		}
		if (button.isActionTypeEqual("Alter_display_name")) {
			new ChangeDisplaynameLore(EDITKEY, containerData, keyName, false).start(player);
		}
		if (button.isActionTypeEqual("Alter_lore")) {
			new ChangeDisplaynameLore(EDITKEY, containerData, keyName, true).start(player);

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