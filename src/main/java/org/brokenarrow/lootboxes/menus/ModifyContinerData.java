package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.commandprompt.ChangeDisplaynameLore;
import org.brokenarrow.lootboxes.commandprompt.ContainerDataLinkedLootTable;
import org.brokenarrow.lootboxes.commandprompt.CreateContainerDataName;
import org.brokenarrow.lootboxes.commandprompt.SpecifyTime;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
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
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.menus.MenuKeys.ALTER_CONTAINER_DATA_MENU;
import static org.brokenarrow.lootboxes.settings.ChatMessages.ADD_CONTINERS_TURNED_ON_ADD_CONTAINERS_WITH_TOOL;
import static org.brokenarrow.lootboxes.settings.ChatMessages.ADD_CONTINERS_TURN_ON_ADD_CONTAINERS;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS;

public class ModifyContinerData extends MenuHolder {

	private final MenuButton createContainerData;
	private final MenuButton backButton;
	private final MenuButton listOfItems;
	private final MenuButton forward;
	private final MenuButton previous;
	private final MenuButton seachButton;
	private final LootItems lootItems = LootItems.getInstance();
	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final org.brokenarrow.lootboxes.lootdata.ItemData itemData = org.brokenarrow.lootboxes.lootdata.ItemData.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final GuiTempletsYaml.Builder guiTemplets;

