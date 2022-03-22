package org.brokenarrow.lootboxes;


import org.brokenarrow.lootboxes.commands.CommandsGroup;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.runTask.RunTask;
import org.brokenarrow.lootboxes.settings.GuiTempletSettings;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.tasks.SpawnLootContainer;
import org.brokenarrow.lootboxes.untlity.EnchantmentList;
import org.brokenarrow.lootboxes.untlity.MatrialList;
import org.brokenarrow.lootboxes.untlity.command.CommandGroupUtility;
import org.brokenarrow.lootboxes.untlity.command.CommandGroupUtilityAPI;
import org.brokenarrow.lootboxes.untlity.command.CommandRegister;
import org.brokenarrow.menu.library.RegisterMenuAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;


public class Lootboxes extends JavaPlugin {
	private RunTask runTask;
	static Lootboxes plugin;
	private Settings settings;
	private SpawnLootContainer spawnLootContainer;
	private MatrialList matrialList;
	private EnchantmentList enchantmentList;
	private boolean placeholderAPIMissing;
	private CommandGroupUtility commandGroupUtility;
	private CommandRegister commandRegister;
	private CommandsGroup commandsGroup;

	@Override
	public void onEnable() {
		plugin = this;
		this.spawnLootContainer = new SpawnLootContainer();
		this.matrialList = new MatrialList();
		this.enchantmentList = new EnchantmentList();
		this.runTask = new RunTask(this);
		this.runTask.start();
		this.settings = new Settings();
		GuiTempletSettings.getInstance().reload();

		this.settings.reload();
		LootItems.getInstance().reload();
		ItemData.getInstance().reload();
		new RegisterMenuAPI(this);

		commandRegister = new CommandRegister(this, "lootbox", new CommandsGroup());
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			/*
			 * We register the EventListener here, when PlaceholderAPI is installed.
			 * Since all events are in the main class (this class), we simply use "this"
			 */
			placeholderAPIMissing = false;
			//Bukkit.getPluginManager().registerEvents(this, this);
		} else {
			placeholderAPIMissing = true;
		}
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

	public Settings getSettings() {
		return settings;
	}

	public MatrialList getMatrialList() {
		return matrialList;
	}

	public EnchantmentList getEnchantmentList() {
		return enchantmentList;
	}

	public SpawnLootContainer getSpawnLootContainer() {
		return spawnLootContainer;
	}

	public boolean isPlaceholderAPIMissing() {
		return placeholderAPIMissing;
	}
}
