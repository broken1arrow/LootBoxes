package org.brokenarrow.lootboxes.menus.keys;

import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.CheckItemsInsideMenu;
import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.commandprompt.SetKeyName;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SaveNewKeys extends MenuHolder {
	private final MenuTemplate guiTemplate;
	private final String containerData;

	public SaveNewKeys(String containerData) {
		this.containerData = containerData;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Save_new_keys");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Save_new_keys"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Save_new_keys'.");

		}
		setSlotsYouCanAddItems(true);
	}

	@Override
	public MenuButton getButtonAt(int slot) {
		MenuButtonData button = this.guiTemplate.getMenuButton(slot);
		if (button == null) return null;
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem) {
				if (run(button, menu, click))
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

	public boolean run(MenuButtonData button, Inventory menu, ClickType click) {

		if (button.isActionTypeEqual("Save_keys_button")) {
			CheckItemsInsideMenu checkItemsInsideMenu = getCheckItemsInsideMenu();
			checkItemsInsideMenu.setSlotsToCheck(this.getFillSpace());
			Map<Integer, ItemStack> items = checkItemsInsideMenu.getItemsFromSetSlots(menu, null, false);
			if (items == null || items.isEmpty()) return false;

			new SetKeyName(items.values().toArray(new ItemStack[0]), containerData).start(player);
		}

		if (button.isActionTypeEqual("Back_button")) {
			new EditKeysToOpen(containerData).menuOpen(player);
		}
		return false;
	}
}
