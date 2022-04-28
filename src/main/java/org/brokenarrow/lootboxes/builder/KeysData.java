package org.brokenarrow.lootboxes.builder;

import com.google.common.base.Enums;
import org.bukkit.Material;

import java.util.List;

import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public final class KeysData {
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
		this.displayName = displayName;
		this.lore = lore;
	}

	public KeysData(String keyName, String displayName, String lootTableLinked, int amountNeeded, String itemType, List<String> lore) {
		this.keyName = keyName;
		this.amountNeeded = amountNeeded;
		this.itemType = addMatrial(itemType);
		this.lootTableLinked = lootTableLinked;
		this.displayName = displayName;
		this.lore = lore;

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

	public String getLootTableLinked() {
		return lootTableLinked;
	}

	@Override
	public String toString() {
		return "KeysToSave{" +
				"keyName='" + keyName + '\'' +
				", displayName='" + displayName + '\'' +
				", lootTableLinked='" + lootTableLinked + '\'' +
				", amountNeeded=" + amountNeeded +
				", itemType=" + itemType +
				", lore=" + lore +
				'}';
	}
}
