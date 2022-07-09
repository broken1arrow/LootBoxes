package org.brokenarrow.lootboxes.builder;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public final class KeysData implements ConfigurationSerializable {
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
		checkNotNull(itemType, "This item are null. for this key: " + keyName);
		Material material = Material.getMaterial(itemType);
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

	/**
	 * Creates a Map representation of this class.
	 * <p>
	 * This class must provide a method to restore this class, as defined in
	 * the {@link ConfigurationSerializable} interface javadocs.
	 *
	 * @return Map containing the current state of this class
	 */
	@NotNull
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> keysData = new LinkedHashMap<>();
		keysData.put("keyName", keyName);
		keysData.put("display_name", displayName);
		keysData.put("lootTable_Linked", lootTableLinked);
		keysData.put("amount_of_keys_to_open", amountNeeded);
		keysData.put("itemType", itemType + "");
		keysData.put("lore", lore);
		return keysData;
	}

	public static KeysData deserialize(Map<String, Object> map) {
		String keyName = (String) map.get("keyName");
		String displayName = (String) map.get("displayName");
		Object lootTableLinkedObj = map.get("lootTableLinked");
		String lootTableLinked;
		if (lootTableLinkedObj == null)
			lootTableLinked = (String) map.get("lootTable_Linked");
		else
			lootTableLinked = (String) lootTableLinkedObj;
		Object amountNeededObj = map.get("amountNeeded");
		int amountNeeded;
		if (amountNeededObj == null)
			amountNeeded = (int) map.getOrDefault("amount_of_keys_to_open", 1);
		else
			amountNeeded = (int) amountNeededObj;

		String itemType = (String) map.get("itemType");
		List<?> lore = (List<?>) map.get("lore");

		return new KeysData(keyName,
				displayName,
				lootTableLinked,
				amountNeeded,
				itemType,
				(List<String>) lore);
	}
}
