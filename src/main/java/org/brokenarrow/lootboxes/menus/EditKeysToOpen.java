package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.brokenarrow.menu.library.NMS.UpdateTittleContainers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EditKeysToOpen extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton listOfItems;
	private final MenuButton forward;
	private final MenuButton previous;
	private final MenuButton seachButton;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerData containerData = ContainerData.getInstance();
	private final org.brokenarrow.lootboxes.lootdata.ItemData itemData = org.brokenarrow.lootboxes.lootdata.ItemData.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final GuiTempletsYaml.Builder guiTemplets;

	public EditKeysToOpen(String container) {
		super(ContainerData.getInstance().getListOfKeys(container));

		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Edit_Keys_To_Open").placeholders(getPageNumber());

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		Map<ItemStack, EditCreateItems.ItemData> cacheItemData = new HashMap<>();

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

		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
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
				System.out.println("KeysToOpen " + object);
				if (object instanceof String) {
					ContainerDataBuilder data = containerData.getCacheContainerData(String.valueOf(object));
					ContainerDataBuilder.Builder builder = data.getBuilder();
					ContainerDataBuilder.KeysData keysData = data.getKeysData().get(object);
					if (keysData != null) {
						if (!keysData.getKeyName().isEmpty())
							player.sendMessage("You change the loottable from " + data.getLootTableLinked() + " to " + object);

						if (keysData.getKeyName().equals(object))
							player.sendMessage("Your change do not change the loottable is same as the old, old " + data.getLootTableLinked() + " new name " + object);
						//particleEffect.add((String) object);
					}


				/*	builder.setKeysData();
					containerData.setContainerData(container, builder.build());*/
					System.out.println("testr clickibng " + object);
					new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
				}
			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(Object object) {

				if (object instanceof String) {
					GuiTempletsYaml gui = guiTemplets.menuKey("Key_list").placeholders(object).build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			/*	if (object instanceof ItemStack) {
					ItemStack item = ((ItemStack) object);
					if (cacheItemData.get(item) == null) return null;

					return item;
				}*/
				return null;
			}
		};
		previous = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {

				if (click.isLeftClick()) {
					previousPage();
				}

				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("List_of_loottables", getPageNumber()), Material.CHEST, getMenu().getSize());
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
				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("List_of_loottables", getPageNumber()), Material.CHEST, getMenu().getSize());
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
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton.getItem();

		return null;
	}
}