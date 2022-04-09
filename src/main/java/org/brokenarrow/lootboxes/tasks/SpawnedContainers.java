package org.brokenarrow.lootboxes.tasks;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.ModifyBlock.*;

public class SpawnedContainers {

	private final Lootboxes lootboxes = Lootboxes.getInstance();
	Map<String, Long> cachedTimeMap = new HashMap<>();
	Map<String, Long> tempCache = new HashMap<>();
	private final ContainerData containerDataInstance = ContainerData.getInstance();
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();

	public void task() {

		for (Map.Entry<String, Long> entry : cachedTimeMap.entrySet()) {
			long time = entry.getValue();
			String key = entry.getKey();
			if (time == 0) {
				setCachedTimeMap(key, time);
			} else if (System.currentTimeMillis() >= time) {
				ContainerDataBuilder containerDataBuilder = containerDataInstance.getCacheContainerData(key);
				if (containerDataBuilder == null) return;
				spawnContainer(containerDataBuilder);
				setCachedTimeMap(key, containerDataBuilder.getCooldown());
			}
		}

		if (tempCache != null && !tempCache.isEmpty()) {
			cachedTimeMap.putAll(tempCache);
			for (String key : cachedTimeMap.keySet())
				tempCache.remove(key);
		}
	}

	public void spawnContainer(ContainerDataBuilder containerData) {
		Map<Location, ContainerDataBuilder.ContainerData> containerDataMap = containerData.getLinkedContainerData();
		String lootTableLinked = containerData.getLootTableLinked();
		for (Map.Entry<Location, ContainerDataBuilder.ContainerData> entry : containerDataMap.entrySet()) {
			ContainerDataBuilder.ContainerData containerData1 = entry.getValue();
			Location location = entry.getKey();
			if (location != null && lootTableLinked != null && !lootTableLinked.isEmpty()) {
				location.getBlock().setType(containerData1.getContainerType());

				location.getBlock().setType(containerData1.getContainerType());
				setRotation(location, containerData1.getFacing());
				setCustomName(location);

				ItemStack[] item = this.lootboxes.getMakeLootTable().makeLottable(lootTableLinked);
				/*System.out.println("loootTable ===" + Arrays.toString(item));
				System.out.println("loc " + location);*/
				Inventory inventory = getInventory(location);
				if (inventory != null) {
					inventory.setContents(item);
				}
			}
		}
	}


	public Map<String, Long> getCachedTimeMap() {
		return cachedTimeMap;
	}

	public void setCachedTimeMap(String containerdata, long seconds) {
		this.tempCache.put(containerdata, System.currentTimeMillis() + (1000 * seconds));
	}
}
