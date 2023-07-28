package org.brokenarrow.lootboxes.builder;

import com.google.common.base.Enums;
import org.broken.arrow.serialize.library.utility.converters.LocationSerializer;
import org.brokenarrow.lootboxes.untlity.Facing;
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

import static org.broken.arrow.serialize.library.utility.converters.ObjectConverter.getBoolean;
import static org.brokenarrow.lootboxes.untlity.CheckCastToClazz.castList;
import static org.brokenarrow.lootboxes.untlity.CheckCastToClazz.castMap;
import static org.brokenarrow.lootboxes.untlity.ConvetParticlesUntlity.*;


public final class ContainerDataBuilder implements ConfigurationSerializable {

	private final String lootTableLinked;
	private final Material icon;
	private final Material randomLootContainerItem;
	private final Facing randomLootContainerFacing;
	private final String displayname;
	private final List<String> lore;
	private final Map<Object, ParticleEffect> particleEffects;
	private final Map<Location, ContainerData> containerData;
	private final Map<String, KeysData> keysData;
	private final Location spawnLocation;
	private final boolean spawningContainerWithCooldown;
	private final boolean enchant;
	private final boolean randomSpawn;
	private final boolean showTitle;
	private final boolean containerShallGlow;
	private final boolean spawnContainerFromWorldCenter;
	private final long cooldown;
	private final int attempts;
	private final int minRadius;
	private final int maxRadius;
	private final Builder builder;


