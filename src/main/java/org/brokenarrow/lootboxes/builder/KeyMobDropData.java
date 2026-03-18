package org.brokenarrow.lootboxes.builder;

import org.bukkit.entity.EntityType;

import java.util.List;

public final class KeyMobDropData {

	private final int chance;
	private final int minimum;
	private final int maximum;
	private final String keyName;
	private final String lootContainerKey;
	private final List<EntityType> entityTypes;
	private final Builder builder;

	private KeyMobDropData(Builder builder) {
		this.chance = builder.chance;
		this.minimum = builder.minimum;
		this.maximum = builder.maximum;
		this.keyName = builder.keyName;
		this.lootContainerKey = builder.lootContainerKey;
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

	public String getLootContainerKey() {
		return lootContainerKey;
	}

	public List<EntityType> getEntityTypes() {
		return entityTypes;
	}

	public Builder getBuilder() {
		return builder;
	}

	public static class Builder {
		private int chance = 5;
		private int minimum = 1;
		private int maximum = 2;
		private String keyName;
		private String lootContainerKey;
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

		public Builder setLootContainerKey(String containerDataFileName) {
			this.lootContainerKey = containerDataFileName;
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

	@Override
	public String toString() {
		return "KeyMobDropData{" +
				"chance=" + chance +
				", minimum=" + minimum +
				", maximum=" + maximum +
				", keyName='" + keyName + '\'' +
				", lootContainerKey='" + lootContainerKey + '\'' +
				", entityTypes=" + entityTypes +
				", builder=" + builder +
				'}';
	}
}
