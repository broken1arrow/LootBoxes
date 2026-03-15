package org.brokenarrow.lootboxes.lootdata;

import org.broken.arrow.library.serialize.utility.serialize.ConfigurationSerializable;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.BlockKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class LootContainerRandomCache  {
    private final Map<BlockKey, RandomLootData> cachedLootContainerLocations = new HashMap<>();


    public Map<BlockKey, RandomLootData> getCachedLootContainerLocations() {
        return cachedLootContainerLocations;
    }

    public RandomLootData getCachedLootContainerLocation(Location location) {
        return cachedLootContainerLocations.get(BlockKey.of(location));
    }

    public void putLootCachedLocation(Location cachedLocations, String containerName) {
        this.putLootCachedLocation(BlockKey.of(cachedLocations), new RandomLootData(containerName));
    }

    public void putLootCachedLocation(final BlockKey blockKey, final RandomLootData randomLootData) {
        this.cachedLootContainerLocations.put(blockKey, randomLootData);
    }

    public void removeCachedLootContainerLocation(final Location cachedLocation) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Lootboxes.getInstance(),() -> {
            this.cachedLootContainerLocations.remove(BlockKey.of(cachedLocation));
            Lootboxes.getInstance().getDatabaseManager().removeRandomSpawnedContainer(cachedLocation);
        },1L);

    }


    public static class RandomLootData implements ConfigurationSerializable {
        String containerKey;

        public RandomLootData(final String containerKey) {
            this.containerKey = containerKey;
        }

        public String getContainerKey() {
            return containerKey;
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("container_key", containerKey);
            return map;
        }


        public static RandomLootData deserialize(Map<String, Object> map) {
            String containerKey = map.get("container_key") + "";
            return new RandomLootData(containerKey);
        }
    }

}
