package org.brokenarrow.lootboxes.menus;

import org.apache.commons.lang.WordUtils;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.commandprompt.SeachForItem;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EntityTypeListMenu extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton seachButton;
	private final MenuButton forward;
	private final MenuButton previous;
	private final MenuButton entityTypeList;
	private final GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final Lootboxes plugin = Lootboxes.getInstance();
	private final KeyDropData keyDropData = KeyDropData.getInstance();

	public EntityTypeListMenu(MenuKeys menuKey, String container, String value, String itemsToSearchFor) {
		super(Lootboxes.getInstance().getMobList().getEntityTypeList(itemsToSearchFor));
		this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "EntityType_List_Menu").placeholders("");
		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		seachButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {

				if (clickType.isLeftClick())
					new SeachForItem(MenuKeys.ENTITY_TYPE_LISTMENU, menuKey, container, value).start(player);
				else
					new EntityTypeListMenu(menuKey, container, value, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Seach_button").build();

				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		entityTypeList = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {

				if (o instanceof EntityType) {
					if (menuKey == MenuKeys.KEY_SETTINGS_MOBDROP) {
						KeyMobDropData data = keyDropData.getKeyMobDropData(container, value);
						KeyMobDropData.Builder builder = data.getBuilder();
						List<EntityType> entityTypes = data.getEntityTypes();

						if (clickType == ClickType.LEFT) {
							entityTypes.add((EntityType) o);
						}
						if (clickType == ClickType.RIGHT) {
							entityTypes.remove((EntityType) o);
						}
						builder.setEntityTypes(entityTypes);
						keyDropData.putCachedKeyData(container, value, builder.build());
						updateButtons();
					}
				}
			}

			@Override
			public ItemStack getItem() {
				return null;
			}

			@Override
			public ItemStack getItem(Object object) {

				if (object instanceof EntityType) {
					KeyMobDropData data = keyDropData.getKeyMobDropData(container, value);
					Material material = plugin.getMobList().makeSpawnEggs((EntityType) object);

					GuiTempletsYaml gui = guiTemplets.menuKey("EntityType_list").placeholders(WordUtils.capitalizeFully(object.toString().replace("_", " ").toLowerCase()), material).build();
					return CreateItemUtily.of(material, gui.getDisplayName(),
							gui.getLore()).setGlow(data.getEntityTypes().contains(object)).makeItemStack();
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
		backButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
				if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU)
					new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
				if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU)
					new EditKeysToOpen.EditKey(container, value).menuOpen(player);
				if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
					new CustomizeItem(container, value).menuOpen(player);
				}
				if (menuKey == MenuKeys.KEY_SETTINGS_MOBDROP) {
					new KeySettingsMobDrop(container, value).menuOpen(player);
				}
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
	public MenuButton getFillButtonAt(Object o) {
		return entityTypeList;
	}

	@Override
	public MenuButton getButtonAt(int slot) {

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
