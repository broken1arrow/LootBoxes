package org.brokenarrow.lootboxes;

import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.runTask.RunTask;
import org.brokenarrow.lootboxes.tasks.SpawnLootContainer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;


public class Lootboxes extends JavaPlugin {
	private RunTask runTask;
	static Lootboxes plugin;
	private SpawnLootContainer spawnLootContainer;

	@Override
	public void onEnable() {
		plugin = this;
		this.spawnLootContainer = new SpawnLootContainer();
		this.runTask = new RunTask(this);
		this.runTask.start();
		new LootItems().reload();
		this.getLogger().log(Level.INFO, "Start Lootboxes");
	}

	public static Lootboxes getInstance() {
		return plugin;
	}

	public RunTask getRunTask() {
		return this.runTask;
	}

	public SpawnLootContainer getSpawnLootContainer() {
		return spawnLootContainer;
	}
}
