package org.brokenarrow.lootboxes.menus.keys;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
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

public class EditKeysToOpen extends MenuHolder {
	private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();
	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final String containerData;
	private final MenuTemplate guiTemplate;

	public EditKeysToOpen(String containerData) {
		super(ContainerDataCache.getInstance().getListOfKeys(containerData));
		this.containerData = containerData;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Edit_keys_to_open");

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Edit_keys_to_open"));
			setTitle(guiTemplate::getMenuTitle);
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Edit_keys_to_open'.");

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

					if (click.isShiftClick() && click.isLeftClick()) {
						Map<String, Object> map = new HashMap<>();

						KeysData keysData = containerDataCacheInstance.getCacheKey(containerData, (String) object);
						map.put(MOB_DROP_KEY_NAME.name(), keysData.getKeyName());
						map.put(MOB_DROP_CONTAINER_DATA_NAME.name(), containerData);

						String placeholderDisplayName = translatePlaceholders(keysData.getDisplayName(), keysData.getKeyName(),
								keysData.getLootTableLinked() != null && keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
						List<String> placeholdersLore = translatePlaceholdersLore(keysData.getLore(), keysData.getKeyName(),
								keysData.getLootTableLinked() != null && keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());

						player.getInventory().addItem(CreateItemUtily.of(keysData.getItemType(), placeholderDisplayName, placeholdersLore).setItemMetaDataList(map).setAmoutOfItems(1).makeItemStack());

						return;
					}
					if (click.isLeftClick())
						new EditKey(containerData, (String) object).menuOpen(player);
					if (click.isRightClick()) {
						containerDataCacheInstance.removeCacheKey(containerData, (String) object);
						keyDropData.removeKeyMobDropData(containerData, (String) object);
						new EditKeysToOpen(containerData).menuOpen(player);
					}
				}
			}

			@Override
			public ItemStack getItem() {

				if (object instanceof String) {
					org.brokenarrow.lootboxes.builder.KeysData keysData = containerDataCacheInstance.getCacheKey(containerData, String.valueOf(object));
					String placeholderDisplayName = translatePlaceholders(keysData.getDisplayName(), object, keysData.getLootTableLinked() != null && keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
					List<String> placeholdersLore = translatePlaceholdersLore(keysData.getLore(), object, keysData.getLootTableLinked() != null && keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
					org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

					String displayName = translatePlaceholders(player,menuButton.getDisplayName(),object, keysData.getAmountNeeded(), placeholderDisplayName,
							placeholdersLore);
					List<String> lore = translatePlaceholdersLore(player,menuButton.getLore(),object, keysData.getAmountNeeded(), placeholderDisplayName,
							placeholdersLore);

					return CreateItemUtily.of(keysData.getItemType(),
							displayName,
							lore)
							.setGlow(menuButton.isGlow())
							.makeItemStack();
				}
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
						.setGlow(menuButton.isGlow())
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {

		if (button.isActionTypeEqual("Add_key_button")) {
			new SaveNewKeys(containerData).menuOpen(player);
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
			new AlterContainerDataMenu(containerData).menuOpen(player);
		}
		return false;
	}

}
