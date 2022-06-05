package org.brokenarrow.lootboxes.builder;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public final class ContainerDataBuilder {

	private final String lootTableLinked;
	private final Material icon;
	private final String displayname;
	private final List<String> lore;
	private final List<String> particleEffect;
	private final Map<Location, ContainerData> containerData;
	private final Map<String, KeysData> keysData;
	private final boolean spawningContainerWithCooldown;
	private final boolean enchant;
	private final boolean randomSpawn;
	private final long cooldown;
	private final Builder builder;

	private ContainerDataBuilder(Builder builder) {

		this.lootTableLinked = builder.containerDataLinkedToLootTable;
		this.particleEffect = builder.particleEffect;
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

	public List<String> getParticleEffects() {
		return particleEffect;
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

	@Override
	public String toString() {
		return "ContainerDataBuilder{" +
				"lootTableLinked='" + lootTableLinked + '\'' +
				", icon=" + icon +
				", displayname='" + displayname + '\'' +
				", lore=" + lore +
				", particleEffect=" + particleEffect +
				", containerData=" + containerData +
				", keysData=" + keysData +
				", spawning=" + spawningContainerWithCooldown +
				", enchant=" + enchant +
				", cooldown=" + cooldown +
				", builder=" + builder +
				'}';
	}

	public static final class Builder {

		private String containerDataLinkedToLootTable;
		private Material icon;
		private String displayname;
		private List<String> lore;
		private List<String> particleEffect;
		private Map<Location, ContainerData> containerData;
		private Map<String, KeysData> keysData;
		private boolean spawningContainerWithCooldown;
		private boolean enchant;
		private boolean randomSpawn;
		private long cooldown;

		public Builder setContainerDataLinkedToLootTable(String ContainerDataLinkedToLootTable) {
			this.containerDataLinkedToLootTable = ContainerDataLinkedToLootTable;
			return this;
		}

		public Builder setParticleEffect(List<String> particleEffect) {
			this.particleEffect = particleEffect;
			return this;
		}

		public Builder setIcon(Material icon) {
			this.icon = icon;
			return this;
		}

		public Builder setDisplayname(String displayname) {
			this.displayname = displayname;
			return this;
		}

		public Builder setLore(List<String> lore) {
			this.lore = lore;
			return this;
		}

		public Builder setContainerData(Map<Location, ContainerData> containerData) {
			this.containerData = containerData;
			return this;
		}

		public Builder setKeysData(Map<String, KeysData> keysData) {
			this.keysData = keysData;
			return this;
		}

		public Builder setSpawningContainerWithCooldown(boolean spawningContainerWithCooldown) {
			this.spawningContainerWithCooldown = spawningContainerWithCooldown;
			return this;
		}

		public Builder setEnchant(boolean enchant) {
			this.enchant = enchant;
			return this;
		}

		public Builder setRandomSpawn(boolean randomSpawn) {
			this.randomSpawn = randomSpawn;
			return this;
		}

		public Builder setCooldown(long cooldown) {
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
					", particleEffect=" + particleEffect +
					", containerData=" + containerData +
					", keysData=" + keysData +
					", spawning=" + spawningContainerWithCooldown +
					", enchant=" + enchant +
					", cooldown=" + cooldown +
					'}';
		}
	}

}
