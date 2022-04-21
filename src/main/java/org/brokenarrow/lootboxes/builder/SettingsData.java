package org.brokenarrow.lootboxes.builder;

public final class SettingsData {
	private final String language;
	private final int amountOfBlocksBelowSurface;
	private final int increse;
	private final int decrese;
	private final boolean spawnOnSurface;
	private final boolean warnBeforeSaveWithMetadata;
	private final boolean saveMetadataOnItem;
	private final boolean randomContinerSpawn;
	private final Builder builder;

	private SettingsData(Builder builder) {
		this.language = builder.language;
		this.amountOfBlocksBelowSurface = builder.amountOfBlocksBelowSurface;
		this.spawnOnSurface = builder.spawnOnSurface;
		this.warnBeforeSaveWithMetadata = builder.warnBeforeSaveWithMetadata;
		this.saveMetadataOnItem = builder.saveMetadataOnItem;
		this.randomContinerSpawn = builder.randomContinerSpawn;
		this.increse = builder.increse;
		this.decrese = builder.decrese;

		this.builder = builder;
	}

	public String getLanguage() {
		return language;
	}

	/**
	 * Get max below Surface. Defult will it spawn as higest 1 block below highest block.
	 *
	 * @return amount of blocks below.
	 */
	public int getAmountOfBlocksBelowSurface() {
		return amountOfBlocksBelowSurface;
	}

	public int getIncrese() {
		return increse;
	}

	public int getDecrese() {
		return decrese;
	}

	/**
	 * Get max below Surface. Defult will it spawn as higest 1 block below highest block.
	 *
	 * @return true if it spawn on Surface.
	 */
	public boolean isSpawnOnSurface() {
		return spawnOnSurface;
	}

	public boolean isWarnBeforeSaveWithMetadata() {
		return warnBeforeSaveWithMetadata;
	}

	public boolean isSaveMetadataOnItem() {
		return saveMetadataOnItem;
	}

	public boolean isRandomContinerSpawn() {
		return randomContinerSpawn;
	}

	public Builder getBuilder() {
		return builder;
	}

	public static class Builder {

		private String language;
		private int amountOfBlocksBelowSurface;
		private int increse;
		private int decrese;
		private boolean spawnOnSurface;
		private boolean warnBeforeSaveWithMetadata;
		private boolean saveMetadataOnItem;
		private boolean randomContinerSpawn;


		public Builder setLanguage(String language) {
			this.language = language;
			return this;
		}

		/**
		 * Set max below Surface. Defult will it spawn as higest 1 block below highest block.
		 *
		 * @param amountOfBlocksBelowSurface set amount block below it shall spawn as max hight.
		 * @return this class.
		 */
		public Builder setAmountOfBlocksBelowSurface(int amountOfBlocksBelowSurface) {
			this.amountOfBlocksBelowSurface = amountOfBlocksBelowSurface;
			return this;
		}

		public Builder setIncrese(int increse) {
			this.increse = increse;
			return this;
		}

		public Builder setDecrese(int decrese) {
			this.decrese = decrese;
			return this;
		}

		/**
		 * If it shall spawn on Surface or below Surface.
		 *
		 * @param spawnOnSurface true if shall spawn on surface.
		 * @return this class.
		 */
		public Builder setSpawnOnSurface(boolean spawnOnSurface) {
			this.spawnOnSurface = spawnOnSurface;
			return this;
		}

		/**
		 * If it shall say/warn it is metadata on item.
		 *
		 * @param warnBeforeSaveWithMetadata
		 * @return
		 */
		public Builder setWarnBeforeSaveWithMetadata(boolean warnBeforeSaveWithMetadata) {
			this.warnBeforeSaveWithMetadata = warnBeforeSaveWithMetadata;
			return this;
		}

		/**
		 * If it shall save metadata.
		 *
		 * @param saveMetadataOnItem
		 * @return
		 */
		public Builder setSaveMetadataOnItem(boolean saveMetadataOnItem) {
			this.saveMetadataOnItem = saveMetadataOnItem;
			return this;
		}

		public Builder setRandomContinerSpawn(boolean randomContinerSpawn) {
			this.randomContinerSpawn = randomContinerSpawn;
			return this;
		}

		public SettingsData build() {
			return new SettingsData(this);
		}

	}
}
