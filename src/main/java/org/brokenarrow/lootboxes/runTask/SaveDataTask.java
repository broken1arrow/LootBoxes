package org.brokenarrow.lootboxes.runTask;

import org.broken.arrow.yaml.library.YamlFileManager;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtask;

public class SaveDataTask extends BukkitRunnable {

	public SaveDataTask(final Lootboxes lootboxes) {
		this.lootboxes = lootboxes;
	}

	private final Map<YamlFileManager, String> cacheSave = new LinkedHashMap<>();
	private final Map<YamlFileManager, String> tempcache = new ConcurrentHashMap<>();
	private int amount;
	private final Lootboxes lootboxes;
	private BukkitTask task;

	public void start() {
		if (task != null && (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId()) || Bukkit.getScheduler().isQueued(task.getTaskId())))
			Bukkit.getScheduler().cancelTask(task.getTaskId());
		task = runTaskTimerAsynchronously(this.lootboxes, 0, 20L);
	}

	@Override
	public void run() {
		if (amount >= 20)
			task();
		amount++;
	}

	public void task() {
		for (final Map.Entry<YamlFileManager, String> entry : this.getCacheSave().entrySet())
			entry.getKey().save(entry.getValue());
		this.getCacheSave().clear();
		amount = 0;

		if (!tempcache.isEmpty()) {
			runtask(() -> {
				cacheSave.putAll(tempcache);
				cacheSave.keySet().forEach(tempcache::remove);
			}, true);
		}
	}

	public Map<YamlFileManager, String> getCacheSave() {
		return cacheSave;
	}

	public void addToSaveCache(final YamlFileManager instance, final String filename) {
		tempcache.put(instance, filename);
	}


}
