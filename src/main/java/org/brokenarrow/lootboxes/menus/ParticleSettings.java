package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.commandprompt.SetNumbers;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static org.brokenarrow.lootboxes.menus.MenuKeys.PARTICLE_SETTINGS;
import static org.brokenarrow.lootboxes.menus.ParticleSettings.Type.SETDATA;

public class ParticleSettings extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton setParticleType;
	private final MenuButton setMatrial;
	private final MenuButton setData;
	private final MenuButton setColors;
	private final MenuButton setParticleSize;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final GuiTempletsYaml.Builder guiTemplets;

	/**
	 * Create menu instance. With out any aguments. Recomend you set al lest inventory/menu size.
	 */
	public ParticleSettings(String container, Object particle) {
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Particle_Settings").placeholders(particle);
		final ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());

		setParticleType = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new ParticleAnimantion(container, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Particle_type").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		setMatrial = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new MatrialList(PARTICLE_SETTINGS, particle, container, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final ParticleEffect particleEffect = containerDataCache.getParticleEffect(container, particle);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Matrial").placeholders("", particleEffect != null ? particleEffect.getMaterial() : "").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		setData = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new SetNumbers(SETDATA).start(player);
			}

			@Override
			public ItemStack getItem() {
				final ParticleEffect particleEffect = containerDataCache.getParticleEffect(container, particle);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Data").placeholders("", particleEffect != null ? particleEffect.getData() : "").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		setColors = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				final ParticleEffect particleEffect = containerDataCache.getParticleEffect(container, particle);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Colors").placeholders("", particleEffect != null && particleEffect.getParticleDustOptions() != null ? particleEffect.getParticleDustOptions().getFromColor() : "").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		setParticleSize = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				final ParticleEffect particleEffect = containerDataCache.getParticleEffect(container, particle);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Particle_Size").placeholders("", particleEffect != null && particleEffect.getParticleDustOptions() != null ? particleEffect.getParticleDustOptions().getSize() : "").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		backButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new ParticleAnimantion(container, "").menuOpen(player);
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
	public MenuButton getButtonAt(int slot) {
		if (guiTemplets.menuKey("Matrial").build().getSlot().contains(slot))
			return setMatrial;
		if (guiTemplets.menuKey("Colors").build().getSlot().contains(slot))
			return setColors;
		if (guiTemplets.menuKey("Data").build().getSlot().contains(slot))
			return setData;
		if (guiTemplets.menuKey("Particle_Size").build().getSlot().contains(slot))
			return setParticleSize;
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;
		return null;
	}

	public enum Type {
		SETCOLORS,
		SETDATA,
		SET_PARTICLE_SIZE
	}
}
