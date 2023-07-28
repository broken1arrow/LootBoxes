package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.commandprompt.SetNumbers;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.menus.MenuKeys.PARTICLE_SETTINGS;
import static org.brokenarrow.lootboxes.menus.ParticleSettings.Type.*;
import static org.brokenarrow.lootboxes.untlity.ConvetParticlesUntlity.isParticleThisClazz;

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
	 * Create menu instance. Without any arguments. Recommend you set al lest inventory/menu size.
	 */
	public ParticleSettings(String container, Object particle) {
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Particle_Settings").placeholders(particle);
		final ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(()-> guiTemplets.build().getGuiTitle());

		setParticleType = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				new ParticleAnimantion(container, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Particle_type").build();
				if (isParticleThisClazz(particle, Material.class, MaterialData.class, BlockData.class, ItemStack.class))
					gui = guiTemplets.menuKey("Particle_type").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		setMatrial = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				if (isParticleThisClazz(particle, Material.class, MaterialData.class, BlockData.class, ItemStack.class))
					new MatrialList(PARTICLE_SETTINGS, particle, container, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final ParticleEffect particleEffect = containerDataCache.getParticleEffect(container, particle);
				GuiTempletsYaml gui = guiTemplets.menuKey("Matrial_not_used").placeholders("", "").build();
				if (isParticleThisClazz(particle, Material.class, MaterialData.class, BlockData.class, ItemStack.class))
					gui = guiTemplets.menuKey("Matrial").placeholders("", particleEffect != null ? particleEffect.getMaterial() : "").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		setData = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				new SetNumbers(SET_DATA, container, particle).start(player);
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
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				if (isParticleThisClazz(particle, Particle.DustOptions.class, Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_17) ? Particle.DustTransition.class : null))
					new SetNumbers(SET_COLORS, container, particle).start(player);
			}

			@Override
			public ItemStack getItem() {
				final ParticleEffect particleEffect = containerDataCache.getParticleEffect(container, particle);
				GuiTempletsYaml gui = guiTemplets.menuKey("Colors_not_used").placeholders("", particleEffect != null && particleEffect.getParticleDustOptions() != null ? particleEffect.getParticleDustOptions().getFromColor() : "").build();
				if (isParticleThisClazz(particle, Particle.DustOptions.class, Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_17) ? Particle.DustTransition.class : null))
					gui = guiTemplets.menuKey("Colors").placeholders("", fromColor(particleEffect), toColor(particleEffect)).build();
				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		setParticleSize = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				if (isParticleThisClazz(particle, Particle.DustOptions.class, Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_17) ? Particle.DustTransition.class : null))
					new SetNumbers(SET_PARTICLE_SIZE, container, particle).start(player);
			}

			@Override
			public ItemStack getItem() {
				final ParticleEffect particleEffect = containerDataCache.getParticleEffect(container, particle);
				GuiTempletsYaml gui = guiTemplets.menuKey("Particle_Size_not-used").placeholders("", particleEffect != null && particleEffect.getParticleDustOptions() != null ? particleEffect.getParticleDustOptions().getSize() : "").build();

				if (isParticleThisClazz(particle, Particle.DustOptions.class, Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_17) ? Particle.DustTransition.class : null))
					gui = guiTemplets.menuKey("Particle_Size").placeholders("", particleEffect != null && particleEffect.getParticleDustOptions() != null ? particleEffect.getParticleDustOptions().getSize() : "").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		backButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
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

	public String fromColor(ParticleEffect particleEffect) {
		if (particleEffect != null && particleEffect.getParticleDustOptions() != null) {
			Color color = particleEffect.getParticleDustOptions().getFromColor();
			if (color != null)
				return color.getRed() + " " + color.getGreen() + " " + color.getBlue();
		}
		return "";
	}

	public String toColor(ParticleEffect particleEffect) {
		if (particleEffect != null && particleEffect.getParticleDustOptions() != null) {
			Color color = particleEffect.getParticleDustOptions().getToColor();
			if (color != null)
				return color.getRed() + " " + color.getGreen() + " " + color.getBlue();
		}
		return "";
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
		SET_COLORS,
		SET_DATA,
		SET_PARTICLE_SIZE
	}
}
