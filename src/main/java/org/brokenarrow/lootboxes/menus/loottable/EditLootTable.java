package org.brokenarrow.lootboxes.menus.loottable;

import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.lootdata.LootItems.YamlKey.GLOBAL_VALUES;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.getPlaceholders;

public final class EditLootTable extends MenuHolder {
	private final String lootTableName;
	private final LootItems lootTable = LootItems.getInstance();
	private final SettingsData settings = Lootboxes.getInstance().getSettings().getSettingsData();
	private LootData lootData ;
	private final MenuTemplate guiTemplate;

	public EditLootTable(final String lootTableName) {
		this.lootTableName = lootTableName;
		//guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Edit_loot_table").placeholders(lootTableName);
		this.lootData = lootTable.getLootData(lootTableName, GLOBAL_VALUES.getKey());
		this.guiTemplate = Lootboxes.getInstance().getMenu("Edit_loot_table");

		this.setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setMenuSize(guiTemplate.getinvSize("Edit_loot_table"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Edit_loot_table'.");
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
				if (button.isActionTypeEqual("Remove_loot_table"))
					placeholders = getPlaceholders( lootTableName,lootTableName);
				if (button.isActionTypeEqual("Change_chance"))
					placeholders = getPlaceholders(lootData.getChance(),settings.getIncrease(),settings.getDecrease());

				if (button.isActionTypeEqual("Change_minimum"))
					placeholders = getPlaceholders(lootData.getMinimum(),settings.getIncrease(),settings.getDecrease());

				if (button.isActionTypeEqual("Change_maximum"))
					placeholders = getPlaceholders(lootData.getMaximum(),settings.getIncrease(),settings.getDecrease());

				return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(),	placeholders),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(),	placeholders))
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {
		final LootData lootData = lootTable.getLootData(lootTableName, GLOBAL_VALUES.getKey());
		if (button.isActionTypeEqual("Change_minimum")) {
			int amount = 0;
			if (click == ClickType.LEFT)
				amount += 1;
			if (click == ClickType.RIGHT)
				amount -= 1;
			if (click == ClickType.SHIFT_LEFT)
				amount += settings.getIncrease();
			if (click == ClickType.SHIFT_RIGHT)
				amount -= settings.getDecrease();
			int amountCached = lootData.getMinimum() + amount;
			if (amountCached > 54)
				amountCached = 54;
			if (amountCached > lootData.getMaximum())
				amountCached = lootData.getMaximum() - 1;
			if (amountCached < 0)
				amountCached = 0;


			final LootData.Builder builder = lootData.getBuilder().setMinimum(amountCached);
			lootTable.setCachedLoot(lootTableName, GLOBAL_VALUES.getKey(), builder.build());
			this.lootData = lootTable.getLootData(lootTableName, GLOBAL_VALUES.getKey());
			return true;
		}
		if (button.isActionTypeEqual("Change_maximum")) {
			int amount = 0;
			if (click == ClickType.LEFT)
				amount += 1;
			if (click == ClickType.RIGHT)
				amount -= 1;
			if (click == ClickType.SHIFT_LEFT)
				amount += settings.getIncrease();
			if (click == ClickType.SHIFT_RIGHT)
				amount -= settings.getDecrease();
			int amountCached = lootData.getMaximum() + amount;
			if (amountCached > 54)
				amountCached = 54;
			if (amountCached < lootData.getMinimum())
				amountCached = lootData.getMinimum() + 1;
			if (amountCached < 0)
				amountCached = 0;
			final LootData.Builder builder = lootData.getBuilder().setMaximum(amountCached);
			lootTable.setCachedLoot(lootTableName, GLOBAL_VALUES.getKey(), builder.build());
			this.lootData = lootTable.getLootData(lootTableName, GLOBAL_VALUES.getKey());
			return true;
		}
		if (button.isActionTypeEqual("Remove_loot_table")) {
			lootTable.removeLootTable(lootTableName);
			new EditCreateLootTable().menuOpen(player);
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
			new EditCreateLootTable().menuOpen(player);
		}
		return false;
	}
}
