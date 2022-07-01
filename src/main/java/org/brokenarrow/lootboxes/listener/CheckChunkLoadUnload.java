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

	private final Map<Object, Chunk> chachedChunks = new ConcurrentHashMap<>();
	private final Lootboxes plugin;
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();

	public CheckChunkLoadUnload(final Lootboxes plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void chunkUnLoad(final ChunkUnloadEvent event) {
		final int chunkX = event.getChunk().getX();
		final int chunkZ = event.getChunk().getZ();

		removeChunk(chunkX, chunkZ);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void chunkLoad(final ChunkLoadEvent event) {
		final int chunkX = event.getChunk().getX();
		final int chunkZ = event.getChunk().getZ();
		final Chunk chunk = event.getChunk();

		final List<Location> locationList = containerDataCache.getChunkData(chunk);
		if (locationList == null || locationList.isEmpty()) {
			/*if (isCunkInCache(chunk)) {
					this.chachedChunks.put(chunk.getX() + "=" + chunk.getZ(), chunk);
					Map<Object, ChunkSnapshot> chachedChunkSnapshot = new HashMap<>();
					chachedChunkSnapshot.put(chunk, chunk.getChunkSnapshot(true, false, false));

					runtaskLater(20, () -> addToHpperCache(true, chachedChunkSnapshot, locationList), true);
				}*/
		} else {
			this.chachedChunks.put(chunkX + "=" + chunkZ, chunk);
			runtaskLater(5, () -> addToHopperCache(true, chunk.getChunkSnapshot(true, false, false), locationList), true);
		}

	}

	public void addToHopperCache(final boolean loadingChunks, final ChunkSnapshot chunkSnapshot, final List<Location> locationList) {

		final int chunkX = chunkSnapshot.getX();
		final int chunkZ = chunkSnapshot.getZ();
		final List<Location> locations = containerDataCache.getChunkData(chunkX, chunkZ);
		for (final Location location : locations) {
			addToCache(location, loadingChunks, chunkSnapshot);
		}
	}

	public void addToCache(final Location location, final boolean loadingChunks, final ChunkSnapshot chunkSnapshot) {
		plugin.getSpawnContainerEffectsTask().addLocationInList(location);
	}


	public Map<Object, Chunk> getChachedKeys() {
		return this.chachedChunks;
	}

	public boolean chachedChunksContainsKey(final Location location) {
		return this.chachedChunks.containsKey((location.getBlockX() >> 4) + "=" + (location.getBlockZ() >> 4));
	}

	public Chunk getChunkData(final int chunkX, final int chunkZ) {
		return this.chachedChunks.get(chunkX + "=" + chunkZ);
	}

	/**
	 * Set chunkdata in cache.
	 *
	 * @param location location of object.
	 */

	public void setChachedChunks(final Location location) {
		this.chachedChunks.put((location.getBlockX() >> 4) + "=" + (location.getBlockZ() >> 4), location.getChunk());
	}

	public void setChachedChunks(final Chunk chunk) {
		this.chachedChunks.put(chunk.getX() + "=" + chunk.getZ(), chunk);
	}

	/**
	 * Remove chunks some not has registed containers.
	 *
	 * @param chunkX chunk location x
	 * @param chunkZ chunk location z
	 */

	public void removeChunkDataIfNoContainerRegisted(final int chunkX, final int chunkZ) {
		final Chunk chunkData = getChunkData(chunkX, chunkZ);
		if (chunkData == null) {
			removeChunk(chunkX, chunkZ);
		}

	}

	public void removeChunk(final int chunkX, final int chunkZ) {
		this.chachedChunks.remove(chunkX + "=" + chunkZ);
	}

	public void clearChachedChunks() {
		this.chachedChunks.clear();
	}
}
