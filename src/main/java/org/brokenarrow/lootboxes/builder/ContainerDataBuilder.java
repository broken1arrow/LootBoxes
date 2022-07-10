package org.brokenarrow.lootboxes.builder;

import org.brokenarrow.lootboxes.untlity.errors.Valid;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.CheckCastToClazz.castList;
import static org.brokenarrow.lootboxes.untlity.CheckCastToClazz.castMap;
import static org.brokenarrow.lootboxes.untlity.ConvetParticlesUntlity.*;

public final class ContainerDataBuilder implements ConfigurationSerializable {

	private final String lootTableLinked;
	private final Material icon;
	private final String displayname;
	private final List<String> lore;
	private final Map<Object, ParticleEffect> particleEffects;
	private final Map<Location, ContainerData> containerData;
	private final Map<String, KeysData> keysData;
	private final boolean spawningContainerWithCooldown;
	private final boolean enchant;
	private final boolean randomSpawn;
	private final long cooldown;
	private final Builder builder;

	private ContainerDataBuilder(final Builder builder) {

		this.lootTableLinked = builder.containerDataLinkedToLootTable;
		this.particleEffects = builder.particleEffects;
		this.icon = builder.icon;
		this.displayname = builder.displayname;
		this.lore = builder.lore;
		this.containerData = builder.containerData;
		this.keysData = builder.keysData;
		this.spawningContainerWithCooldown = builder.spawningContainerWithCooldown;
		this.enchant = builder.enchant;
		this.cooldown = builder.cooldown;
		this.randomSpawn = builder.randomSpawn;
		this.builder = builder;
	}

	public String getLootTableLinked() {
		return lootTableLinked;
	}

	@NotNull
	public Map<Object, ParticleEffect> getParticleEffects() {
		return particleEffects;
	}

	@Nullable
	public ParticleEffect getParticleEffect(final Object o) {
		if (o == null) return null;
		if (this.getParticleEffects().isEmpty()) return null;

		return this.getParticleEffects().get(o);
	}

	public Material getIcon() {
		return icon;
	}

	public String getDisplayname() {
		return displayname;
	}

	public List<String> getLore() {
		return lore;
	}

	public Map<Location, ContainerData> getLinkedContainerData() {
		return containerData;
	}

	public Map<String, KeysData> getKeysData() {
		return keysData;
	}

	public boolean isSpawningContainerWithCooldown() {
		return spawningContainerWithCooldown;
	}

	public boolean isEnchant() {
		return enchant;
	}

	public boolean isRandomSpawn() {
		return randomSpawn;
	}

	public long getCooldown() {
		return cooldown;
	}

	public Builder getBuilder() {
		return builder;
	}


	public static final class Builder {

		private String containerDataLinkedToLootTable;
		private Material icon;
		private String displayname;
		private List<String> lore;
		private Map<Object, ParticleEffect> particleEffects;
		private Map<Location, ContainerData> containerData;
		private Map<String, KeysData> keysData;
		private boolean spawningContainerWithCooldown;
		private boolean enchant;
		private boolean randomSpawn;
		private long cooldown;

		public Builder setContainerDataLinkedToLootTable(final String ContainerDataLinkedToLootTable) {
			this.containerDataLinkedToLootTable = ContainerDataLinkedToLootTable;
			return this;
		}

		public Builder setParticleEffects(final Map<Object, ParticleEffect> particleEffects) {
			this.particleEffects = particleEffects;

			return this;
		}

		public Builder setIcon(final Material icon) {
			this.icon = icon;
			return this;
		}

		public Builder setDisplayname(final String displayname) {
			this.displayname = displayname;
			return this;
		}

		public Builder setLore(final List<String> lore) {
			this.lore = lore;
			return this;
		}

		public Builder setContainerData(final Map<Location, ContainerData> containerData) {
			this.containerData = containerData;
			return this;
		}

		public Builder setKeysData(final Map<String, KeysData> keysData) {
			this.keysData = keysData;
			return this;
		}

		public Builder setSpawningContainerWithCooldown(final boolean spawningContainerWithCooldown) {
			this.spawningContainerWithCooldown = spawningContainerWithCooldown;
			return this;
		}

		public Builder setEnchant(final boolean enchant) {
			this.enchant = enchant;
			return this;
		}

		public Builder setRandomSpawn(final boolean randomSpawn) {
			this.randomSpawn = randomSpawn;
			return this;
		}

		public Builder setCooldown(final long cooldown) {
			this.cooldown = cooldown;
			return this;
		}

		public ContainerDataBuilder build() {
			return new ContainerDataBuilder(this);
		}

		@Override
		public String toString() {
			return "Builder{" +
					"containerDataLinkedToLootTable='" + containerDataLinkedToLootTable + '\'' +
					", icon=" + icon +
					", displayname='" + displayname + '\'' +
					", lore=" + lore +
					", particleEffect=" + particleEffects +
					", containerData=" + containerData +
					", keysData=" + keysData +
					", spawning=" + spawningContainerWithCooldown +
					", enchant=" + enchant +
					", cooldown=" + cooldown +
					'}';
		}
	}

