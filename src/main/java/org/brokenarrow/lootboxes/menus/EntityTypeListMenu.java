package org.brokenarrow.lootboxes.menus;

import org.apache.commons.lang.WordUtils;
import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.builder.KeyMobDropData.Builder;
import org.brokenarrow.lootboxes.commandprompt.SeachInMenu;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.menus.keys.EditKey;
import org.brokenarrow.lootboxes.menus.keys.KeySettingsMobDrop;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityTypeListMenu extends MenuHolder {

	private final Lootboxes plugin = Lootboxes.getInstance();
	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final String container;
	private final String value;
	private final MenuTemplate guiTemplate;
	private final MenuKeys menuKey;
	private KeyMobDropData mobDropData;

	public EntityTypeListMenu(final MenuKeys menuKey, final String container, final String value, final String entitySearchFor) {
		super(Lootboxes.getInstance().getMobList().getEntityTypeList(entitySearchFor));
		this.menuKey = menuKey;
		this.container = container;
		this.value = value;
		this.guiTemplate = Lootboxes.getInstance().getMenu("EntityType_list");
		this.mobDropData = keyDropData.getKeyMobDropData(container, value);

		setUseColorConversion(true);

		if (guiTemplate != null) {
			setAutoTitleCurrentPage(false);
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("EntityType_list"));
			setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), this.value));
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'EntityType_list'.");

		}
	}

	@Override
	public MenuButton getFillButtonAt(@NotNull Object object) {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;
		return new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (object instanceof EntityType) {
					if (menuKey == MenuKeys.KEY_SETTINGS_MOBDROP) {
						final KeyMobDropData data = keyDropData.getKeyMobDropData(container, value);
						Builder builder = data.getBuilder();
						List<EntityType> entityTypes;
						if (data.getEntityTypes() != null)
							entityTypes = data.getEntityTypes();
						else
							entityTypes = new ArrayList<>();

						if (click == ClickType.LEFT) {
							entityTypes.add((EntityType) object);
						}
						if (click == ClickType.RIGHT) {
							entityTypes.remove((EntityType) object);
						}
						builder.setEntityTypes(entityTypes);
						keyDropData.putCachedKeyData(container, value, builder.build());
						mobDropData = keyDropData.getKeyMobDropData(container, value);
						updateButtons();
					}
				}
			}

			@Override
			public ItemStack getItem() {
				if (object instanceof EntityType) {
					final String material = plugin.getMobList().getSpawnEggType((EntityType) object);

					org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();
					String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), WordUtils.capitalizeFully(object.toString().replace("_", " ").toLowerCase()), material);

					return CreateItemUtily.of(material,
									displayName,
									TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
							.setGlow(mobDropData != null && mobDropData.getEntityTypes() != null && !mobDropData.getEntityTypes().isEmpty() && mobDropData.getEntityTypes().contains(object))
							.makeItemStack();
				}
				return null;
			}
		};
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
				new SeachInMenu(MenuKeys.ENTITY_TYPE_LISTMENU, menuKey, container, value).start(player);
			else
				new EntityTypeListMenu(menuKey, container, value, "").menuOpen(player);
		}
		if (button.isActionTypeEqual("Back_button")) {
			if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU)
				new AlterContainerDataMenu(container).menuOpen(player);
			if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU)
				new EditKey(container, value).menuOpen(player);
			if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
				new CustomizeItem(container, value).menuOpen(player);
			}
			if (menuKey == MenuKeys.KEY_SETTINGS_MOBDROP) {
				new KeySettingsMobDrop(container, value).menuOpen(player);
			}
		}
		return false;
	}

}