	private ContainerDataBuilder(final Builder builder) {

		this.lootTableLinked = builder.containerDataLinkedToLootTable;
		this.particleEffects = builder.particleEffects;
		this.icon = builder.icon;
		this.randomLootContainerItem = builder.randomLootContainerItem;
		this.randomLootContainerFacing = builder.randomLootContainerFacing;
		this.displayname = builder.displayname;
		this.lore = builder.lore;
		this.containerData = builder.containerData;
		this.keysData = builder.keysData;
		this.spawnLocation = builder.spawnLocation;
		this.spawningContainerWithCooldown = builder.spawningContainerWithCooldown;
		this.enchant = builder.enchant;
		this.cooldown = builder.cooldown;
		this.randomSpawn = builder.randomSpawn;
		this.showTitle = builder.showTitle;
		this.containerShallGlow = builder.containerShallGlow;
		this.spawnContainerFromWorldCenter = builder.spawnContainerFromWorldCenter;
		this.attempts = builder.attempts;
		this.minRadius = builder.minRadius;
		this.maxRadius = builder.maxRadius;
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

	public Material getRandomLootContainerItem() {
		return randomLootContainerItem;
	}

	public Facing getRandomLootContainerFacing() {
		return randomLootContainerFacing;
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

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public boolean isSpawningContainerWithCooldown() {
		return spawningContainerWithCooldown;
	}

	public boolean isEnchant() {
		return enchant;
	}

	public boolean isShowTitle() {
		return showTitle;
	}

	public boolean isContainerShallGlow() {
		return containerShallGlow;
	}

	public boolean isRandomSpawn() {
		return randomSpawn;
	}

	public boolean isSpawnContainerFromWorldCenter() {
		return spawnContainerFromWorldCenter;
	}

	public long getCooldown() {
		return cooldown;
	}

	public int getAttempts() {
		return attempts;
	}

	public int getMinRadius() {
		return minRadius;
	}

	public int getMaxRadius() {
		return maxRadius;
	}

	public Builder getBuilder() {
		return builder;
	}

	public static final class Builder {

		private String containerDataLinkedToLootTable;
		private Material icon;
		public Material randomLootContainerItem;
		public Facing randomLootContainerFacing;
		private String displayname;
		private List<String> lore;
		private Map<Object, ParticleEffect> particleEffects;
		private Map<Location, ContainerData> containerData;
		private Map<String, KeysData> keysData;
		private Location spawnLocation;
		private boolean spawningContainerWithCooldown;
		private boolean enchant;
		private boolean randomSpawn;
		public boolean showTitle;
		public boolean containerShallGlow;
		private boolean spawnContainerFromWorldCenter;
		private long cooldown;
		public int attempts;

		public int minRadius;
		public int maxRadius;

		public Builder setSpawnContainerFromWorldCenter(final boolean spawnContainerFromWorldCenter) {
			this.spawnContainerFromWorldCenter = spawnContainerFromWorldCenter;
			return this;
		}

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

		public Builder setRandomLootContainerItem(Material randomLootContainerItem) {
			this.randomLootContainerItem = randomLootContainerItem;
			return this;
		}

		public Builder setRandomLootContainerFacing(Facing randomLootContainerFacing) {
			this.randomLootContainerFacing = randomLootContainerFacing;
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

		public Builder setSpawnLocation(final Location spawnLocation) {
			this.spawnLocation = spawnLocation;
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

		public Builder setShowTitle(boolean showTitle) {
			this.showTitle = showTitle;
			return this;
		}

		public Builder setContainerShallGlow(boolean containerShallGlow) {
			this.containerShallGlow = containerShallGlow;
			return this;
		}

		public Builder setCooldown(final long cooldown) {
			this.cooldown = cooldown;
			return this;
		}

		public Builder setAttempts(int attempts) {
			this.attempts = attempts;
			return this;
		}

		public Builder setMinRadius(final int minRadius) {
			this.minRadius = minRadius;
			return this;
		}

		public Builder setMaxRadius(final int maxRadius) {
			this.maxRadius = maxRadius;
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
					", randonLootContainerItem=" + randomLootContainerItem +
					", randonLootContainerFaceing=" + randomLootContainerFacing +
					", displayname='" + displayname + '\'' +
					", lore=" + lore +
					", particleEffects=" + particleEffects +
					", containerData=" + containerData +
					", keysData=" + keysData +
					", spawningContainerWithCooldown=" + spawningContainerWithCooldown +
					", enchant=" + enchant +
					", randomSpawn=" + randomSpawn +
					", showTitel=" + showTitle +
					", contanerShallglow=" + containerShallGlow +
					", cooldown=" + cooldown +
					", attempts=" + attempts +
					'}';
		}
	}

	@Override
	public String toString() {
		return "ContainerDataBuilder{" +
				"lootTableLinked='" + lootTableLinked + '\'' +
				", icon=" + icon +
				", randonLootContainerItem=" + randomLootContainerItem +
				", randonLootContainerFaceing=" + randomLootContainerFacing +
				", displayname='" + displayname + '\'' +
				", lore=" + lore +
				", particleEffects=" + particleEffects +
				", containerData=" + containerData +
				", keysData=" + keysData +
				", spawningContainerWithCooldown=" + spawningContainerWithCooldown +
				", enchant=" + enchant +
				", randomSpawn=" + randomSpawn +
				", showTitel=" + showTitle +
				", contanerShallglow=" + containerShallGlow +
				", cooldown=" + cooldown +
				", attempts=" + attempts +
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
		keysData.put("Random_loot_container", this.randomLootContainerItem + "");
		keysData.put("Random_loot_faceing", this.randomLootContainerFacing + "");
		keysData.put("Display_name", this.displayname);
		keysData.put("Lore", this.lore);
		keysData.put("Particle_effect", this.particleEffects.entrySet().stream().collect(Collectors.toMap(effectEntry -> effectEntry.getKey().toString(), Map.Entry::getValue)));
		keysData.put("Spawn_with_cooldown", this.spawningContainerWithCooldown);
		keysData.put("Enchant", this.enchant);
		keysData.put("Random_spawn", this.randomSpawn);
		keysData.put("Cooldown", this.cooldown);
		keysData.put("Random_loot_titel", this.showTitle);
		keysData.put("Random_loot_glow", this.containerShallGlow);
		keysData.put("Keys", this.keysData);
		keysData.put("Attempts", this.attempts);
		keysData.put("Spawn_world_center", this.spawnContainerFromWorldCenter);
		keysData.put("Min_radius", this.minRadius);
		keysData.put("Max_radius", this.maxRadius);
		keysData.put("Spawn-point", LocationSerializer.serializeLoc(spawnLocation));
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
		final boolean randomSpawn = getBoolean(map.get("Random_spawn"));
		final long cooldown = (Integer) map.get("Cooldown");
		final Material random_loot_container = Material.getMaterial(String.valueOf(map.get("Random_loot_container")));
		final String random_loot_facing = (String) map.get("Random_loot_faceing");
		final boolean random_loot_title = (boolean) map.getOrDefault("Random_loot_titel", false);
		final boolean random_loot_glow = (boolean) map.getOrDefault("Random_loot_glow", false);
		final int attempts = (Integer) map.getOrDefault("Attempts", 1);
		final boolean spawnContainerFromCenter = (boolean) map.getOrDefault("Spawn_world_center", false);
		final int minRadius = (int) map.getOrDefault("Min_radius", spawnContainerFromCenter ? 100 : 10);
		final int maxRadius = (int) map.getOrDefault("Max_radius", spawnContainerFromCenter ? 1500 : 80);
		Facing blockFace = null;
		if (random_loot_facing != null)
			blockFace = Enums.getIfPresent(Facing.class, random_loot_facing).orNull();
		if (blockFace == null)
			blockFace = Facing.WEST;

		Valid.checkNotNull(icon, "Material is null for this container");
		Material material = Material.getMaterial(icon);
		if (material == null) {
			material = Material.CHEST;
		}

		final ContainerDataBuilder.Builder builder = new ContainerDataBuilder.Builder()
				.setContainerDataLinkedToLootTable(lootTableLinked)
				.setSpawningContainerWithCooldown(spawningContainerWithCooldown)
				.setCooldown(cooldown)
				.setParticleEffects(particles != null ? particles.entrySet().stream().collect(Collectors.toMap(effectEntry -> getParticleOrEffect(effectEntry.getKey()), Map.Entry::getValue)) :
						convertToParticleEffect(particleEffect == null || particleEffect.isEmpty() ? convertParticleEffectList(particleEffectList) : convertStringList(particleEffect)))
				.setEnchant(enchant)
				.setIcon(material)
				.setRandomLootContainerItem(random_loot_container != null ? random_loot_container : Material.CHEST)
				.setRandomLootContainerFacing(blockFace)
				.setDisplayname(displayName)
				.setLore(lore)
				.setContainerShallGlow(random_loot_glow)
				.setShowTitle(random_loot_title)
				.setRandomSpawn(randomSpawn)
				.setContainerData(containers)
				.setKeysData(keys)
				.setAttempts(attempts)
				.setSpawnContainerFromWorldCenter(spawnContainerFromCenter)
				.setMinRadius(minRadius)
				.setMaxRadius(maxRadius)
				.setSpawnLocation(LocationSerializer.deserializeLoc(map.get("Spawn-point")));

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
