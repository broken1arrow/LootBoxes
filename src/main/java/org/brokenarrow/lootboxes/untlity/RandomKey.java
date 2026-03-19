package org.brokenarrow.lootboxes.untlity;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.KeyMeta.MOB_DROP_CONTAINER_DATA_NAME;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.MOB_DROP_KEY_NAME;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholders;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholdersLore;

public class RandomKey {
	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
	private final RandomUntility random = Lootboxes.getInstance().getRandomUntility();

	public ItemStack[] makeRandomAmountOfItems(EntityType entity) {
		List<ItemStack> itemStacks = new ArrayList<>();
		Map<String, KeyMobDropData> entityKeyDataSet = this.keyDropData.getEntityCache(entity);
		if (entityKeyDataSet != null && !entityKeyDataSet.isEmpty()) {
			for (KeyMobDropData keyMobDropData : entityKeyDataSet.values())
				itemStacks.add(makeRandomAmountOfItems(keyMobDropData));
		}
		return itemStacks.toArray(new ItemStack[0]);
	}

    public ItemStack makeRandomAmountOfItems(final KeyMobDropData keyMobDropData) {
        if (keyMobDropData == null) return null;
        final String containerData = keyMobDropData.getLootContainerKey();
        final String keyName = keyMobDropData.getKeyName();
        int amountOfItems = randomNumber(keyMobDropData);
        if (keyMobDropData.getChance() <= 0 || !random.chance(keyMobDropData.getChance()))
            return null;
        Map<String, Object> map = new HashMap<>();
        KeysData keysData = this.containerDataCache.getCacheKey(containerData, keyName);
        map.put(MOB_DROP_KEY_NAME.name(), keyName);
        map.put(MOB_DROP_CONTAINER_DATA_NAME.name(), containerData);
        String lootTable = keysData.getLootTableLinked();
        if (lootTable == null || lootTable.isEmpty()) {
            LootContainerData containerDataCache = this.containerDataCache.getCacheContainerData(containerData);
            if (containerDataCache != null) {
                lootTable = containerDataCache.getLootTableLinked();
            }
        }
        final String lootTableName = lootTable != null && !lootTable.isEmpty() ? lootTable : "No table linked";
        String placeholderDisplayName = translatePlaceholders(keysData.getDisplayName(), keysData.getKeyName(),
                lootTableName, keysData.getAmountNeeded(), keysData.getItemType());
        List<String> placeholdersLore = translatePlaceholdersLore(keysData.getLore(), keysData.getKeyName(),
                lootTableName, keysData.getAmountNeeded(), keysData.getItemType());

        return CreateItemUtily.of(false, keysData.getItemType(), placeholderDisplayName, placeholdersLore).setItemMetaDataList(map).setAmountOfItems(amountOfItems).makeItemStack();
    }

	private int randomNumber(KeyMobDropData keyMobDropData) {
		return Math.max(random.randomIntNumber(keyMobDropData.getMinimum(), keyMobDropData.getMaximum()), 0);
	}

	private ItemStack createItem(Material material, int amountOfItems) {

		return CreateItemUtily.of(material).setAmountOfItems(amountOfItems).makeItemStack();
	}
}
