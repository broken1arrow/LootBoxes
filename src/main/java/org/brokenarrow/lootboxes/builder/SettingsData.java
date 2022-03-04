package org.brokenarrow.lootboxes.builder;

public final class SettingsData {

	private final int amountOfBlocksBelowSurface;
	private final boolean spawnOnSurface;
	private final Builder builder;

	private SettingsData(Builder builder) {
		this.amountOfBlocksBelowSurface = builder.amountOfBlocksBelowSurface;
		this.spawnOnSurface = builder.spawnOnSurface;
		this.builder = builder;
	}

	/**
	 * Get max below Surface. Defult will it spawn as higest 1 block below highest block.
	 *
	 * @return amount of blocks below.
	 */
	public int getAmountOfBlocksBelowSurface() {
		return amountOfBlocksBelowSurface;
	}

	/**
	 * Get max below Surface. Defult will it spawn as higest 1 block below highest block.
	 *
	 * @return true if it spawn on Surface.
	 */
	public boolean isSpawnOnSurface() {
		return spawnOnSurface;
	}

	public Builder getBuilder() {
		return builder;
	}

	public static class Builder {

		private int amountOfBlocksBelowSurface;
		private boolean spawnOnSurface;

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

		public SettingsData build() {
			return new SettingsData(this);
		}

	}
}
