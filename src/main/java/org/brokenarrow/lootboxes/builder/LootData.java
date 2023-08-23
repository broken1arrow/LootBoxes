package org.brokenarrow.lootboxes.builder;

import org.bukkit.Material;

public final class LootData {

	private final int chance;
	private final int minimum;
	private final int maximum;
	private final Material material;
	private final String itemDataPath;
	private final String lootTableName;
	private final boolean haveMetadata;
	private final Builder builder;

	public LootData(Builder builder) {
		this.chance = builder.chance;
		this.minimum = builder.minimum;
		this.maximum = builder.maximum;
		this.material = builder.material;
		this.itemDataPath = builder.itemDataPath;
		this.lootTableName = builder.lootTableName;
		this.haveMetadata = builder.haveMetadata;
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

	public Material getMaterial() {
		return material;
	}

	public String getItemDataPath() {
		return itemDataPath;
	}

	public String getLootTableName() {
		return lootTableName;
	}

	public boolean isHaveMetadata() {
		return haveMetadata;
	}

	public Builder getBuilder() {
		return builder;
	}

	public static class Builder {
		private int chance;
		private int minimum;
		private int maximum;
		private Material material;
		private String itemDataPath;
		private String lootTableName;
		private boolean haveMetadata;

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

		public Builder setMaterial(Material material) {
			this.material = material;
			return this;
		}

		public Builder setItemDataPath(String itemDataPath) {
			this.itemDataPath = itemDataPath;
			return this;
		}

		public Builder setLootTableName(String lootTableName) {
			this.lootTableName = lootTableName;
			return this;
		}

		public Builder setHaveMetadata(boolean haveMetadata) {
			this.haveMetadata = haveMetadata;
			return this;
		}

		public LootData build() {
			return new LootData(this);
		}
	}
}
