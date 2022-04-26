package org.brokenarrow.lootboxes.untlity;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.EntityKeyData;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.brokenarrow.lootboxes.untlity.RandomUntility.chance;
import static org.brokenarrow.lootboxes.untlity.RandomUntility.randomIntNumber;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholders;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholdersLore;

public class RandomKey {

	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final ContainerData containerData = ContainerData.getInstance();

	public ItemStack[] makeRandomAmountOfItems(EntityType entety) {
		EntityKeyData entityKeyData = this.keyDropData.getEntityCache(entety);
		if (entityKeyData != null) {
			return makeRandomAmountOfItems(entityKeyData.getContainerDataFileName(), entityKeyData.getKeyName());
		}
		return new ItemStack[0];
	}

	public ItemStack[] makeRandomAmountOfItems(String containerData, String keyName) {
		List<ItemStack> itemStacks = new ArrayList<>();

		KeyMobDropData keyMobDropData = this.keyDropData.getKeyMobDropData(containerData, keyName);
		if (keyMobDropData == null) return null;

		//backupcounter = Math.max(randomIntNumber(keyMobDropData.getMinimum(), keyMobDropData.getMaximum()), 0);

		int amountOfItems = randomNumber(keyMobDropData);

		if (!chance(keyMobDropData.getChance()))
			return null;
		ContainerDataBuilder.KeysData keysData = this.containerData.getCacheKey(containerData, keyName);
		String placeholderDisplayName = translatePlaceholders(keysData.getDisplayName(), keysData.getKeyName(), keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
		List<String> placeholdersLore = translatePlaceholdersLore(keysData.getLore(), keysData.getKeyName(), keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
		
		ItemStack itemStack = CreateItemUtily.of(keysData.getItemType(), placeholderDisplayName, placeholdersLore).setAmoutOfItems(amountOfItems).makeItemStack();
		itemStacks.add(itemStack);

		return itemStacks.toArray(new ItemStack[0]);
	}

	private int randomNumber(KeyMobDropData keyMobDropData) {
		return Math.max(randomIntNumber(keyMobDropData.getMinimum(), keyMobDropData.getMaximum()), 0);
	}

	private ItemStack createItem(Material material, int amountOfItems) {

		return CreateItemUtily.of(material).setAmoutOfItems(amountOfItems).makeItemStack();
	}
}