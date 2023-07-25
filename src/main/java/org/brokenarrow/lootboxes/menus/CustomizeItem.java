package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.commandprompt.SaveEnchantment;
import org.brokenarrow.lootboxes.commandprompt.SeachForEnchantment;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class CustomizeItem extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton removeButton;
	private final MenuButton changeItem;
	private final MenuButton enchantItem;
	private final MenuButton changeChance;
	private final MenuButton changeMiniAmount;
	private final MenuButton changeMaxAmount;
	private final GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();
	private final SettingsData settingsData = Lootboxes.getInstance().getSettings().getSettingsData();

	public CustomizeItem(final String lootTable, final String itemToEdit) {

		this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "CustomizeItem").placeholders("");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		//setFillSpace(guiTemplets.build().getFillSpace());
		final LootData data = lootItems.getLootData(lootTable, itemToEdit);
		changeItem = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {
				new MatrialList(MenuKeys.CUSTOMIZEITEM_MENU, itemToEdit, lootTable, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Change_Item").placeholders(data.getMaterial()).build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		enchantItem = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {
				new EnchantMents(lootTable, itemToEdit, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Enchant_Item").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		this.changeChance = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {
				final LootData data = lootItems.getLootData(lootTable, itemToEdit);
				final LootData.Builder builder = data.getBuilder();

				int amount = 0;
				if (clickType == ClickType.LEFT)
					amount += 1;
				if (clickType == ClickType.RIGHT)
					amount -= 1;
				if (clickType == ClickType.SHIFT_LEFT)
					amount += settingsData.getIncrese();
				if (clickType == ClickType.SHIFT_RIGHT)
					amount -= settingsData.getDecrese();
				int chance = data.getChance() + amount;
				if (chance > 100)
					chance = 100;
				if (chance < 0)
					chance = 0;
				builder.setChance(chance);
				lootItems.setCachedLoot(lootTable, itemToEdit, builder.build());
				updateButtons();
				System.out.println("this.changeChanc");
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final LootData data = lootItems.getLootData(lootTable, itemToEdit);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Change_Chance").placeholders(data.getChance(), settingsData.getIncrese(), settingsData.getDecrese()).build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		this.changeMiniAmount = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {
				final LootData data = lootItems.getLootData(lootTable, itemToEdit);
				final LootData.Builder builder = data.getBuilder();

				int amount = 0;
				if (clickType == ClickType.LEFT)
					amount += 1;
				if (clickType == ClickType.RIGHT)
					amount -= 1;
				if (clickType == ClickType.SHIFT_LEFT)
					amount += settingsData.getIncrese();
				if (clickType == ClickType.SHIFT_RIGHT)
					amount -= settingsData.getDecrese();
				int minimum = data.getMinimum() + amount;

				if (minimum < 0)
					minimum = 0;
				builder.setMinimum(minimum);
				lootItems.setCachedLoot(lootTable, itemToEdit, builder.build());
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final LootData data = lootItems.getLootData(lootTable, itemToEdit);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Change_Minimum").placeholders(data.getMinimum(), settingsData.getIncrese(), settingsData.getDecrese()).build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		this.changeMaxAmount = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {
				final LootData data = lootItems.getLootData(lootTable, itemToEdit);
				final LootData.Builder builder = data.getBuilder();

				int amount = 0;
				if (clickType == ClickType.LEFT)
					amount += 1;
				if (clickType == ClickType.RIGHT)
					amount -= 1;
				if (clickType == ClickType.SHIFT_LEFT)
					amount += settingsData.getIncrese();
				if (clickType == ClickType.SHIFT_RIGHT)
					amount -= settingsData.getDecrese();
				int maximum = data.getMaximum() + amount;

				if (maximum < 0)
					maximum = 0;
				builder.setMaximum(maximum);
				lootItems.setCachedLoot(lootTable, itemToEdit, builder.build());
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final LootData data = lootItems.getLootData(lootTable, itemToEdit);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Change_Maximum").placeholders(data.getMaximum(), settingsData.getIncrese(), settingsData.getDecrese()).build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		removeButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				//lootItems.getLootData(lootTable, itemToEdit);
				//lootItems.setLootData();
				final Map<String, LootData> data = lootItems.getCachedTableContents(lootTable);
				final LootData lootData = data.get(itemToEdit);
				if (lootData != null) {
					ItemData.getInstance().removeCacheItemData(lootData.getItemdataFileName(), lootData.getItemdataPath());
				}
				data.remove(itemToEdit);
				lootItems.saveTask(lootTable);
				new EditCreateItems(lootTable).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Remove_Button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};


		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				new EditCreateItems(lootTable).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};


	}

	@Override
	public MenuButton getButtonAt(final int slot) {

		if (guiTemplets.menuKey("Change_Item").build().getSlot().contains(slot))
			return changeItem;
		if (guiTemplets.menuKey("Change_Minimum").build().getSlot().contains(slot))
			return this.changeMiniAmount;
		if (guiTemplets.menuKey("Change_Maximum").build().getSlot().contains(slot))
			return this.changeMaxAmount;
		if (guiTemplets.menuKey("Enchant_Item").build().getSlot().contains(slot))
			return enchantItem;
		if (guiTemplets.menuKey("Change_Chance").build().getSlot().contains(slot))
			return changeChance;
		if (guiTemplets.menuKey("Remove_Button").build().getSlot().contains(slot))
			return removeButton;
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;

		return null;
	}


	public static class EnchantMents extends MenuHolder {
		private final MenuButton backButton;
		private final MenuButton seachButton;
		private final MenuButton forward;
		private final MenuButton previous;
		private final MenuButton enchantmentsList;
		private final GuiTempletsYaml.Builder guiTemplets;
		private final LootItems lootItems = LootItems.getInstance();
		private final ItemData itemData = ItemData.getInstance();

		public EnchantMents(final String lootTable, final String itemToEdit, final String enchantMentsToSearchFor) {
			super(Lootboxes.getInstance().getEnchantmentList().getEnchantments(enchantMentsToSearchFor));
			final Map<ItemStack, Enchantment> cachedEnchantment = new HashMap<>();
			this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "EnchantMents").placeholders("");

			setMenuSize(guiTemplets.build().getGuiSize());
			setTitle(guiTemplets.build().getGuiTitle());
			setFillSpace(guiTemplets.build().getFillSpace());

			seachButton = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {
					//Enchantment.SILK_TOUCH
					if (clickType.isLeftClick())
						new SeachForEnchantment(lootTable, itemToEdit).start(player);
					else
						new EnchantMents(lootTable, itemToEdit, "").menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					final GuiTempletsYaml gui = guiTemplets.menuKey("Seach_button").build();

					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};

			enchantmentsList = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {

					if (clickType.isLeftClick()) {
						if (o instanceof Enchantment)
							new SaveEnchantment(lootTable, itemToEdit, (Enchantment) o).start(player);
					} else if (clickType.isRightClick()) {
						if (o instanceof Enchantment) {
							final Enchantment enchantment = (Enchantment) o;
							final LootData data = lootItems.getLootData(lootTable, itemToEdit);
							final ItemStack item = itemData.getCacheItemData(data.getItemdataFileName(), data.getItemdataPath());
							boolean hasenchantsLeft = false;
							if (item.getItemMeta() != null) {
								final ItemMeta metadata = item.getItemMeta();
								metadata.removeEnchant(enchantment);
								item.setItemMeta(metadata);
								hasenchantsLeft = !metadata.getEnchants().isEmpty();
							}

							CreateItemUtily.of(item).addEnchantments(enchantment);

							//String filePatch = itemData.setCacheItemData(data.getItemdataPath(), item);
							itemData.updateCacheItemData(data.getItemdataFileName(), data.getItemdataPath(), item);
							if (!hasenchantsLeft)
								itemData.removeCacheItemData(data.getItemdataFileName(), data.getItemdataPath());
							final LootData.Builder builder = lootItems.getLootData(lootTable, itemToEdit).getBuilder();
							builder.setHaveMetadata(hasenchantsLeft);

							lootItems.setCachedLoot(lootTable, itemToEdit, builder.build());
						}
						updateButtons();
					}
				}

				@Override
				public ItemStack getItem() {
					return null;
				}

				@Override
				public ItemStack getItem(final Object object) {

					Enchantment enchantment = null;
					if (object instanceof Enchantment)
						enchantment = (Enchantment) object;
					if (object instanceof ItemStack)
						enchantment = cachedEnchantment.get(object);

					final LootData data = lootItems.getLootData(lootTable, itemToEdit);
					final ItemStack item = itemData.getCacheItemData(data.getItemdataFileName(), data.getItemdataPath());


					GuiTempletsYaml gui = guiTemplets.menuKey("Enchant_Item").placeholders("", object instanceof Enchantment ? enchantment.getKey().getKey() : enchantment != null ? enchantment.getKey().getKey() : "").build();

					final boolean haveEnchant = item != null && item.getItemMeta() != null && enchantment != null && item.getItemMeta().hasEnchant(enchantment);
					if (haveEnchant)
						gui = guiTemplets.menuKey("Item_Has_Enchantment").placeholders("", enchantment.getKey().getKey(), item.getItemMeta().getEnchants().get(enchantment).shortValue()).build();

					final ItemStack itemStack = CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).setGlow(haveEnchant).setShowEnchantments(!haveEnchant).makeItemStack();
					cachedEnchantment.put(itemStack, enchantment);
					return itemStack;
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
					final GuiTempletsYaml gui = guiTemplets.menuKey("Previous_button").build();

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
					final GuiTempletsYaml gui = guiTemplets.menuKey("Forward_button").build();
					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			backButton = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {
					new CustomizeItem(lootTable, itemToEdit).menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					final GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};

		}

		@Override
		public MenuButton getFillButtonAt(final Object o) {
			return enchantmentsList;
		}

		@Override
		public MenuButton getButtonAt(final int slot) {

			if (guiTemplets.menuKey("Seach_button").build().getSlot().contains(slot))
				return seachButton;
			if (guiTemplets.menuKey("Forward_button").build().getSlot().contains(slot))
				return forward;
			if (guiTemplets.menuKey("Previous_button").build().getSlot().contains(slot))
				return previous;
			if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
				return backButton;

			return null;
		}
	}
}
