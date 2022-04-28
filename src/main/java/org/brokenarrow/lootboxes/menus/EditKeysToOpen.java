package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.commandprompt.ChangeDisplaynameLore;
import org.brokenarrow.lootboxes.commandprompt.SetKeyName;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.lootdata.KeysToSave;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.CheckItemsInsideInventory;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.brokenarrow.menu.library.NMS.UpdateTittleContainers;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

import static org.brokenarrow.lootboxes.menus.MenuKeys.EDITKEY;
import static org.brokenarrow.lootboxes.menus.MenuKeys.EDIT_KEYS_FOR_OPEN_MENU;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholders;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholdersLore;

public class EditKeysToOpen extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton listOfItems;
	private final MenuButton forward;
	private final MenuButton previous;
	private final MenuButton seachButton;
	private final MenuButton addKeyButton;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();
	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final org.brokenarrow.lootboxes.lootdata.ItemData itemData = org.brokenarrow.lootboxes.lootdata.ItemData.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final GuiTempletsYaml.Builder guiTemplets;

	public EditKeysToOpen(String containerData) {
		super(ContainerDataCache.getInstance().getListOfKeys(containerData));

		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Edit_Keys_To_Open").placeholders(getPageNumber());

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		seachButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Seach_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		addKeyButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new SaveNewKeys(containerData).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Add_key_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new ModifyContinerData.AlterContainerDataMenu(containerData).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		listOfItems = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				if (object instanceof String) {
					if (click.isLeftClick())
						new EditKey(containerData, (String) object).menuOpen(player);
					else if (click.isRightClick()) {
						containerDataCacheInstance.removeCacheKey(containerData, (String) object);
						keyDropData.removeKeyMobDropData(containerData, (String) object);
						new EditKeysToOpen(containerData).menuOpen(player);
					}
				}


				/*	builder.setKeysData();
					containerData.setContainerData(container, builder.build());*/
				System.out.println("testr clickibng " + object);
			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(Object object) {

				if (object instanceof String) {
					org.brokenarrow.lootboxes.builder.KeysData keysData = containerDataCacheInstance.getCacheKey(containerData, String.valueOf(object));
					String placeholderDisplayName = translatePlaceholders(keysData.getDisplayName(), object, keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
					List<String> placeholdersLore = translatePlaceholdersLore(keysData.getLore(), object, keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());

					GuiTempletsYaml gui = guiTemplets.menuKey("Key_list").placeholders(object, keysData.getAmountNeeded(), placeholderDisplayName,
							placeholdersLore).build();

					return CreateItemUtily.of(keysData.getItemType(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
				return null;
			}
		};
		previous = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {

				if (click.isLeftClick()) {
					previousPage();
				}

				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("List_of_loottables", getPageNumber()));
				updateButtons();
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

				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("List_of_loottables", getPageNumber()));
				updateButtons();
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
	public ItemStack getFillItemsAt(Object o) {
		return listOfItems.getItem(o);

	}

	@Override
	public ItemStack getItemAt(int slot) {

		if (guiTemplets.menuKey("Forward_button").build().getSlot().contains(slot))
			return forward.getItem();
		if (guiTemplets.menuKey("Previous_button").build().getSlot().contains(slot))
			return previous.getItem();
		if (guiTemplets.menuKey("Seach_button").build().getSlot().contains(slot))
			return seachButton.getItem();
		if (guiTemplets.menuKey("Add_key_button").build().getSlot().contains(slot))
			return addKeyButton.getItem();
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton.getItem();

		return null;
	}

	public static class EditKey extends MenuHolder {

		private final MenuButton backButton;
		private final MenuButton changeItem;
		private final MenuButton changeAmount;
		private final MenuButton displayName;
		private final MenuButton lore;
		private final MenuButton mobDropKey;
		private final GuiTempletsYaml.Builder guiTemplets;
		private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();

		public EditKey(String containerData, String keyName) {
			guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Edit_Key").placeholders(getPageNumber());

			setMenuSize(guiTemplets.build().getGuiSize());
			setTitle(guiTemplets.build().getGuiTitle());
			//setFillSpace(guiTemplets.build().getFillSpace());
			//LootData data = lootItems.getLootData(lootTable, itemToEdit);

			this.changeItem = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
					new MatrialList(EDIT_KEYS_FOR_OPEN_MENU, keyName, containerData, "").menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					org.brokenarrow.lootboxes.builder.KeysData keysData = containerDataCacheInstance.getCacheKey(containerData, keyName);
					GuiTempletsYaml gui = guiTemplets.menuKey("Change_Item").placeholders(keysData.getItemType()).build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			this.changeAmount = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {


					org.brokenarrow.lootboxes.builder.KeysData keysData = containerDataCacheInstance.getCacheKey(containerData, keyName);
					int amount = 0;
					if (clickType == ClickType.LEFT)
						amount += 1;
					if (clickType == ClickType.RIGHT)
						amount -= 1;
					if (clickType == ClickType.SHIFT_LEFT)
						amount += 10;
					if (clickType == ClickType.SHIFT_RIGHT)
						amount -= 10;
					int amountCached = keysData.getAmountNeeded() + amount;
					if (amountCached > 64)
						amountCached = 64;
					if (amountCached < 0)
						amountCached = 0;
					containerDataCacheInstance.setKeyData(KeysToSave.AMOUNT_NEEDED, amountCached, containerData, keyName);

					updateButtons();
				}

				@Override
				public ItemStack getItem() {
					org.brokenarrow.lootboxes.builder.KeysData keysData = containerDataCacheInstance.getCacheKey(containerData, keyName);
					GuiTempletsYaml gui = guiTemplets.menuKey("Change_Amount").placeholders(keysData.getAmountNeeded()).build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			this.displayName = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
					new ChangeDisplaynameLore(EDITKEY, containerData, keyName, false).start(player);
				}

				@Override
				public ItemStack getItem() {
					org.brokenarrow.lootboxes.builder.KeysData keysData = containerDataCacheInstance.getCacheKey(containerData, keyName);
					String placeholderDisplayName = translatePlaceholders(keysData.getDisplayName(), keyName, keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
					GuiTempletsYaml gui = guiTemplets.menuKey("Alter_Display_name").placeholders("", placeholderDisplayName).build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			this.lore = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
					new ChangeDisplaynameLore(EDITKEY, containerData, keyName, true).start(player);
				}

				@Override
				public ItemStack getItem() {
					org.brokenarrow.lootboxes.builder.KeysData keysData = containerDataCacheInstance.getCacheKey(containerData, keyName);
					List<String> placeholdersLore = translatePlaceholdersLore(keysData.getLore(), keyName, keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
					GuiTempletsYaml gui = guiTemplets.menuKey("Alter_Lore").placeholders("", placeholdersLore).build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			this.mobDropKey = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					new KeySettingsMobDrop(containerData, keyName).menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					GuiTempletsYaml gui = guiTemplets.menuKey("Mob_Drop_Key").build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};

			this.backButton = new MenuButton() {

				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					new EditKeysToOpen(containerData).menuOpen(player);
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
		public ItemStack getItemAt(int slot) {

			if (guiTemplets.menuKey("Change_Item").build().getSlot().contains(slot))
				return this.changeItem.getItem();
			if (guiTemplets.menuKey("Change_Amount").build().getSlot().contains(slot))
				return this.changeAmount.getItem();
			if (guiTemplets.menuKey("Alter_Display_name").build().getSlot().contains(slot))
				return this.displayName.getItem();
			if (guiTemplets.menuKey("Alter_Lore").build().getSlot().contains(slot))
				return this.lore.getItem();
			if (guiTemplets.menuKey("Mob_Drop_Key").build().getSlot().contains(slot))
				return this.mobDropKey.getItem();
			if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
				return this.backButton.getItem();

			return null;
		}
	}

	public static class SaveNewKeys extends MenuHolder {
		private final MenuButton saveItems;
		private final MenuButton backButton;
		private final GuiTempletsYaml.Builder guiTemplets;
		private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();

		public SaveNewKeys(String containerData) {
			guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Save_new_keys");

			setMenuSize(guiTemplets.build().getGuiSize());
			setTitle(guiTemplets.build().getGuiTitle());
			setFillSpace(guiTemplets.build().getFillSpace());
			/*setFillSpace(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));*/
			setSlotsYouCanAddItems(true);

			saveItems = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					Map<Integer, ItemStack> items = new CheckItemsInsideInventory().getItemsExceptBottomBar(menu, null, false);
					if (items == null || items.isEmpty()) return;

					new SetKeyName(items.values().toArray(new ItemStack[0]), containerData).start(player);
				}

				@Override
				public ItemStack getItem() {
					GuiTempletsYaml gui = guiTemplets.menuKey("Save_keys_button").build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			this.backButton = new MenuButton() {

				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					new EditKeysToOpen(containerData).menuOpen(player);
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
		public ItemStack getItemAt(int slot) {

			if (guiTemplets.menuKey("Save_keys_button").build().getSlot().contains(slot))
				return saveItems.getItem();
			if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
				return this.backButton.getItem();
			return null;
		}
	}

}
