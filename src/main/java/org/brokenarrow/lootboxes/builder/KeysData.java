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
		checkNotNull(itemType, "This item are null.");
		Material material = Enums.getIfPresent(Material.class, itemType).orNull();
		checkNotNull(material, "This " + itemType + " are not valid");

		return material;
	}

	/**
	 * Name on the item player get.
	 *
	 * @return the display name.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Get the matrial for this key.
	 *
	 * @return matrial.
	 */
	public Material getItemType() {
		return itemType;
	}

	/**
	 * Get the key name used internally,
	 * to keep different keys apart.
	 *
	 * @return the key name.
	 */
	public String getKeyName() {
		return keyName;
	}

	/**
	 * Get the amount needed, for open a continer.
	 *
	 * @return the amount.
	 */
	public int getAmountNeeded() {
		return amountNeeded;
	}

	/**
	 * Get the lore for this item.
	 *
	 * @return A list.
	 */
	public List<String> getLore() {
		return lore;
	}

	/**
	 * Get table this key is linked too.
	 *
	 * @return the table linked to this key.
	 */
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
