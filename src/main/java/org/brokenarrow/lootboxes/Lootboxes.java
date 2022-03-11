package org.brokenarrow.lootboxes;

import brokenarrow.menu.lib.RegisterMenuAPI;
import org.brokenarrow.lootboxes.commands.CommandsGroup;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.runTask.RunTask;
import org.brokenarrow.lootboxes.tasks.SpawnLootContainer;
import org.brokenarrow.lootboxes.untlity.command.CommandGroupUtility;
import org.brokenarrow.lootboxes.untlity.command.CommandGroupUtilityAPI;
import org.brokenarrow.lootboxes.untlity.command.CommandRegister;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;


public class Lootboxes extends JavaPlugin {
	private RunTask runTask;
	static Lootboxes plugin;
	private SpawnLootContainer spawnLootContainer;
	private CommandGroupUtility commandGroupUtility;
	private CommandRegister commandRegister;
	private CommandsGroup commandsGroup;

	@Override
	public void onEnable() {
		plugin = this;
		this.spawnLootContainer = new SpawnLootContainer();
		this.runTask = new RunTask(this);
		this.runTask.start();

		LootItems.getInstance().reload();
		ItemData.getInstance().reload();
		new RegisterMenuAPI(this);

		commandRegister = new CommandRegister(this, "lootbox", new CommandsGroup());

		this.getLogger().log(Level.INFO, "Start Lootboxes");
	}

	public static Lootboxes getInstance() {
		return plugin;
	}

	public RunTask getRunTask() {
		return this.runTask;
	}

	public CommandsGroup getCommandsGroup() {
		return commandsGroup;
	}

	public CommandGroupUtilityAPI getCommandGroupUtility() {
		return this.commandGroupUtility;
	}


	public SpawnLootContainer getSpawnLootContainer() {
		return spawnLootContainer;
	}
}
