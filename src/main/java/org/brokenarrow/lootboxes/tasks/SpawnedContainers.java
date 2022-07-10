package org.brokenarrow.lootboxes.tasks;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.brokenarrow.lootboxes.untlity.DebugMessages.sendDebug;
import static org.brokenarrow.lootboxes.untlity.ModifyBlock.*;
import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtask;

public class SpawnedContainers {

	private final Lootboxes lootboxes = Lootboxes.getInstance();
	private final Map<String, Long> cachedTimeMap = new HashMap<>();
	private final Map<String, Long> tempCache = new HashMap<>();
	private final Set<String> removeKey = new HashSet<>();
	private final Map<Location, Boolean> hasRefill = new HashMap<>();
	private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();

	public void task() {

		for (Map.Entry<String, Long> entry : cachedTimeMap.entrySet()) {
			long time = entry.getValue();
			String key = entry.getKey();
			if (time == 0) {
				setCachedTimeMap(key, time);
			} else if (System.currentTimeMillis() >= time) {
				ContainerDataBuilder containerDataBuilder = containerDataCacheInstance.getCacheContainerData(key);
				if (containerDataBuilder == null) return;
				if (!containerDataBuilder.isSpawningContainerWithCooldown()) {
					removeKey.add(key);
					return;
				}
				spawnContainer(containerDataBuilder);
				setCachedTimeMap(key, containerDataBuilder.getCooldown());

			}
		}
		removeKeyFromCache();
		if (!tempCache.isEmpty()) {
			runtask(() -> {
				cachedTimeMap.putAll(tempCache);
				cachedTimeMap.keySet().forEach(tempCache::remove);
			}, true);
		}
	}

	public void spawnContainer(ContainerDataBuilder containerData) {
		Map<Location, ContainerData> containerDataMap = containerData.getLinkedContainerData();
		String lootTableLinked = containerData.getLootTableLinked();
		for (Map.Entry<Location, ContainerData> entry : containerDataMap.entrySet()) {
			ContainerData containerData1 = entry.getValue();
			Location location = entry.getKey();
			sendDebug("spawnContainer, loottable: " + lootTableLinked, this.getClass());
			sendDebug("spawnContainer, location: " + location, this.getClass());
			sendDebug("spawnContainer, containerData: " + containerData1, this.getClass());
			if (location != null && lootTableLinked != null && !lootTableLinked.isEmpty()) {

				location.getBlock().setType(containerData1.getContainerType());

				location.getBlock().setType(containerData1.getContainerType());
				setRotation(location, containerData1.getFacing());
				setCustomName(location, containerData.getDisplayname());

				ItemStack[] item = this.lootboxes.getMakeLootTable().makeLottable(lootTableLinked);
				this.setRefill(location, true);
				Inventory inventory = getInventory(location);
				if (inventory != null) {
					inventory.setContents(item);
				}
			}
		}
	}

	public Set<String> getRemoveKey() {
		return removeKey;
	}

	public Map<Location, Boolean> getHasRefill() {
		return hasRefill;
	}

	public void setRefill(Location location, boolean hasFill) {
		hasRefill.put(location, hasFill);
	}

	public boolean isRefill(Location location) {
		Boolean hasFilled = hasRefill.get(location);
		return hasFilled == null || hasFilled;
	}

	public void removeKeyFromCache() {
		if (removeKey.isEmpty()) return;

		runtask(() -> removeKey.forEach(cachedTimeMap::remove), true);
	}

	public Map<String, Long> getCachedTimeMap() {
		return cachedTimeMap;
	}

	public void setCachedTimeMap(String containerdata, long seconds) {
		this.tempCache.put(containerdata, System.currentTimeMillis() + (1000 * seconds));
	}
}