	public ModifyContinerData() {
		super(ContainerDataCache.getInstance().getContainerData());
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Container_data").placeholders(getPageNumber());

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		final Map<ItemStack, EditCreateItems.ItemData> cacheItemData = new HashMap<>();

		seachButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {

			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Seach_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		createContainerData = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				new CreateContainerDataName(Material.AIR).start(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("CreateLoot_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				new MainMenu().menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		listOfItems = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {

				if (object instanceof String) {
					if (click.isLeftClick())
						new AlterContainerDataMenu((String) object).menuOpen(player);
					if (click.isRightClick()) {
						containerDataCache.removeCacheContainerData((String) object);
						keyDropData.removeKey((String) object);
					}
				}
			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(final Object object) {

				if (object instanceof String) {
					final ContainerDataBuilder data = containerDataCache.getCacheContainerData(String.valueOf(object));
					if (data != null) {
						final GuiTempletsYaml gui = guiTemplets.menuKey("Loot_Tables").placeholders(object, data.getLootTableLinked(), data.getCooldown(), data.getIcon()).build();
						ItemStack itemStack = null;
						if (data.getIcon() == null || data.getIcon() == Material.AIR)
							itemStack = CreateItemUtily.of(Material.CHEST).makeItemStack();
						final ItemStack guiItem = CreateItemUtily.of(itemStack != null ? itemStack : data.getIcon(),
								gui.getDisplayName(),
								gui.getLore()).setShowEnchantments(true).makeItemStack();
						return guiItem;
					}
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

				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("Container_data", getPageNumber()));
				updateButtons();
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
				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("Container_data", getPageNumber()));
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Forward_button").build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
	}


	@Override
	public MenuButton getFillButtonAt(final Object o) {
		return listOfItems;

	}

	@Override
	public MenuButton getButtonAt(final int slot) {

		if (guiTemplets.menuKey("Forward_button").build().getSlot().contains(slot))
			return forward;
		if (guiTemplets.menuKey("Previous_button").build().getSlot().contains(slot))
			return previous;
		if (guiTemplets.menuKey("Seach_button").build().getSlot().contains(slot))
			return seachButton;
		if (guiTemplets.menuKey("CreateLoot_button").build().getSlot().contains(slot))
			return createContainerData;
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;

		return null;
	}

	public static class AlterContainerDataMenu extends MenuHolder {

		private final MenuButton containerLinkedToLootTable;
		private final MenuButton animation;
		private final MenuButton keys;
		private final MenuButton changeIcon;
		private final MenuButton changeDisplayName;
		private final MenuButton generateLootWhenClicking;
		private final MenuButton coolddownBetweenContainers;
		private final MenuButton containers;
		private final MenuButton backButton;
		private final MenuButton addRemovecontainers;
		private final MenuButton changeIfShallUsedToRandomSpawn;
		private final GuiTempletsYaml.Builder guiTemplets;
		private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
		private final Settings settings = Lootboxes.getInstance().getSettings();

		public AlterContainerDataMenu(final String container) {
			guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Alter_ContainerData_Menu").placeholders(container);
			setMenuSize(guiTemplets.build().getGuiSize());
			setTitle(guiTemplets.build().getGuiTitle());
			//setFillSpace(guiTemplets.build().getFillSpace());
			final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
			changeIfShallUsedToRandomSpawn = new MenuButton() {

				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					if (containerDataBuilder != null) {
						if (containerDataBuilder.isRandomSpawn() == click.isLeftClick()) return;

						final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
						builder.setRandomSpawn(click.isLeftClick());
						containerDataCache.setContainerData(container, builder.build());


					}
					updateButtons();
				}

				@Override
				public ItemStack getItem() {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					GuiTempletsYaml gui = guiTemplets.menuKey("Set_random_spawn_disabled").placeholders(containerDataBuilder.isRandomSpawn()).build();

					if (containerDataBuilder.isRandomSpawn()) {
						gui = guiTemplets.menuKey("Set_random_spawn").placeholders(containerDataBuilder.isRandomSpawn()).build();
					}
					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			containerLinkedToLootTable = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					if (container != null && containerDataBuilder != null) {
						if (click.isRightClick()) {
							new ContainerDataLinkedLootTable(containerDataCache.getCacheContainerData(container), container);
						} else if (click.isLeftClick()) {
							new ListOfLoottables(container).menuOpen(player);
						}
					}
				}

				@Override
				public ItemStack getItem() {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					GuiTempletsYaml gui = guiTemplets.menuKey("Container_Linked_To_LootTable").placeholders(containerDataBuilder.getLootTableLinked()).build();

					if (containerDataBuilder.isRandomSpawn()) {
						gui = guiTemplets.menuKey("Container_Linked_To_LootTable_Random_Spawn").placeholders(containerDataBuilder.getLootTableLinked()).build();
					}

					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			animation = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					new ParticleAnimantion(container, "").menuOpen(player);
				}

				@Override
				public ItemStack getItem() {

					final GuiTempletsYaml gui = guiTemplets.menuKey("Particle_Animantion").placeholders("", containerDataCache.getParticlesList(container)).build();
					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			keys = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					new EditKeysToOpen(container).menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					final GuiTempletsYaml gui = guiTemplets.menuKey("Keys_To_Open_Container").placeholders("", containerDataCache.getListOfKeys(container)).build();
					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			changeIcon = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					new MatrialList(ALTER_CONTAINER_DATA_MENU, "", container, "").menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					final GuiTempletsYaml gui = guiTemplets.menuKey("Change_Icon").placeholders("", containerDataCache.getCacheContainerData(container).getCooldown()).build();
					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			changeDisplayName = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					new ChangeDisplaynameLore(ALTER_CONTAINER_DATA_MENU, container, "", false).start(player);

				}

				@Override
				public ItemStack getItem() {
					final GuiTempletsYaml gui = guiTemplets.menuKey("Change_DisplayName").placeholders(containerDataCache.getCacheContainerData(container).getDisplayname(), container).build();

					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			generateLootWhenClicking = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					if (containerDataBuilder != null) {
						if (containerDataBuilder.isSpawningContainerWithCooldown() == click.isLeftClick()) return;

						final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
						builder.setSpawningContainerWithCooldown(click.isLeftClick());
						containerDataCache.setContainerData(container, builder.build());
					}
					updateButtons();
				}

				@Override
				public ItemStack getItem() {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					final GuiTempletsYaml gui = guiTemplets.menuKey("Generate_Loot_When_Clicking").placeholders("", containerDataBuilder.isSpawningContainerWithCooldown()).build();
					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			coolddownBetweenContainers = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					if (!containerDataBuilder.isSpawningContainerWithCooldown()) return;
					new SpecifyTime(container).start(player);
				}

				@Override
				public ItemStack getItem() {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					final GuiTempletsYaml gui;
					if (containerDataBuilder.isSpawningContainerWithCooldown())
						gui = guiTemplets.menuKey("Cooldown_Container").placeholders("", containerDataBuilder.getCooldown()).build();
					else
						gui = guiTemplets.menuKey("Cooldown_Container_Generate_Loot_is_on").placeholders("", containerDataBuilder.getCooldown()).build();

					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			containers = new MenuButton() {
				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					new ContainersLinkedList(container, "").menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);

					GuiTempletsYaml gui = guiTemplets.menuKey("Containers").placeholders("", container).build();
					if (containerDataBuilder.isRandomSpawn())
						gui = guiTemplets.menuKey("Containers_random_spawn_on").placeholders("", container).build();
					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			addRemovecontainers = new MenuButton() {
				private final SettingsData setting = settings.getSettings();

				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					if (containerDataBuilder != null && containerDataBuilder.isRandomSpawn()) return;

					if (click.isShiftClick() && click.isRightClick()) {
						new ChooseContainer(container).menuOpen(player);
						return;
					} else if (!click.isShiftClick() && click.isLeftClick()) {
						ADD_CONTINERS_TURN_ON_ADD_CONTAINERS.sendMessage(player);
						player.closeInventory();
					} else if (!click.isShiftClick() && click.isRightClick()) {
						ADD_CONTINERS_TURNED_ON_ADD_CONTAINERS_WITH_TOOL.sendMessage(player);
						player.getInventory().addItem(CreateItemUtily.of(setting.getLinkToolItem(),
										setting.getLinkToolDisplayName(), setting.getLinkToolLore())
								.setItemMetaData(ADD_AND_REMOVE_CONTAINERS.name(), container).makeItemStack());
						player.closeInventory();
					}
					player.setMetadata(ADD_AND_REMOVE_CONTAINERS.name(), new FixedMetadataValue(Lootboxes.getInstance(), container));

				}

				@Override
				public ItemStack getItem() {
					GuiTempletsYaml gui = guiTemplets.menuKey("Add_remove_containers").placeholders("", container).build();

					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					if (containerDataBuilder != null && containerDataBuilder.isRandomSpawn()) {
						gui = guiTemplets.menuKey("Add_remove_containers_Random_Spawn").placeholders("", container).build();
					}
					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			backButton = new MenuButton() {

				@Override
				public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
					new ModifyContinerData().menuOpen(player);
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

			if (guiTemplets.menuKey("Set_random_spawn").build().getSlot().contains(slot))
				return changeIfShallUsedToRandomSpawn;
			if (guiTemplets.menuKey("Container_Linked_To_LootTable").build().getSlot().contains(slot))
				return containerLinkedToLootTable;
			if (guiTemplets.menuKey("Particle_Animantion").build().getSlot().contains(slot))
				return animation;
			if (guiTemplets.menuKey("Change_DisplayName").build().getSlot().contains(slot))
				return changeDisplayName;
			if (guiTemplets.menuKey("Generate_Loot_When_Clicking").build().getSlot().contains(slot))
				return generateLootWhenClicking;
			if (guiTemplets.menuKey("Keys_To_Open_Container").build().getSlot().contains(slot))
				return keys;
			if (guiTemplets.menuKey("Change_Icon").build().getSlot().contains(slot))
				return changeIcon;
			if (guiTemplets.menuKey("Cooldown_Container").build().getSlot().contains(slot))
				return coolddownBetweenContainers;
			if (guiTemplets.menuKey("Containers").build().getSlot().contains(slot))
				return containers;
			if (guiTemplets.menuKey("Add_remove_containers").build().getSlot().contains(slot))
				return addRemovecontainers;
			if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
				return backButton;

			return null;
		}
	}
}
