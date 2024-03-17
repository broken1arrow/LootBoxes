package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.builder.LootData.Builder;
import org.brokenarrow.lootboxes.commandprompt.SaveEnchantment;
import org.brokenarrow.lootboxes.commandprompt.SeachForEnchantment;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enchantments extends MenuHolder {
	private final String lootTableName;
	private final String itemToEdit;
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();
	private final MenuTemplate guiTemplate;
	private final Map<ItemStack, Enchantment> cachedEnchantment = new HashMap<>();
	public Enchantments(final String lootTableName, final String itemToEdit, final String enchantmentsToSearchFor) {
		super(Lootboxes.getInstance().getEnchantmentList().getEnchantments(enchantmentsToSearchFor));
		this.lootTableName = lootTableName;
		this.itemToEdit = itemToEdit;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Enchantments_list");

		setUseColorConversion(true);

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Enchantments_list"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),itemToEdit,lootTableName));
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Enchantments_list'.");

		}
	}


	@Override
	public MenuButton getFillButtonAt(@NotNull Object object) {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;
		return new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {

				if (click.isLeftClick()) {
					if (object instanceof Enchantment)
						new SaveEnchantment(lootTableName, itemToEdit, (Enchantment) object).start(player);
				} else if (click.isRightClick()) {
					if (object instanceof Enchantment) {
						final Enchantment enchantment = (Enchantment) object;
						final LootData lootData = lootItems.getLootData(lootTableName, itemToEdit);

						final ItemStack item = itemData.getCacheItemData(lootTableName, lootData.getItemDataPath());
						boolean hasEnchantsLeft = false;
						if (item != null && item.getItemMeta() != null) {
							final ItemMeta metadata = item.getItemMeta();
							metadata.removeEnchant(enchantment);
							item.setItemMeta(metadata);
							hasEnchantsLeft = !metadata.getEnchants().isEmpty();
						}

						//ItemStack itemStack = CreateItemUtily.of(item).addEnchantments(enchantment).makeItemStack();

						itemData.updateCacheItemData(lootTableName, lootData.getItemDataPath(), item );
						if (!hasEnchantsLeft)
							itemData.removeCacheItemData(lootTableName, lootData.getItemDataPath());
						final Builder builder = lootItems.getLootData(lootTableName, itemToEdit).getBuilder();
						builder.setHaveMetadata(hasEnchantsLeft);

						lootItems.setCachedLoot(lootTableName, itemToEdit, builder.build());
					}
					updateButtons();
				}
			}

			@Override
			public ItemStack getItem() {

				Enchantment enchantment = null;
				if (object instanceof Enchantment)
					enchantment = (Enchantment) object;
				if (object instanceof ItemStack)
					enchantment = cachedEnchantment.get(object);

				final LootData data = lootItems.getLootData(lootTableName, itemToEdit);
				final ItemStack item = itemData.getCacheItemData(lootTableName, data.getItemDataPath());

				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton;
				final boolean haveEnchant = item != null && item.getItemMeta() != null && enchantment != null && item.getItemMeta().hasEnchant(enchantment);

				if (haveEnchant)
					menuButton = button.getActiveButton();
				else
					menuButton = button.getPassiveButton();
				if (menuButton == null)
					menuButton = button.getPassiveButton();

				String displayName;
				List<String> lore;
				String enchanted = object instanceof Enchantment ? enchantment.getName() : enchantment != null ? enchantment.getName() : "";
				if (haveEnchant) {
					displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), "", enchanted, item.getItemMeta().getEnchants().get(enchantment).shortValue());
					lore = TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), "", enchanted, item.getItemMeta().getEnchants().get(enchantment).shortValue());
				} else {
					displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), enchanted, enchanted, "");
					lore = TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), enchanted, enchanted, "not set");
				}

				ItemStack itemStack = CreateItemUtily.of(menuButton.getMaterial(),
								displayName,
								lore)
						.makeItemStack();
				cachedEnchantment.put(itemStack, enchantment);
				return itemStack;
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
			if (click.isLeftClick())
				new SeachForEnchantment(lootTableName, itemToEdit).start(player);
			else
				new Enchantments(lootTableName, itemToEdit, "").menuOpen(player);
		}
		if (button.isActionTypeEqual("Back_button")) {
			new CustomizeItem(lootTableName, itemToEdit).menuOpen(player);
		}
		return false;
	}

}