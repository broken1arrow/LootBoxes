package org.brokenarrow.lootboxes.lootdata;

import org.broken.arrow.library.nbt.RegisterNbtAPI;
import org.broken.arrow.library.serialize.utility.serialize.ConfigurationSerializable;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.BlockKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class LootContainerRandomCache {
    private final Map<BlockKey, RandomLootData> cachedLootContainerLocations = new HashMap<>();


    public Map<BlockKey, RandomLootData> getCachedLootContainerLocations() {
        return cachedLootContainerLocations;
    }

    public RandomLootData getCachedLootContainerLocation(Location location) {
        return cachedLootContainerLocations.get(BlockKey.of(location));
    }

    public void putLootCachedLocation(Location cachedLocations, String containerName, final ItemStack[] stacks) {
        RandomLootData randomLootData = new RandomLootData(containerName);
        randomLootData.setContent(stacks);
        this.putLootCachedLocation(BlockKey.of(cachedLocations), randomLootData);
    }

    public void putLootCachedLocation(final BlockKey blockKey, final RandomLootData randomLootData) {
        this.cachedLootContainerLocations.put(blockKey, randomLootData);
    }

    public void removeCachedLootContainerLocation(final Location cachedLocation) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Lootboxes.getInstance(), () -> {
            this.cachedLootContainerLocations.remove(BlockKey.of(cachedLocation));
            Lootboxes.getInstance().getDatabaseManager().removeRandomSpawnedContainer(cachedLocation);
        }, 1L);

    }

    public static class RandomLootData implements ConfigurationSerializable {
        String containerKey;
        private ItemStack[] stacks;

        public RandomLootData(final String containerKey) {
            this.containerKey = containerKey;
        }

        public String getContainerKey() {
            return containerKey;
        }

        public void setContent(ItemStack[] stacks) {
            this.stacks = stacks;
        }

        public ItemStack[] getContent() {
            return stacks;
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("container_key", containerKey);
            map.put("container_contents", RegisterNbtAPI.serializeItemStack(stacks));
            return map;
        }


        public static RandomLootData deserialize(Map<String, Object> map) {
            String containerKey = map.get("container_key") + "";
            Object contents = map.get("container_contents");
            ItemStack[] containerContents = new ItemStack[0];
            if (contents instanceof byte[])
                containerContents = RegisterNbtAPI.deserializeItemStack((byte[]) contents);

            RandomLootData randomLootData = new RandomLootData(containerKey);
            randomLootData.setContent(containerContents);
            return randomLootData;
        }

    }

}
