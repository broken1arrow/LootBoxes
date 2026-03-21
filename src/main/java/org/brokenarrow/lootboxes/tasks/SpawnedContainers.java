package org.brokenarrow.lootboxes.tasks;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.BlockKey;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.CustomContainer;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.SkullUtility;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.brokenarrow.lootboxes.untlity.DebugMessages.sendDebug;
import static org.brokenarrow.lootboxes.untlity.ModifyBlock.*;

public class SpawnedContainers {

    private final Lootboxes lootboxes = Lootboxes.getInstance();
    private final Map<String, Long> cachedTimeMap = new HashMap<>();
    private final Map<String, Long> tempCache = new HashMap<>();
    private final Set<String> removeKey = new HashSet<>();
    private final Map<Location, Boolean> hasRefill = new HashMap<>();
    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();


    public void task() {

        for (Map.Entry<String, Long> entry : cachedTimeMap.entrySet()) {
            long time = entry.getValue();
            String key = entry.getKey();
            if (time == 0) {
                setCachedTimeMap(key, time);
            } else if (System.currentTimeMillis() >= time) {
                LootContainerData lootContainerData = containerDataCache.getCacheContainerData(key);
                if (lootContainerData == null || !lootContainerData.isSpawningContainerWithCooldown()) {
                    removeKey.add(key);
                    continue;
                }
                boolean failToSpawn = spawnContainer(lootContainerData);
                setCachedTimeMap(key, lootContainerData.getCooldown());
                if (!failToSpawn) {
                    removeKey.add(key);
                }
            }
        }
        removeKeyFromCache();
        if (!tempCache.isEmpty()) {
            cachedTimeMap.putAll(tempCache);
            cachedTimeMap.keySet().forEach(tempCache::remove);
        }
    }

    public boolean spawnContainer(LootContainerData lootContainerData) {
        Map<BlockKey, ContainerData> containerDataMap = lootContainerData.getLinkedContainerData();
        String lootTableLinked = lootContainerData.getLootTableLinked();
        for (Map.Entry<BlockKey, ContainerData> entry : containerDataMap.entrySet()) {
            ContainerData containerData = entry.getValue();
            BlockKey blockKey = entry.getKey();
            sendDebug("spawnContainer, loottable: " + lootTableLinked, this.getClass());
            sendDebug("spawnContainer, location: " + blockKey, this.getClass());
            sendDebug("spawnContainer, containerData: " + containerData, this.getClass());
            if (blockKey != null && lootTableLinked != null && !lootTableLinked.isEmpty()) {
                ItemStack[] item = this.lootboxes.getMakeLootTable().makeLootTable(lootTableLinked);
                if (item == null) {
                    return false;
                }
                Location location = blockKey.getLocation();
                if (location == null) {
                    sendDebug("Could not resolve the location set for spawn chest for this stored location " + blockKey + ".", this.getClass());
                    return false;
                }

                final Block block = location.getBlock();
                final ItemStack itemStack = containerData.getContainer();

                if (itemStack == null) {
                    sendDebug("Could not find valid container set for spawn chest for this center location " + blockKey + ".", this.getClass());
                    return false;
                }

                if (!SkullUtility.applySkullFromItem(block, itemStack)) {
                    block.setType(itemStack.getType());
                }

                setRotation(location, containerData.getFacing().getFace());
                setCustomName(location, lootContainerData.getDisplayName());

                lootboxes.getSpawnContainerEffectsTask().addLocationInList(location);
                this.setRefill(location, true);
                final CustomContainer customContainer = Lootboxes.getInstance().getCustomLootContainersCache().getSimilarContainer(itemStack.getType(), itemStack);
                if (customContainer != null) {
                    if (customContainer.isVanillaInventory()) {
                        Inventory inventory = getInventory(location);
                        if (inventory != null) {
                            inventory.setContents(item);
                        }
                    } else {
                        containerData.setContents(item);
                    }
                }
            }
        }
        return true;
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
        removeKey.forEach(cachedTimeMap::remove);
    }

    public Map<String, Long> getCachedTimeMap() {
        return cachedTimeMap;
    }

    public void setCachedTimeMap(String containerData, long seconds) {
        this.tempCache.put(containerData, System.currentTimeMillis() + (1000 * seconds));
    }
}
