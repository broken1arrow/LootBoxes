package org.brokenarrow.lootboxes.menus.loottable;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.commandprompt.CreateTable;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.MainMenu;
import org.brokenarrow.lootboxes.menus.itemcreation.EditCreateItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditCreateLootTable extends MenuHolder {
	private final MenuTemplate guiTemplate;
	Map<ItemStack, ItemStack> data = new HashMap<>();

	public EditCreateLootTable() {
		super(new ArrayList<>(LootItems.getInstance().getCachedLoot().keySet()));
		this.guiTemplate = Lootboxes.getInstance().getMenu("Loot_tables");
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Loot_tables"));
			setTitle(guiTemplate::getMenuTitle);
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Loot_tables'.");
		}

	}

	@Override
	public MenuButton getFillButtonAt(@NotNull Object object) {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (object instanceof String) {
					if (click.isLeftClick())
						new EditCreateItems((String) object).menuOpen(player);
					else
						new EditLootTable((String) object).menuOpen(player);
				}
			}

			@Override
			public ItemStack getItem() {

				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

				if (object instanceof String) {
					if (object.equals("Global_Values")) return null;
					final ItemStack itemStack = CreateItemUtily.of(menuButton.getMaterial(),
							TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(),"", object),
							TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore())).makeItemStack();
					data.put(itemStack, itemStack);
					return itemStack;
				} else if (object instanceof ItemStack)
					return data.get(object);

				return null;
			}
		};
		//return listOfItems;
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

		if (button.isActionTypeEqual("Create_table")) {
			new CreateTable().start(player);
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
			new MainMenu().menuOpen(player);
		}
		return false;
	}
}
