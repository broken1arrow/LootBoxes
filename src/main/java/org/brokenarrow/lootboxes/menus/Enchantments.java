package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.button.logic.ButtonUpdateAction;
import org.broken.arrow.menu.library.button.logic.FillMenuButton;
import org.broken.arrow.menu.library.holder.MenuHolderPage;
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

public class Enchantments extends MenuHolderPage<Enchantment> {
	private final String lootTableName;
	private final String itemToEdit;
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();
	private final MenuTemplate guiTemplate;
	private final Map<String, Enchantment> cachedEnchantment = new HashMap<>();
	public Enchantments(final String lootTableName, final String itemToEdit, final String enchantmentsToSearchFor) {
		super(Lootboxes.getInstance().getEnchantmentList().getEnchantments(enchantmentsToSearchFor));
		this.lootTableName = lootTableName;
		this.itemToEdit = itemToEdit;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Enchantments_list");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

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

	@Override
	public FillMenuButton<Enchantment> createFillMenuButton() {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;

		return new FillMenuButton<>((player, menu, click, clickedItem, enchantment) -> {
			if (click.isLeftClick()) {
				if (enchantment != null) new SaveEnchantment(lootTableName, itemToEdit, enchantment).start(player);
			} else if (click.isRightClick()) {
				if (enchantment != null) {
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

					itemData.updateCacheItemData(lootTableName, lootData.getItemDataPath(), item);
					if (!hasEnchantsLeft) itemData.removeCacheItemData(lootTableName, lootData.getItemDataPath());
					final Builder builder = lootItems.getLootData(lootTableName, itemToEdit).getBuilder();
					builder.setHaveMetadata(hasEnchantsLeft);

					lootItems.setCachedLoot(lootTableName, itemToEdit, builder.build());
				}
				return ButtonUpdateAction.ALL;
			}
			return ButtonUpdateAction.NONE;
		}, (slot, enchantment) -> {

			final LootData data = lootItems.getLootData(lootTableName, itemToEdit);
			final ItemStack item = itemData.getCacheItemData(lootTableName, data.getItemDataPath());

			org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

			Enchantment enchantmentCached = cachedEnchantment.get(menuButton.getMaterial());

			final boolean haveEnchant = item != null && item.getItemMeta() != null && enchantment != null && item.getItemMeta().hasEnchant(enchantment);
			if (haveEnchant) menuButton = button.getActiveButton();
			if (menuButton == null) menuButton = button.getPassiveButton();

			String displayName;
			List<String> lore;
			String enchanted = enchantment != null ? enchantment.getName() : "";
			if (haveEnchant) {
				displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), "", enchanted, item.getItemMeta().getEnchants().get(enchantment).shortValue());
				lore = TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), "", enchanted, item.getItemMeta().getEnchants().get(enchantment).shortValue());
			} else {
				displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), enchanted, enchanted, "");
				lore = TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), enchanted, enchanted, "not set");
			}

			ItemStack itemStack = CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(), displayName, lore).makeItemStack();
			cachedEnchantment.put(menuButton.getMaterial(), enchantment);
			return itemStack;
		});
	}
}