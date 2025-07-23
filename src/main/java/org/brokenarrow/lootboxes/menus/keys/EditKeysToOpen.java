package org.brokenarrow.lootboxes.menus.keys;

import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.KeyMeta.MOB_DROP_CONTAINER_DATA_NAME;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.MOB_DROP_KEY_NAME;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholders;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholdersLore;

public class EditKeysToOpen extends MenuHolderPage<String> {
	private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();
	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final String containerKey;
	private final MenuTemplate guiTemplate;

	public EditKeysToOpen(String containerKey) {
		super(ContainerDataCache.getInstance().getListOfKeys(containerKey));
		this.containerKey = containerKey;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Edit_keys_to_open");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Edit_keys_to_open"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Edit_keys_to_open'.");

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

				return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName()),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {

		if (button.isActionTypeEqual("Add_key_button")) {
			new SaveNewKeys(containerKey).menuOpen(player);
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
			new AlterContainerDataMenu(containerKey).menuOpen(player);
		}
		return false;
	}

	@Override
	public FillMenuButton<String> createFillMenuButton() {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;

		return new FillMenuButton<>((player1, menu, click, clickedItem, keyName) -> {
			if (keyName != null) {
				if (click.isShiftClick() && click.isLeftClick()) {
					Map<String, Object> map = new HashMap<>();

					KeysData keysData = containerDataCacheInstance.getCacheKey(containerKey, keyName);
					map.put(MOB_DROP_KEY_NAME.name(), keysData.getKeyName());
					map.put(MOB_DROP_CONTAINER_DATA_NAME.name(), containerKey);
					String lootTable = keysData.getLootTableLinked();
					if (lootTable == null || lootTable.isEmpty()) {
						ContainerDataBuilder containerDataCache = containerDataCacheInstance.getCacheContainerData(containerKey);
						if (containerDataCache != null) {
							lootTable = containerDataCache.getLootTableLinked();
						}
					}
					final String lootTableName = lootTable != null && !lootTable.isEmpty() ? lootTable : "No table linked";
					String placeholderDisplayName = translatePlaceholders(keysData.getDisplayName(), keysData.getKeyName(),
							lootTableName, keysData.getAmountNeeded(), keysData.getItemType());
					List<String> placeholdersLore = translatePlaceholdersLore(keysData.getLore(), keysData.getKeyName(),
							lootTableName, keysData.getAmountNeeded(), keysData.getItemType());

					player.getInventory().addItem(CreateItemUtily.of(false,keysData.getItemType(), placeholderDisplayName, placeholdersLore).setItemMetaDataList(map).setAmountOfItems(1).makeItemStack());
					return ButtonUpdateAction.NONE;
				}
				if (click.isLeftClick())
					new EditKey(containerKey, keyName).menuOpen(player);
				if (click.isRightClick()) {
					containerDataCacheInstance.removeCacheKey(containerKey, keyName);
					keyDropData.removeKeyMobDropData(containerKey, keyName);
					new EditKeysToOpen(containerKey).menuOpen(player);
				}
			}
			return ButtonUpdateAction.NONE;
		}, (slot, keyName) -> {
			if (keyName != null) {
				KeysData keysData = containerDataCacheInstance.getCacheKey(containerKey, keyName);
				String lootTable = keysData.getLootTableLinked();
				if (lootTable == null || lootTable.isEmpty()) {
					ContainerDataBuilder containerDataCache = containerDataCacheInstance.getCacheContainerData(containerKey);
					if (containerDataCache != null) {
						lootTable = containerDataCache.getLootTableLinked();
					}
				}
				final String LootTableName = lootTable != null && !lootTable.isEmpty() ? lootTable : "No table linked";

				String placeholderDisplayName = translatePlaceholders(keysData.getDisplayName(), keyName, LootTableName, keysData.getAmountNeeded(), keysData.getItemType());
				List<String> placeholdersLore = translatePlaceholdersLore(keysData.getLore(), keyName, LootTableName, keysData.getAmountNeeded(), keysData.getItemType());
				org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();

				String displayName = translatePlaceholders(player,menuButton.getDisplayName(),keyName, keysData.getAmountNeeded(), placeholderDisplayName,
						placeholdersLore);
				List<String> lore = translatePlaceholdersLore(player,menuButton.getLore(),keyName, keysData.getAmountNeeded(), placeholderDisplayName,
						placeholdersLore);

				return CreateItemUtily.of(menuButton.isGlow(),keysData.getItemType(),
								displayName,
								lore)
						.makeItemStack();
			}
			return null;
		});
	}
}
