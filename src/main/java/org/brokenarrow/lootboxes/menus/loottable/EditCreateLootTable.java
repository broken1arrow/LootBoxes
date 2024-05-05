package org.brokenarrow.lootboxes.menus.loottable;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.button.logic.ButtonUpdateAction;
import org.broken.arrow.menu.library.button.logic.FillMenuButton;
import org.broken.arrow.menu.library.holder.MenuHolderPage;
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

public class EditCreateLootTable extends MenuHolderPage<String> {
	private final MenuTemplate guiTemplate;
	Map<ItemStack, ItemStack> data = new HashMap<>();

	public EditCreateLootTable() {
		super(new ArrayList<>(LootItems.getInstance().getCachedLoot().keySet()));
		this.guiTemplate = Lootboxes.getInstance().getMenu("Loot_tables");

		this.setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Loot_tables"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Loot_tables'.");
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

				return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
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

	@Override
	public FillMenuButton<String> createFillMenuButton() {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;

		return new FillMenuButton<>((player, menu, click, clickedItem, lootTable) -> {
			System.out.println("lootTable " + lootTable);
			if (lootTable != null) {
				if (click.isLeftClick())
					new EditCreateItems(lootTable).menuOpen(player);
				else
					new EditLootTable(lootTable).menuOpen(player);
			}
			return ButtonUpdateAction.NONE;
		}, (slot, lootTable) -> {
			org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();
			if (lootTable != null) {
				if (lootTable.equals("Global_Values")) return null;
				final ItemStack itemStack = CreateItemUtily.of(false,menuButton.getMaterial(),
						TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), "", lootTable),
						TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore())).makeItemStack();
				data.put(itemStack, itemStack);
				return itemStack;
			}
			return null;
		});
	}
}
