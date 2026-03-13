package org.brokenarrow.lootboxes.listener;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class CheckChunkLoadUnload implements Listener {

	private final Map<Object, Chunk> cachedChunks = new ConcurrentHashMap<>();
	private final Lootboxes plugin;
	private final ContainerDataCache containerDataCache;

	public CheckChunkLoadUnload(final Lootboxes plugin) {
		this.plugin = plugin;
		this.containerDataCache = plugin.getContainerDataCache();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void chunkUnLoad(final ChunkUnloadEvent event) {
		removeChunk(event.getChunk());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void chunkLoad(final ChunkLoadEvent event) {
		final Chunk chunk = event.getChunk();

		final List<Location> locationList = containerDataCache.getChunkDataCache().getChunkData(chunk);
		if (locationList != null && !locationList.isEmpty()) {
			this.setCachedChunks(chunk);
			ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot(true, false, false);
			runtaskLater(5, () -> addToHopperCache(chunkSnapshot, locationList), true);
		}

	}

	public void addToHopperCache(final ChunkSnapshot chunkSnapshot, final List<Location> locationList) {
		final List<Location> locations = containerDataCache.getChunkDataCache().getChunkData(chunkSnapshot);
		for (final Location location : locations) {
			addToCache(location, chunkSnapshot);
		}
	}

	public void addToCache(final Location location, final ChunkSnapshot chunkSnapshot) {
		plugin.getSpawnContainerEffectsTask().addLocationInList(location);
	}


	public Map<Object, Chunk> getCachedKeys() {
		return this.cachedChunks;
	}

	public boolean cachedChunksContainsKey(final Location location) {
		return this.cachedChunks.containsKey((location.getBlockX() >> 4) + "=" + (location.getBlockZ() >> 4));
	}

	public Chunk getChunkData(final int chunkX, final int chunkZ) {
		return this.cachedChunks.get(chunkX + "=" + chunkZ);
	}

	/**
	 * Set chunkdata in cache.
	 *
	 * @param location location of object.
	 */

	public void setCachedChunks(final Location location) {
		this.cachedChunks.put((location.getBlockX() >> 4) + "=" + (location.getBlockZ() >> 4), location.getChunk());
	}

	public void setCachedChunks(final Chunk chunk) {
		this.cachedChunks.put(chunk.getX() + "=" + chunk.getZ(), chunk);
	}

	/**
	 * Remove chunks some not has registed containers.
	 *
	 * @param chunkX chunk location x
	 * @param chunkZ chunk location z
	 */

	public void removeChunkDataIfNoContainerRegister(final int chunkX, final int chunkZ) {
		final Chunk chunkData = getChunkData(chunkX, chunkZ);
		if (chunkData == null) {
			removeChunk(chunkX, chunkZ);
		}

	}

	public void removeChunk(Chunk chunk) {
		this.cachedChunks.remove(chunk.getX() + "=" + chunk.getZ());
	}

	public void removeChunk(final int chunkX, final int chunkZ) {
		this.cachedChunks.remove(chunkX + "=" + chunkZ);
	}

	public void clearCachedChunks() {
		this.cachedChunks.clear();
	}
}
