package org.brokenarrow.lootboxes.runTask;

import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class RunTask extends BukkitRunnable {
	public RunTask(Lootboxes lootboxes) {
		this.lootboxes = lootboxes;
	}

	private final Lootboxes lootboxes;
	private final Random random = new Random();
	private BukkitTask task;

	public void start() {
		if (task != null && (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId()) || Bukkit.getScheduler().isQueued(task.getTaskId())))
			Bukkit.getScheduler().cancelTask(task.getTaskId());
		task = runTaskTimer(this.lootboxes, 0L, 20L);
	}

	@Override
	public void run() {
		this.lootboxes.getSpawnLootContainer().task();
		this.lootboxes.getSpawnedContainers().task();


	}


}