	@Override
	public String toString() {
		return "ContainerDataBuilder{" +
				"lootTableLinked='" + lootTableLinked + '\'' +
				", icon=" + icon +
				", displayname='" + displayname + '\'' +
				", lore=" + lore +
				", particleEffect=" + particleEffects +
				", containerData=" + containerData +
				", keysData=" + keysData +
				", spawning=" + spawningContainerWithCooldown +
				", enchant=" + enchant +
				", cooldown=" + cooldown +
				", builder=" + builder +
				'}';
	}

	/**
	 * Creates a Map representation of this class.
	 * <p>
	 * This class must provide a method to restore this class, as defined in
	 * the {@link ConfigurationSerializable} interface javadocs.
	 *
	 * @return Map containing the current state of this class
	 */
	@NotNull
	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> keysData = new LinkedHashMap<>();
		keysData.put("LootTable_linked", this.lootTableLinked);
		keysData.put("Icon", this.icon + "");
		keysData.put("Display_name", this.displayname);
		keysData.put("Lore", this.lore);
		keysData.put("Particle_effect", this.particleEffects.entrySet().stream().collect(Collectors.toMap(k -> k.getKey().toString(), Map.Entry::getValue)));
		keysData.put("Spawn_with_cooldown", this.spawningContainerWithCooldown);
		keysData.put("Enchant", this.enchant);
		keysData.put("Random_spawn", this.randomSpawn);
		keysData.put("Cooldown", this.cooldown);
		keysData.put("Keys", this.keysData);
		keysData.put("Containers", this.containerData);
		return keysData;
	}

	public static ContainerDataBuilder deserialize(final Map<String, Object> map) {

		final String lootTableLinked = (String) map.get("LootTable_linked");
		final String icon = (String) map.get("Icon");
		String displayName = (String) map.getOrDefault("Display_name", "&6Loot Chest");
		if (displayName == null)
			displayName = "&6Loot Chest";
		final List<String> lore = castList((List<?>) map.get("Lore"), String.class);
		final Object particleEffects = map.get("Particle_effect");
		List<String> particleEffect = null;
		Map<Object, ParticleEffect> particles = null;
		List<ParticleEffect> particleEffectList = null;
		if (particleEffects instanceof List) {
			particleEffect = castList((List<?>) particleEffects, String.class);
			if (particleEffect == null || particleEffect.isEmpty())
				particleEffectList = castList((List<?>) particleEffects, ParticleEffect.class);
		}

		if (particleEffects instanceof Map && (particleEffectList == null || particleEffectList.isEmpty()))
			particles = castMap((Map<?, ?>) particleEffects, Object.class, ParticleEffect.class);

		final Map<Location, ContainerData> containers = castMap((Map<?, ?>) map.get("Containers"), Location.class, ContainerData.class);
		final Map<String, KeysData> keys = castMap((Map<?, ?>) map.get("Keys"), String.class, KeysData.class);
		final boolean spawningContainerWithCooldown = (boolean) map.get("Spawn_with_cooldown");
		final boolean enchant = (boolean) map.get("Enchant");
		final boolean randomSpawn = (boolean) map.get("Random_spawn");
		final long cooldown = (Integer) map.get("Cooldown");
		Valid.checkNotNull(icon, "Material is null for this container");
		Material material = Material.getMaterial(icon);
		if (material == null)
			material = Material.CHEST;

		final ContainerDataBuilder.Builder builder = new ContainerDataBuilder.Builder()
				.setContainerDataLinkedToLootTable(lootTableLinked)
				.setSpawningContainerWithCooldown(spawningContainerWithCooldown)
				.setCooldown(cooldown)
				.setParticleEffects(particles != null ? particles.entrySet().stream().collect(Collectors.toMap(k -> getParticleOrEffect(k.getKey()), Map.Entry::getValue)) :
						convertToParticleEffect(particleEffect == null || particleEffect.isEmpty() ? convertParticleEffectList(particleEffectList) : convertStringList(particleEffect)))
				.setEnchant(enchant)
				.setIcon(material)
				.setDisplayname(displayName)
				.setLore(lore)
				.setRandomSpawn(randomSpawn)
				.setContainerData(containers)
				.setKeysData(keys);

		return builder.build();
	}

	public static Map<Object, ParticleEffect> convertToParticleEffect(List<?> objectList) {
		Map<Object, ParticleEffect> map = new HashMap<>();
		if (objectList == null || objectList.isEmpty()) return map;

		for (final Object particle : objectList) {
			final ParticleEffect.Builder builder = new ParticleEffect.Builder();
			if (particle instanceof Particle) {

				final Particle part = (Particle) particle;
				builder.setParticle(part).setDataType(part.getDataType());
			} else if (particle instanceof Effect) {

				final Effect part = (Effect) particle;
				builder.setEffect(part).setDataType(part.getData());
			}
			if (particle instanceof Particle || particle instanceof Effect)
				map.put(particle, builder.build());
		}

		return map;
	}

}
