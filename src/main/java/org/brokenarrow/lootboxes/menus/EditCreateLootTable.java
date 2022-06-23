package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.commandprompt.CreateTable;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.lootdata.LootItems.YamlKey.GLOBAL_VALUES;

public class EditCreateLootTable extends MenuHolder {

	private final LootItems lootItems = LootItems.getInstance();
	private final MenuButton createTable;
	private final MenuButton backButton;
	private final MenuButton newTable;
	private final MenuButton listOfTables;
	private final MenuButton forward;
	private final MenuButton previous;
	private final GuiTempletsYaml.Builder guiTemplets;
	Map<ItemStack, ItemStack> data = new HashMap<>();

	public EditCreateLootTable() {
		super(new ArrayList<>(LootItems.getInstance().getCachedLoot().keySet()));
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "LootTables").placeholders("");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());
		createTable = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
//new SimpleConversation();
				new CreateTable().start(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Create_Table").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		newTable = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				return null;
			}
		};
		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new MainMenu().menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		listOfTables = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

				if (object instanceof String) {
					if (click.isLeftClick())
						new EditCreateItems((String) object).menuOpen(player);
					else
						new EditLootTable((String) object).menuOpen(player);
				}
			}

			@Override
			public ItemStack getItem() {
				return null;
			}

			@Override
			public ItemStack getItem(Object object) {

				if (object instanceof String) {
					if (object.equals("Global_Values")) return null;
					GuiTempletsYaml gui = guiTemplets.menuKey("Loot_Tables").placeholders(object).build();

					ItemStack itemStack = CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
					data.put(itemStack, itemStack);
					return itemStack;
				} else if (object instanceof ItemStack)
					return data.get(object);
				return null;
			}
		};
		previous = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {

				if (click.isLeftClick()) {
					previousPage();
				}

			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Previous_button").build();

				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		forward = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				if (click.isLeftClick()) {
					nextPage();
				}

			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Forward_button").build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
	}

	@Override
	public MenuButton getFillButtonAt(Object o) {
		return listOfTables;

	}

	@Override
	public MenuButton getButtonAt(int slot) {

		if (guiTemplets.menuKey("Forward_button").build().getSlot().contains(slot))
			return forward;
		if (guiTemplets.menuKey("Previous_button").build().getSlot().contains(slot))
			return previous;
		if (guiTemplets.menuKey("Create_Table").build().getSlot().contains(slot))
			return createTable;
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;
		return null;
	}

	public static class EditLootTable extends MenuHolder {

		private final MenuButton removeLootTable;
		private final MenuButton maxAmount;
		private final MenuButton minAmount;
		private final MenuButton backButton;
		private final GuiTempletsYaml.Builder guiTemplets;
		private final LootItems lootTable = LootItems.getInstance();
		private final SettingsData settings = Lootboxes.getInstance().getSettings().getSettings();

		public EditLootTable(String lootTableName) {
			guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Edit_loot_table").placeholders(lootTableName);

			setMenuSize(guiTemplets.build().getGuiSize());
			setTitle(guiTemplets.build().getGuiTitle());
			removeLootTable = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					lootTable.removeFile(lootTableName);
					new EditLootTable(lootTableName).menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					GuiTempletsYaml gui = guiTemplets.menuKey("Remove_loot_table").build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			maxAmount = new MenuButton() {

				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					LootData lootData = lootTable.getLootData(lootTableName, GLOBAL_VALUES.getKey());
					int amount = 0;
					if (click == ClickType.LEFT)
						amount += 1;
					if (click == ClickType.RIGHT)
						amount -= 1;
					if (click == ClickType.SHIFT_LEFT)
						amount += settings.getIncrese();
					if (click == ClickType.SHIFT_RIGHT)
						amount -= settings.getDecrese();
					int amountCached = lootData.getMaximum() + amount;
					if (amountCached > 54)
						amountCached = 54;
					if (amountCached < lootData.getMinimum())
						amountCached = lootData.getMinimum() + 1;
					if (amountCached < 0)
						amountCached = 0;
					LootData.Builder builder = lootData.getBuilder().setMaximum(amountCached);
					lootTable.setCachedLoot(lootTableName, GLOBAL_VALUES.getKey(), builder.build());
					EditLootTable.this.updateButton(this);
				}

				@Override
				public ItemStack getItem() {
					LootData lootData = lootTable.getLootData(lootTableName, GLOBAL_VALUES.getKey());
					GuiTempletsYaml gui = guiTemplets.menuKey("Change_Maximum").placeholders(lootData.getMaximum(), settings.getIncrese(), settings.getDecrese()).build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			minAmount = new MenuButton() {

				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					LootData lootData = lootTable.getLootData(lootTableName, GLOBAL_VALUES.getKey());
					int amount = 0;
					if (click == ClickType.LEFT)
						amount += 1;
					if (click == ClickType.RIGHT)
						amount -= 1;
					if (click == ClickType.SHIFT_LEFT)
						amount += settings.getIncrese();
					if (click == ClickType.SHIFT_RIGHT)
						amount -= settings.getDecrese();
					int amountCached = lootData.getMinimum() + amount;
					if (amountCached > 54)
						amountCached = 54;
					if (amountCached > lootData.getMaximum())
						amountCached = lootData.getMaximum() - 1;
					if (amountCached < 0)
						amountCached = 0;


					LootData.Builder builder = lootData.getBuilder().setMinimum(amountCached);
					lootTable.setCachedLoot(lootTableName, GLOBAL_VALUES.getKey(), builder.build());
					EditLootTable.this.updateButton(this);
				}

				@Override
				public ItemStack getItem() {
					LootData lootData = lootTable.getLootData(lootTableName, GLOBAL_VALUES.getKey());
					GuiTempletsYaml gui = guiTemplets.menuKey("Change_Minimum").placeholders(lootData.getMinimum(), settings.getIncrese(), settings.getDecrese()).build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}

			};
			backButton = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					new EditCreateLootTable().menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
		}

		@Override
		public MenuButton getButtonAt(int slot) {
			if (guiTemplets.menuKey("Remove_loot_table").build().getSlot().contains(slot))
				return removeLootTable;
			if (guiTemplets.menuKey("Change_Maximum").build().getSlot().contains(slot))
				return maxAmount;
			if (guiTemplets.menuKey("Change_Minimum").build().getSlot().contains(slot))
				return minAmount;
			if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
				return backButton;
			return null;
		}
	}

}
