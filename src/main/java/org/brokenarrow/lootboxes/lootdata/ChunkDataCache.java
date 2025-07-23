package org.brokenarrow.lootboxes.lootdata;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkDataCache {

    private final Map<String, List<Location>> chunkData = new HashMap<>();

    /**
     * Set list of locations connected to same chunk.
     *
     * @param location were the container is placed.
     */
    public void setChunkData(final Location location) {

        final int x = location.getBlockX() >> 4;
        final int z = location.getBlockZ() >> 4;
        final List<Location> list = new ArrayList<>();
        final List<Location> locationList = this.chunkData.get(x + "=" + z);
        if (locationList != null) {
            if (!locationList.isEmpty())
                list.addAll(locationList);
        }
        if (!list.contains(location))
            list.add(location);
        this.chunkData.put(x + "=" + z, list);
    }

    /**
     * Get list of locations where containers are located
     * in the chunk.
     *
     * @param x the container is placed in.
     * @return list of locations.
     */
    public List<Location> getChunkData(final int x, final int z) {
        return this.chunkData.get(x + "=" + z);
    }

    /**
     * Get list of locations where containers are located
     * in the chunk.
     *
     * @param chunk the container is placed in.
     * @return list of locations.
     */
    public List<Location> getChunkData(final Object chunk) {
        Integer x = null;
        Integer z = null;
        if (chunk instanceof Chunk) {
            x = ((Chunk) chunk).getX();
            z = ((Chunk) chunk).getZ();
        }
        if (chunk instanceof ChunkSnapshot) {
            x = ((ChunkSnapshot) chunk).getX();
            z = ((ChunkSnapshot) chunk).getZ();
        }
        if (x == null || z == null)
            return null;
        return this.chunkData.get(x + "=" + z);
    }
}
