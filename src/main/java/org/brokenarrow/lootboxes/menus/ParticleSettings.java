package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.commandprompt.SetNumbers;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.brokenarrow.lootboxes.untlity.ServerVersion.Version;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
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
import static org.brokenarrow.lootboxes.untlity.ConvertParticlesUnity.isParticleThisClazz;

public class ParticleSettings extends MenuHolder {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final String container;
	private final Object particle;
	private final ParticleEffect particleEffect;
	private boolean canSetColor = true;
	private boolean isUsingMaterial = true;
	private final MenuTemplate guiTemplate;

	public ParticleSettings(String container, Object particle) {
		this.container = container;
		this.particle = particle;
		final ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
		this.particleEffect = containerDataCache.getParticleEffect(container, particle);
		this.guiTemplate = Lootboxes.getInstance().getMenu("Particle_settings");

		setUseColorConversion(true);
		if (guiTemplate != null) {
			setMenuSize(guiTemplate.getinvSize("Particle_settings"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Particle_settings'.");
		}
		if (!isParticleThisClazz(particle, Material.class, MaterialData.class, Lootboxes.getInstance().getServerVersion().atLeast(Version.v1_9) ?BlockData.class: null, ItemStack.class))
			this.isUsingMaterial = false;
		if (!isParticleThisClazz(particle, Lootboxes.getInstance().getServerVersion().atLeast(Version.v1_13) ? Particle.DustOptions.class : null, Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_17) ? Particle.DustTransition.class : null))
			this.canSetColor = false;
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
				Object[] placeholders = new Object[0];
				menuButton = getActiveButton(button);
				if (menuButton == null)
					menuButton = button.getPassiveButton();
				if (button.isActionTypeEqual("Data")) {
					String data = particleEffect != null ? particleEffect.getData() + "" : "";
					placeholders = new Object[]{data, data};
				}
				if (button.isActionTypeEqual("Particle_material")) {
					String material = particleEffect != null ? particleEffect.getMaterial() + "" : "";
					placeholders = new Object[]{material, material};
				}
				if (button.isActionTypeEqual("Particle_size")) {
					String size = particleEffect != null && particleEffect.getParticleDustOptions() != null ? particleEffect.getParticleDustOptions().getSize() + "" : "";
					placeholders = new Object[]{size, size};
				}
				if (button.isActionTypeEqual("Particle_colors")) {
					String toColor = toColor(particleEffect);
					String fromColor = fromColor(particleEffect);
					placeholders = new Object[]{fromColor,toColor.isEmpty() ? fromColor:toColor};
				}

				return CreateItemUtily.of(menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), placeholders),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), placeholders))
						.setGlow(menuButton.isGlow())
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {

		if (isUsingMaterial) {
			if (button.isActionTypeEqual("Particle_material")) {
				new MaterialList(PARTICLE_SETTINGS, particle, container, "").menuOpen(player);
			}
		}
		if (button.isActionTypeEqual("Data")) {
			new SetNumbers(SET_DATA, container, particle).start(player);
		}
		if (canSetColor) {
			if (button.isActionTypeEqual("Particle_colors")) {
				new SetNumbers(SET_COLORS, container, particle).start(player);
			}
			if (button.isActionTypeEqual("Particle_Size")) {
				new SetNumbers(SET_PARTICLE_SIZE, container, particle).start(player);
			}
		}

		if (button.isActionTypeEqual("Back_button")) {
			new ParticleAnimation(container, "").menuOpen(player);

		}

		return false;
	}

	public org.broken.arrow.menu.button.manager.library.utility.MenuButton getActiveButton(MenuButtonData button) {
		if (canSetColor && (button.isActionTypeEqual("Particle_size") || button.isActionTypeEqual("Particle_colors")))
			return button.getActiveButton();
		if (isUsingMaterial && button.isActionTypeEqual("Material"))
			return button.getActiveButton();

		return null;
	}

	public enum Type {
		SET_COLORS,
		SET_DATA,
		SET_PARTICLE_SIZE
	}
}
