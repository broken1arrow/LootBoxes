package org.brokenarrow.lootboxes.builder;

import java.util.List;

public final class SettingsData {
	private final String language;
	private final String linkToolItem;
	private final String linkToolDisplayName;
	private final String placeContainerDisplayName;
	private final List<String> linkToolLore;
	private final List<String> placeContainerLore;
	private final int amountOfBlocksBelowSurface;
	private final int blocksAwayFromPlayer;
	private final int blocksBetweenContainers;
	private final int increase;
	private final int decrease;
	private final boolean spawnOnSurface;
	private final boolean warnBeforeSaveWithMetadata;
	private final boolean saveMetadataOnItem;
	private final boolean randomContainerSpawn;
	private final boolean removeContainerWhenPlayerClose;
	private final boolean debug;
	private final boolean singleMenuFile;
	private final Builder builder;

	private SettingsData(Builder builder) {
		this.language = builder.language;
		this.linkToolItem = builder.linkToolItem;
		this.linkToolDisplayName = builder.linkToolDisplayName;
		this.linkToolLore = builder.linkToolLore;
		this.placeContainerDisplayName = builder.placeContainerDisplayName;
		this.placeContainerLore = builder.placeContainerLore;
		this.amountOfBlocksBelowSurface = builder.amountOfBlocksBelowSurface;
		this.spawnOnSurface = builder.spawnOnSurface;
		this.warnBeforeSaveWithMetadata = builder.warnBeforeSaveWithMetadata;
		this.saveMetadataOnItem = builder.saveMetadataOnItem;
		this.randomContainerSpawn = builder.randomContainerSpawn;
		this.removeContainerWhenPlayerClose = builder.removeContainerWhenPlayerClose;
		this.blocksAwayFromPlayer = builder.blocksAwayFromPlayer;
		this.blocksBetweenContainers = builder.blocksBetweenContainers;
		this.increase = builder.increase;
		this.decrease = builder.decrease;
		this.debug = builder.debug;
		this.singleMenuFile = builder.singleMenuFile;
		this.builder = builder;
	}

	public String getLanguage() {
		return language;
	}

	public String getLinkToolItem() {
		return linkToolItem;
	}

	public String getLinkToolDisplayName() {
		return linkToolDisplayName;
	}

	public List<String> getLinkToolLore() {
		return linkToolLore;
	}

	public String getPlaceContainerDisplayName() {
		return placeContainerDisplayName;
	}

	public List<String> getPlaceContainerLore() {
		return placeContainerLore;
	}

	/**
	 * Get max below Surface. Defult will it spawn as higest 1 block below highest block.
	 *
	 * @return amount of blocks below.
	 */
	public int getAmountOfBlocksBelowSurface() {
		return amountOfBlocksBelowSurface;
	}

	public int getBlocksAwayFromPlayer() {
		return blocksAwayFromPlayer;
	}

	public int getBlocksBetweenContainers() {
		return blocksBetweenContainers;
	}

	public int getIncrease() {
		return increase;
	}

	public int getDecrease() {
		return decrease;
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

	public boolean isRandomContainerSpawn() {
		return randomContainerSpawn;
	}

	public boolean isRemoveContainerWhenPlayerClose() {
		return removeContainerWhenPlayerClose;
	}

	public boolean isDebug() {
		return debug;
	}

	public boolean isSingleMenuFile() {
		return singleMenuFile;
	}

	public Builder getBuilder() {
		return builder;
	}

	public static class Builder {


		private String language;
		private String linkToolItem;
		private String linkToolDisplayName;
		private List<String> linkToolLore;
		private String placeContainerDisplayName;
		private List<String> placeContainerLore;
		private int amountOfBlocksBelowSurface;
		private int blocksAwayFromPlayer;
		private int blocksBetweenContainers;
		private int increase;
		private int decrease;
		private boolean spawnOnSurface;
		private boolean warnBeforeSaveWithMetadata;
		private boolean saveMetadataOnItem;
		private boolean randomContainerSpawn;
		private boolean removeContainerWhenPlayerClose;
		public boolean debug;
		private boolean singleMenuFile;

		public Builder setLanguage(String language) {
			this.language = language;
			return this;
		}

		public Builder setLinkToolItem(String linkToolItem) {
			this.linkToolItem = linkToolItem;
			return this;
		}

		public Builder setLinkToolDisplayName(String linkToolDisplayName) {
			this.linkToolDisplayName = linkToolDisplayName;
			return this;
		}

		public Builder setLinkToolLore(List<String> linkToolLore) {
			this.linkToolLore = linkToolLore;
			return this;
		}

		public Builder setPlaceContainerDisplayName(String placeContainerDisplayName) {
			this.placeContainerDisplayName = placeContainerDisplayName;
			return this;
		}

		public Builder setPlaceContainerLore(List<String> placeContainerLore) {
			this.placeContainerLore = placeContainerLore;
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

		public Builder setBlocksAwayFromPlayer(int blocksAwayFromPlayer) {
			this.blocksAwayFromPlayer = blocksAwayFromPlayer;
			return this;
		}

		public Builder setBlocksBetweenContainers(int blocksBetweenContainers) {
			this.blocksBetweenContainers = blocksBetweenContainers;
			return this;
		}

		public Builder setIncrease(int increase) {
			this.increase = increase;
			return this;
		}

		public Builder setDecrease(int decrease) {
			this.decrease = decrease;
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

		public Builder setRandomContainerSpawn(boolean randomContainerSpawn) {
			this.randomContainerSpawn = randomContainerSpawn;
			return this;
		}

		public Builder setRemoveContainerWhenPlayerClose(boolean removeContainerWhenPlayerClose) {
			this.removeContainerWhenPlayerClose = removeContainerWhenPlayerClose;
			return this;
		}

		public Builder setDebug(boolean debug) {
			this.debug = debug;
			return this;
		}

		public Builder setSingleMenuFile(final boolean singleMenuFile) {
			this.singleMenuFile = singleMenuFile;
			return this;
		}

		public SettingsData build() {
			return new SettingsData(this);
		}

	}
}
