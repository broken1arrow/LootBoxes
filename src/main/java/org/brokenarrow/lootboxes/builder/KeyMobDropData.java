package org.brokenarrow.lootboxes.builder;

import org.bukkit.entity.EntityType;

import java.util.List;

public final class KeyMobDropData {

	private final int chance;
	private final int minimum;
	private final int maximum;
	private final String keyName;
	private final String containerDataFileName;
	private final List<EntityType> entityTypes;
	private final Builder builder;

	private KeyMobDropData(Builder builder) {
		this.chance = builder.chance;
		this.minimum = builder.minimum;
		this.maximum = builder.maximum;
		this.keyName = builder.keyName;
		this.containerDataFileName = builder.containerDataFileName;
		this.entityTypes = builder.entityTypes;
		this.builder = builder;
	}

	public int getChance() {
		return chance;
	}

	public int getMinimum() {
		return minimum;
	}

	public int getMaximum() {
		return maximum;
	}


	public String getKeyName() {
		return keyName;
	}

	public String getContainerDataFileName() {
		return containerDataFileName;
	}

	public List<EntityType> getEntityTypes() {
		return entityTypes;
	}

	public Builder getBuilder() {
		return builder;
	}

	public static class Builder {
		private int chance;
		private int minimum;
		private int maximum;
		private String keyName;
		private String containerDataFileName;
		public List<EntityType> entityTypes;

		public Builder setChance(int chance) {
			this.chance = chance;
			return this;
		}

		public Builder setMinimum(int minimum) {
			this.minimum = minimum;
			return this;
		}

		public Builder setMaximum(int maximum) {
			this.maximum = maximum;
			return this;
		}

		public Builder setKeyName(String keyName) {
			this.keyName = keyName;
			return this;
		}

		public Builder setContainerDataFileName(String containerDataFileName) {
			this.containerDataFileName = containerDataFileName;
			return this;
		}

		public Builder setEntityTypes(List<EntityType> entityTypes) {
			this.entityTypes = entityTypes;
			return this;
		}

		public KeyMobDropData build() {
			return new KeyMobDropData(this);
		}
	}
}
