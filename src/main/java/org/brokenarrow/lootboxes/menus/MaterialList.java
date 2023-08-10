package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder.Builder;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.commandprompt.SeachInMenu;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeysToSave;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.menus.keys.EditKey;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.LootDataSave;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;

public class MaterialList extends MenuHolder {
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();

	private final Object value;
	private final MenuKeys menuKey;
	private final String container;
	private final MenuTemplate guiTemplate;
	private final ContainerDataBuilder data;

	public MaterialList(final MenuKeys menuKey, final Object value, final String container, final String itemsToSearchFor) {
		super(Lootboxes.getInstance().getMatrialList().getMatrials(itemsToSearchFor));
		this.menuKey = menuKey;
		this.value = value;
		this.container = container;
		//this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Matrial_List").placeholders("");
		data = containerDataCache.getCacheContainerData(container);

		this.guiTemplate = Lootboxes.getInstance().getMenu("Material_list");
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Material_list"));
			setTitle(guiTemplate::getMenuTitle);
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Material_list'.");
		}
		//setTitle(()-> guiTemplets.build().getGuiTitle("Matrial_List", this.getPageNumber() +1));
		setAutoTitleCurrentPage(false);
	}

	@Override
	public MenuButton getFillButtonAt(@NotNull Object object) {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (object instanceof Material) {

					if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU) {

						if (data != null) {
							final Builder builder = data.getBuilder();
							builder.setIcon((Material) object);
							containerDataCache.setContainerData(container, builder.build());
							new AlterContainerDataMenu(container).menuOpen(player);
						}
					}
					if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU) {
						containerDataCache.setKeyData(KeysToSave.ITEM_TYPE, object, container, (String) value);
						new EditKey(container, (String) value).menuOpen(player);

					}
					if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
						lootItems.setLootData(LootDataSave.ITEM, container, (String) value, object);
						new CustomizeItem(container, (String) value).menuOpen(player);
					}

					if (menuKey == MenuKeys.PARTICLE_SETTINGS) {
						final Builder builder = data.getBuilder();
						final ParticleEffect particleEffect = data.getParticleEffect(value);
						Map<Object, ParticleEffect> particleEffectList = data.getParticleEffects();

						if (particleEffect != null) {
							final ParticleEffect.Builder particleBuilder = particleEffect.getBuilder();
							if (click.isLeftClick())
								particleBuilder.setMaterial((Material) object);
							if (click.isRightClick())
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

				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

				ItemStack itemstack = null;
				if (object instanceof Material)
					itemstack = new ItemStack((Material) object);
				if (object instanceof ItemStack)
					itemstack = (ItemStack) object;
				if (itemstack == null)
					return null;
				String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), "", bountifyCapitalized(itemstack.getType()));

				return CreateItemUtily.of(itemstack,
								displayName,
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
						.setGlow(menuButton.isGlow())
						.makeItemStack();
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

		if (button.isActionTypeEqual("Search")) {
			if (click.isLeftClick())
				new SeachInMenu(MenuKeys.MATRIALLIST_MENU, menuKey, container, value).start(player);
			else
				new MaterialList(menuKey, value, container, "").menuOpen(player);
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
		if (button.isActionTypeEqual("Back_button")) {
			if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU)
				new AlterContainerDataMenu(container).menuOpen(player);
			if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU)
				new EditKey(container, (String) value).menuOpen(player);
			if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
				new CustomizeItem(container, (String) value).menuOpen(player);
			}
			if (menuKey == MenuKeys.PARTICLE_SETTINGS) {
				new ParticleSettings(container, value).menuOpen(player);
			}
		}
		return false;
	}
}