package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.menus.containerdata.ModifyContainerData;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.getPlaceholders;


public class SettingsContainerData extends MenuHolder {

	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final MenuTemplate guiTemplate;
	private final String containerDataName;
	private ContainerDataBuilder containerDataBuilder;

	public SettingsContainerData(String containerDataName) {
		//guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Settings_Container_Data").placeholders(containerDataName);
		this.guiTemplate = Lootboxes.getInstance().getMenu("Settings_container_data");
		this.containerDataName = containerDataName;
		this.containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
		if (guiTemplate != null) {
			setMenuSize(guiTemplate.getinvSize("Settings_container_data"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Settings_container_data'.");
		}

/*		int decrease = settings.getSettingsData().getDecrease();
		int increase = settings.getSettingsData().getIncrease();
		maxRadius = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {
					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					int maxRadius = containerDataBuilder.getMaxRadius();

					if (click.isRightClick())
						maxRadius += click.isShiftClick() ? increase : 1;
					if (click.isLeftClick())
						maxRadius -= click.isShiftClick() ? decrease : 1;
					if (maxRadius < 1)
						maxRadius = 1;
					builder.setMaxRadius(maxRadius);
					containerDataCache.setContainerData(containerDataName, builder.build());
				}
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				GuiTempletsYaml gui = guiTemplets.menuKey("Max_radius_off").placeholders("", containerDataBuilder.getMaxRadius()).build();
				if (containerDataBuilder.isRandomSpawn()) {
					gui = guiTemplets.menuKey("Max_radius").placeholders("", containerDataBuilder.getMaxRadius()).build();
				}
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		minRadius = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {
					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					int minRadius = containerDataBuilder.getMinRadius();

					if (click.isRightClick())
						minRadius += click.isShiftClick() ? increase : 1;
					if (click.isLeftClick())
						minRadius -= click.isShiftClick() ? decrease : 1;
					if (minRadius < 1)
						minRadius = 0;
					builder.setMinRadius(minRadius);
					containerDataCache.setContainerData(containerDataName, builder.build());
				}
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				GuiTempletsYaml gui = guiTemplets.menuKey("Min_radius_off").placeholders(containerDataBuilder.getMinRadius()).build();
				if (containerDataBuilder.isRandomSpawn()) {
					gui = guiTemplets.menuKey("Min_radius").placeholders(containerDataBuilder.getMinRadius()).build();
				}
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		useWorldCenter = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					builder.setSpawnContainerFromWorldCenter(click.isLeftClick());
					if (player.getLocation().getWorld() != null)
						builder.setSpawnLocation(player.getLocation().getWorld().getSpawnLocation());
					containerDataCache.setContainerData(containerDataName, builder.build());
				}
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				GuiTempletsYaml gui = guiTemplets.menuKey("World_center_off").placeholders(containerDataBuilder.isSpawnContainerFromWorldCenter()).build();
				if (containerDataBuilder.isRandomSpawn()) {
					gui = guiTemplets.menuKey("World_center").placeholders(containerDataBuilder.isSpawnContainerFromWorldCenter()).build();
				}
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		attemptsToSpawn = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					int attempts = containerDataBuilder.getAttempts();
					if (click.isRightClick())
						attempts += 1;
					if (click.isLeftClick())
						attempts -= 1;

					builder.setAttempts(attempts);
					containerDataCache.setContainerData(containerDataName, builder.build());
				}
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				GuiTempletsYaml gui = guiTemplets.menuKey("Attempts_to_spawn_random_spawn_off").placeholders("", -1).build();
				if (containerDataBuilder.isRandomSpawn()) {
					gui = guiTemplets.menuKey("Attempts_to_spawn").placeholders("", containerDataBuilder.getAttempts()).build();
				}
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};


		generateLootWhenClicking = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {
					if (containerDataBuilder.isSpawningContainerWithCooldown() == click.isLeftClick()) return;

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					builder.setSpawningContainerWithCooldown(click.isLeftClick());
					containerDataCache.setContainerData(containerDataName, builder.build());
				}
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Generate_Loot_When_Clicking").placeholders("", containerDataBuilder.isSpawningContainerWithCooldown()).build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		containerShallGlow = new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {
					if (containerDataBuilder.isContainerShallGlow() == click.isLeftClick()) return;

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					builder.setContainerShallGlow(click.isLeftClick());
					containerDataCache.setContainerData(containerDataName, builder.build());

				}
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);

				GuiTempletsYaml gui = guiTemplets.menuKey("Show_glow").placeholders("", containerDataBuilder.isContainerShallGlow()).build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		showTitleSpawnChest = new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {
					if (containerDataBuilder.isShowTitle() == click.isLeftClick()) return;

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					builder.setShowTitle(click.isLeftClick());
					containerDataCache.setContainerData(containerDataName, builder.build());
				}
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);

				GuiTempletsYaml gui = guiTemplets.menuKey("Show_titel").placeholders("", containerDataBuilder.isShowTitle()).build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		randomLootContainer = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null)
					new ChooseRandomLootContainer(containerDataBuilder, containerDataName).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);

				GuiTempletsYaml gui = guiTemplets.menuKey("Random_loot_container_settings").placeholders("", containerDataBuilder.getRandomLootContainerItem(), containerDataBuilder.getRandomLootContainerFacing()).build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		changeIfShallUsedToRandomSpawn = new MenuButton() {

			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {
					if (containerDataBuilder.isRandomSpawn() == click.isLeftClick()) return;

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					builder.setRandomSpawn(click.isLeftClick());
					containerDataCache.setContainerData(containerDataName, builder.build());


				}
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				GuiTempletsYaml gui = guiTemplets.menuKey("Set_random_spawn_disabled").placeholders(containerDataBuilder.isRandomSpawn()).build();

				if (containerDataBuilder.isRandomSpawn()) {
					gui = guiTemplets.menuKey("Set_random_spawn").placeholders(containerDataBuilder.isRandomSpawn()).build();
				}
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				new AlterContainerDataMenu(containerDataName).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};*/
	}

	@Override
	public MenuButton getButtonAt(int slot) {
		MenuButtonData button = this.guiTemplate.getMenuButton(slot);
		if (button == null) return null;
		return new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (run(button, click))
					updateButtons();
			}

			@Override
			public ItemStack getItem() {
				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();
				Object[] placeholders = setPlaceholders(button, containerDataBuilder);
				menuButton = getActiveButton(button, containerDataBuilder);

				if (menuButton == null)
					menuButton = button.getPassiveButton();

				return CreateItemUtily.of(menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), placeholders),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), placeholders))
						.setGlow(menuButton.isGlow())
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {
		ContainerDataBuilder containerDataBuilder = this.containerDataBuilder;
		int decrease = settings.getSettingsData().getDecrease();
		int increase = settings.getSettingsData().getIncrease();

		if (containerDataBuilder != null) {
			final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
			boolean buttonMatch = false;
			switch (button.getActionType()) {
				case "Container_type":
					new ChooseRandomLootContainer(containerDataBuilder, containerDataName).menuOpen(player);
					break;
				case "Show_title":
					builder.setShowTitle(click.isLeftClick());
					buttonMatch = true;
					break;
				case "Glow":
					builder.setContainerShallGlow(click.isLeftClick());
					buttonMatch = true;
					break;
				case "Random_spawn":
					builder.setRandomSpawn(click.isLeftClick());
					buttonMatch = true;
					break;
				case "Loot_on_timer":
					builder.setSpawningContainerWithCooldown(click.isLeftClick());
					buttonMatch = true;
					break;
				case "Attempts":
					int attempts = containerDataBuilder.getAttempts();

					if (click.isRightClick())
						attempts += 1;
					if (click.isLeftClick())
						attempts -= 1;

					builder.setAttempts(attempts);
					buttonMatch = true;
					break;
				case "Min_radius":
					int minRadius = containerDataBuilder.getMinRadius();

					if (click.isRightClick())
						minRadius += click.isShiftClick() ? increase : 1;
					if (click.isLeftClick())
						minRadius -= click.isShiftClick() ? decrease : 1;
					if (minRadius < 1)
						minRadius = 0;
					builder.setMinRadius(minRadius);
					buttonMatch = true;
					break;
				case "Max_radius":
					int maxRadius = containerDataBuilder.getMaxRadius();
					if (click.isRightClick())
						maxRadius += click.isShiftClick() ? increase : 1;
					if (click.isLeftClick())
						maxRadius -= click.isShiftClick() ? decrease : 1;
					if (maxRadius < 1)
						maxRadius = 0;
					builder.setMaxRadius(maxRadius);
					buttonMatch = true;
					break;
				case "World_center":
					builder.setSpawnContainerFromWorldCenter(click.isLeftClick());
					if (player.getLocation().getWorld() != null)
						builder.setSpawnLocation(player.getLocation().getWorld().getSpawnLocation());
					buttonMatch = true;
					break;
				case "Back_button":
					new ModifyContainerData().menuOpen(player);
					break;
			}
			if (buttonMatch) {
				containerDataCache.setContainerData(containerDataName, builder.build());
				this.containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				return true;
			}
		}
		if (button.isActionTypeEqual("Forward_button")) {
		}
		if (button.isActionTypeEqual("Previous_button")) {
		}
		if (button.isActionTypeEqual("Search")) {
		}

		if (button.isActionTypeEqual("Back_button")) {
			new AlterContainerDataMenu(containerDataName).menuOpen(player);
		}
		return false;
	}

	public org.broken.arrow.menu.button.manager.library.utility.MenuButton getActiveButton(MenuButtonData button, ContainerDataBuilder containerDataBuilder) {

		if (containerDataBuilder.isRandomSpawn()) {
			switch (button.getActionType()) {
				case "Random_spawn":
				case "Attempts":
				case "Min_radius":
				case "Max_radius":
				case "World_center":
					return button.getActiveButton();
			}
		}
		return null;
	}

	public Object[] setPlaceholders(MenuButtonData button, ContainerDataBuilder containerDataBuilder) {
		Object[] placeholders = getPlaceholders("");
		if (button.isActionTypeEqual("Container_type"))
			placeholders = getPlaceholders("",
					containerDataBuilder.getRandomLootContainerItem(),
					containerDataBuilder.getRandomLootContainerFacing());

		if (button.isActionTypeEqual("Show_title"))
			placeholders = getPlaceholders("", containerDataBuilder.isShowTitle());

		if (button.isActionTypeEqual("Glow"))
			placeholders = getPlaceholders("", containerDataBuilder.isContainerShallGlow());

		if (button.isActionTypeEqual("Random_spawn"))
			placeholders = getPlaceholders(containerDataBuilder.isRandomSpawn());

		if (button.isActionTypeEqual("Loot_on_timer"))
			placeholders = getPlaceholders("", containerDataBuilder.isSpawningContainerWithCooldown());

		if (button.isActionTypeEqual("Attempts"))
			placeholders = getPlaceholders("", containerDataBuilder.getAttempts());

		if (button.isActionTypeEqual("Min_radius"))
			placeholders = getPlaceholders(containerDataBuilder.getMinRadius(), containerDataBuilder.getMinRadius());

		if (button.isActionTypeEqual("Max_radius"))
			placeholders = getPlaceholders("", containerDataBuilder.getMaxRadius());

		if (button.isActionTypeEqual("World_center"))
			placeholders = getPlaceholders( containerDataBuilder.isSpawnContainerFromWorldCenter());

		return placeholders;
	}

}
