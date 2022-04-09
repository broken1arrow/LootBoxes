package org.brokenarrow.lootboxes.builder;

import com.google.common.base.Enums;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.checkListForPlaceholdersAndTranslate;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholders;
import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public final class ContainerDataBuilder {

	private final String lootTableLinked;
	private final Material icon;
	private final String displayname;
	private final List<String> lore;
	private final List<String> particleEffect;
	private final Map<Location, ContainerData> containerData;
	private final Map<String, KeysData> keysData;
	private final boolean spawning;
	private final boolean enchant;
	private final long cooldown;
	private final Builder builder;

	public ContainerDataBuilder(Builder builder) {

		this.lootTableLinked = builder.containerDataLinkedToLootTable;
		this.particleEffect = builder.particleEffect;
		this.icon = builder.icon;
		this.displayname = builder.displayname;
		this.lore = builder.lore;
		this.containerData = builder.containerData;
		this.keysData = builder.keysData;
		this.spawning = builder.spawning;
		this.enchant = builder.enchant;
		this.cooldown = builder.cooldown;
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

	public boolean isSpawning() {
		return spawning;
	}

	public boolean isEnchant() {
		return enchant;
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
		private List<String> particleEffect;
		private Map<Location, ContainerData> containerData;
		private Map<String, KeysData> keysData;
		private boolean spawning;
		private boolean enchant;
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

		public Builder setSpawning(boolean spawning) {
			this.spawning = spawning;
			return this;
		}

		public Builder setEnchant(boolean enchant) {
			this.enchant = enchant;
			return this;
		}

		public Builder setCooldown(long cooldown) {
			this.cooldown = cooldown;
			return this;
		}

		public ContainerDataBuilder build() {
			return new ContainerDataBuilder(this);
		}
	}

	public static final class ContainerData {

		private final BlockFace facing;
		private final Material containerType;

		public ContainerData(BlockFace facing, Material containerType) {
			this.facing = facing;
			this.containerType = containerType;
		}

		public ContainerData(String facing, String containerType) {
			this.facing = addBlockFace(facing);
			this.containerType = addMatrial(containerType);
		}


		public BlockFace addBlockFace(final String facing) {
			checkNotNull(facing, "This block face are null.");
			BlockFace blockFace = Enums.getIfPresent(BlockFace.class, facing).orNull();
			checkNotNull(blockFace, "This " + facing + " are not valid");

			return blockFace;
		}

		public Material addMatrial(final String containerType) {
			checkNotNull(containerType, "This containerType are null.");
			Material material = Enums.getIfPresent(Material.class, containerType).orNull();
			checkNotNull(material, "This " + containerType + " are not valid");

			return material;
		}

		public BlockFace getFacing() {
			return facing;
		}

		public Material getContainerType() {
			return containerType;
		}
	}

	public static final class KeysData {
		private final String keyName;
		private final String displayName;
		private final String lootTableLinked;
		private final int amountNeeded;
		private final Material itemType;
		private final List<String> lore;

		public KeysData(String keyName, String displayName, String lootTableLinked, int amountNeeded, Material itemType, List<String> lore) {
			this.keyName = keyName;
			this.amountNeeded = amountNeeded;
			this.itemType = itemType;
			this.lootTableLinked = lootTableLinked;
			this.displayName = translatePlaceholdersDisplayName(displayName, keyName, lootTableLinked.length() > 0 ? lootTableLinked : "No table linked", amountNeeded, itemType);
			this.lore = translatePlaceholdersLore(lore, keyName, lootTableLinked.length() > 0 ? lootTableLinked : "No table linked", amountNeeded, itemType);
		}

		public KeysData(String keyName, String displayName, String lootTableLinked, int amountNeeded, String itemType, List<String> lore) {
			this.keyName = keyName;
			this.amountNeeded = amountNeeded;
			this.itemType = addMatrial(itemType);
			this.lootTableLinked = lootTableLinked;
			System.out.println("lootTableLinked " + lootTableLinked + " " + lootTableLinked.length());
			this.displayName = translatePlaceholdersDisplayName(displayName, keyName, lootTableLinked.length() > 0 ? lootTableLinked : "No table linked", amountNeeded, itemType);
			this.lore = translatePlaceholdersLore(lore, keyName, lootTableLinked.length() > 0 ? lootTableLinked : "No table linked", amountNeeded, itemType);

		}

		private String translatePlaceholdersDisplayName(String displayName, Object... placeholders) {
			return translatePlaceholders(displayName, placeholders);

		}

		private List<String> translatePlaceholdersLore(List<String> lores, Object... placeholders) {
			if (lores == null) return new ArrayList<>();
			List<String> clonedlores = new ArrayList<>(lores);
			List<String> list = new ArrayList<>();
			for (String lore : clonedlores) {
				if (!checkListForPlaceholdersAndTranslate(lores, lore, placeholders))
					list.add(translatePlaceholders(lore, placeholders));
			}
			return list;
		}


		private Material addMatrial(final String itemType) {
			checkNotNull(itemType, "This containerType are null.");
			Material material = Enums.getIfPresent(Material.class, itemType).orNull();
			checkNotNull(material, "This " + itemType + " are not valid");

			return material;
		}

		public String getDisplayName() {
			return displayName;
		}

		public Material getItemType() {
			return itemType;
		}

		public String getKeyName() {
			return keyName;
		}

		public int getAmountNeeded() {
			return amountNeeded;
		}

		public List<String> getLore() {
			return lore;
		}

	}
/*
	@Override
	public String toString() {
		return "ContainerDataBuilder{" +
				"lootTableLinked='" + lootTableLinked + '\'' +
				", effect='" + effect + '\'' +
				", icon='" + icon + '\'' +
				", displayname='" + displayname + '\'' +
				", lore=" + lore +
				", containerData=" + containerData +
				", keysData=" + keysData +
				", spawning=" + spawning +
				", enchant=" + enchant +
				", Cooldown=" + cooldown +
				", builder=" + builder +
				'}';
	}*/
}
