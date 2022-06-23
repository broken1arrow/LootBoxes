package org.brokenarrow.lootboxes.runTask;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.settings.AllYamlFilesInFolder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtask;

public class SaveDataTask extends BukkitRunnable {

	public SaveDataTask(Lootboxes lootboxes) {
		this.lootboxes = lootboxes;
	}

	private final Map<AllYamlFilesInFolder, String> cacheSave = new LinkedHashMap<>();
	private final Map<AllYamlFilesInFolder, String> tempcache = new ConcurrentHashMap<>();
	private int amount;
	private final Lootboxes lootboxes;
	private BukkitTask task;

	public void start() {
		if (task != null && (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId()) || Bukkit.getScheduler().isQueued(task.getTaskId())))
			Bukkit.getScheduler().cancelTask(task.getTaskId());
		task = runTaskLaterAsynchronously(this.lootboxes, 20L);
	}

	@Override
	public void run() {
		if (amount + 20 >= 100)
			task();
		amount++;
	}

	public void task() {
		for (Map.Entry<AllYamlFilesInFolder, String> entry : getCacheSave().entrySet())
			entry.getKey().save(entry.getValue());
		amount = 0;

		if (!tempcache.isEmpty()) {
			runtask(() -> {
				cacheSave.putAll(tempcache);
				cacheSave.keySet().forEach(tempcache::remove);
			}, true);
		}
	}

	public Map<AllYamlFilesInFolder, String> getCacheSave() {
		return cacheSave;
	}

	public void addToSaveCache(AllYamlFilesInFolder instance, String filename) {
		tempcache.put(instance, filename);
	}


}
