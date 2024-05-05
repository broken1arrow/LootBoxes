package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.itemcreation.EditCreateItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.getPlaceholders;

public class CustomizeItem extends MenuHolder {

	private final LootItems lootItems = LootItems.getInstance();
	private final SettingsData settingsData = Lootboxes.getInstance().getSettings().getSettingsData();
	private final MenuTemplate guiTemplate;
	private final String lootTableName;
	private final String itemToEdit;
	private LootData data;
	public CustomizeItem(final String lootTableName, final String itemToEdit) {
		this.lootTableName = lootTableName;
		this.itemToEdit = itemToEdit;
		this.data = lootItems.getLootData(lootTableName, itemToEdit);
		this.guiTemplate = Lootboxes.getInstance().getMenu("Customize_item");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setMenuSize(guiTemplate.getinvSize("Customize_item"));
			setTitle(()->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),itemToEdit,lootTableName));
			setMenuOpenSound(guiTemplate.getSound());
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Customize_item'.");
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
				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();
				Object[] placeholders = new Object[0];
				if (button.isActionTypeEqual("Change_item"))
					placeholders = getPlaceholders(data.getMaterial() == null ? "": bountifyCapitalized(data.getMaterial()));
				if (button.isActionTypeEqual("Change_chance"))
					placeholders = getPlaceholders(data.getChance(),settingsData.getIncrease(),settingsData.getDecrease());

				if (button.isActionTypeEqual("Change_minimum"))
					placeholders = getPlaceholders(data.getMinimum(),settingsData.getIncrease(),settingsData.getDecrease());

				if (button.isActionTypeEqual("Change_maximum"))
					placeholders = getPlaceholders(data.getMaximum(),settingsData.getIncrease(),settingsData.getDecrease());


				return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(),placeholders),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(),placeholders))
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {

		final LootData data = lootItems.getLootData(lootTableName, itemToEdit);
		final LootData.Builder builder = data.getBuilder();
		int amount;

		if (button.isActionTypeEqual("Change_item")) {
			new MaterialList(MenuKeys.CUSTOMIZEITEM_MENU, itemToEdit, lootTableName, "").menuOpen(player);
		}
		if (button.isActionTypeEqual("Change_chance")) {
			amount = 0;
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
			lootItems.setCachedLoot(lootTableName, itemToEdit, builder.build());
			this.data = lootItems.getLootData(lootTableName, itemToEdit);
			return true;
		}
		if (button.isActionTypeEqual("Enchant_item")) {
			new Enchantments(lootTableName, itemToEdit, "").menuOpen(player);

		}
		if (button.isActionTypeEqual("Change_minimum")) {
			amount = 0;
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
			lootItems.setCachedLoot(lootTableName, itemToEdit, builder.build());
			this.data = lootItems.getLootData(lootTableName, itemToEdit);
			return true;
		}
		if (button.isActionTypeEqual("Change_maximum")) {
			amount = 0;
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
			lootItems.setCachedLoot(lootTableName, itemToEdit, builder.build());
			this.data = lootItems.getLootData(lootTableName, itemToEdit);
			return true;
		}
		if (button.isActionTypeEqual("Remove")) {
			final Map<String, LootData> lootDataMap = lootItems.getCachedTableContents(lootTableName);
			if (lootDataMap != null)
					ItemData.getInstance().removeCacheItemData(lootTableName, data.getItemDataPath());

			if (lootDataMap != null)
				lootDataMap.remove(itemToEdit);
			lootItems.saveTask(lootTableName);
			new EditCreateItems(lootTableName).menuOpen(player);
		}
		if (button.isActionTypeEqual("Forward_button")) {}
		if (button.isActionTypeEqual("Previous_button")) {}

		if (button.isActionTypeEqual("Back_button")) {
			new EditCreateItems(lootTableName).menuOpen(player);
		}
		return false;
	}

}
