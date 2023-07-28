package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.commandprompt.SeachInMenu;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeysToSave;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.LootDataSave;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;

public class MatrialList extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton seachButton;
	private final MenuButton forward;
	private final MenuButton previous;
	private final MenuButton itemList;
	private final GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();

	public MatrialList(final MenuKeys menuKey, final Object value, final String container, final String itemsToSearchFor) {
		super(Lootboxes.getInstance().getMatrialList().getMatrials(itemsToSearchFor));
		this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Matrial_List").placeholders("");
		final ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(()-> guiTemplets.build().getGuiTitle("Matrial_List", this.getPageNumber() +1));
		setAutoTitleCurrentPage(false);
		setFillSpace(guiTemplets.build().getFillSpace());

		seachButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull ClickType clickType, final @NotNull ItemStack itemStack, final Object o) {

				if (clickType.isLeftClick())
					new SeachInMenu(MenuKeys.MATRIALLIST_MENU, menuKey, container, value).start(player);
				else
					new MatrialList(menuKey, value, container, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Seach_button").build();

				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		itemList = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull ClickType clickType, final @NotNull ItemStack itemStack, final Object o) {
				if (o instanceof Material) {

					if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU) {

						if (data != null) {
							final ContainerDataBuilder.Builder builder = data.getBuilder();
							builder.setIcon((Material) o);
							containerDataCache.setContainerData(container, builder.build());
							new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
						}
					}
					if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU) {
						containerDataCache.setKeyData(KeysToSave.ITEM_TYPE, o, container, (String) value);
						new EditKeysToOpen.EditKey(container, (String) value).menuOpen(player);

					}
					if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
						lootItems.setLootData(LootDataSave.ITEM, container, (String) value, o);
						new CustomizeItem(container, (String) value).menuOpen(player);
					}

					if (menuKey == MenuKeys.PARTICLE_SETTINGS) {
						final ContainerDataBuilder.Builder builder = data.getBuilder();
						final ParticleEffect particleEffect = data.getParticleEffect(value);
						Map<Object, ParticleEffect> particleEffectList = data.getParticleEffects();

						if (particleEffect != null) {
							final ParticleEffect.Builder particleBuilder = particleEffect.getBuilder();
							if (clickType.isLeftClick())
								particleBuilder.setMaterial((Material) o);
							if (clickType.isRightClick())
								particleBuilder.setMaterial(null);

							particleEffectList.put(value, particleBuilder.build());
							builder.setParticleEffects(particleEffectList);
							containerDataCache.setContainerData(container, builder.build());
						}
						new ParticleSettings(container, value).menuOpen(player);
					}
				}
			}

			@Override
			public ItemStack getItem() {
				return null;
			}

			@Override
			public ItemStack getItem(final @NotNull Object object) {

				ItemStack itemstack = null;
				if (object instanceof Material)
					itemstack = new ItemStack((Material) object);
				if (object instanceof ItemStack)
					itemstack = (ItemStack) object;
				if (itemstack == null)
					return null;
				final GuiTempletsYaml gui = guiTemplets.menuKey("Item_list").placeholders("", bountifyCapitalized(itemstack.getType())).build();
				return CreateItemUtily.of(itemstack, gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		previous = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {

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
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
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
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull ClickType clickType, final @NotNull ItemStack itemStack, final Object o) {
				if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU)
					new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
				if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU)
					new EditKeysToOpen.EditKey(container, (String) value).menuOpen(player);
				if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
					new CustomizeItem(container, (String) value).menuOpen(player);
				}
				if (menuKey == MenuKeys.PARTICLE_SETTINGS) {
					new ParticleSettings(container, value).menuOpen(player);


				}
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
	public MenuButton getFillButtonAt(final @NotNull Object o) {
		return itemList;
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