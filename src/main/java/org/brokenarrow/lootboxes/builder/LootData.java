package org.brokenarrow.lootboxes.builder;

import org.bukkit.Material;

public final class LootData {

	private final int chance;
	private final int minimum;
	private final int maximum;
	private final Material material;
	private final String itemdataPath;
	private final String itemdataFileName;
	private final boolean haveMetadata;
	private final Builder builder;

	public LootData(Builder builder) {
		this.chance = builder.chance;
		this.minimum = builder.minimum;
		this.maximum = builder.maximum;
		this.material = builder.material;
		this.itemdataPath = builder.itemdataPath;
		this.itemdataFileName = builder.itemdataFileName;
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

	public String getItemdataPath() {
		return itemdataPath;
	}

	public String getItemdataFileName() {
		return itemdataFileName;
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
		private String itemdataPath;
		private String itemdataFileName;
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

		public Builder setItemdataPath(String itemdataPath) {
			this.itemdataPath = itemdataPath;
			return this;
		}

		public Builder setItemdataFileName(String itemdataFileName) {
			this.itemdataFileName = itemdataFileName;
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
