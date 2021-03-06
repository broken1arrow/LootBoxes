package org.brokenarrow.lootboxes;


import de.tr7zw.changeme.nbtapi.metodes.RegisterNbtAPI;
import org.brokenarrow.lootboxes.builder.*;
import org.brokenarrow.lootboxes.commands.GetKeyCommand;
import org.brokenarrow.lootboxes.commands.GuiCommand;
import org.brokenarrow.lootboxes.commands.ReloadCommand;
import org.brokenarrow.lootboxes.effects.SpawnContainerEffectsTask;
import org.brokenarrow.lootboxes.hooks.landprotecting.LandProtectingLoader;
import org.brokenarrow.lootboxes.listener.*;
import org.brokenarrow.lootboxes.lootdata.*;
import org.brokenarrow.lootboxes.runTask.HeavyTasks;
import org.brokenarrow.lootboxes.runTask.RunTask;
import org.brokenarrow.lootboxes.runTask.SaveDataTask;
import org.brokenarrow.lootboxes.settings.ChatMessages;
import org.brokenarrow.lootboxes.settings.GuiTempletSettings;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.tasks.SpawnContainerRandomLoc;
import org.brokenarrow.lootboxes.tasks.SpawnedContainers;
import org.brokenarrow.lootboxes.untlity.*;
import org.brokenarrow.lootboxes.untlity.command.CommandRegister;
import org.brokenarrow.menu.library.RegisterMenuAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;


public class Lootboxes extends JavaPlugin {
	private RunTask runTask;
	static Lootboxes plugin;
	private ChatMessages chatMessages;
	private Settings settings;
	private SpawnContainerRandomLoc spawnContainerRandomLoc;
	private MatrialList matrialList;
	private EnchantmentList enchantmentList;
	private ParticleEffectList particleEffectList;
	private MobList mobList;
	private boolean placeholderAPIMissing;
	private CheckChunkLoadUnload checkChunkLoadUnload;
	private CommandRegister commandRegister;
	private SpawnContainerEffectsTask spawnContainerEffectsTask;
	private SpawnedContainers spawnedContainers;
	private MakeLootTable makeLootTable;
	private HeavyTasks heavyTasks;
	private RandomUntility randomUntility;
	private RegisterNbtAPI nbtAPI;
	private LandProtectingLoader landProtectingLoader;
	private SaveDataTask saveDataTask;
	private ServerVersion serverVersion;

	@Override
	public void onLoad() {
		landProtectingLoader = new LandProtectingLoader(this);
	}

	@Override
	public void onEnable() {
		plugin = this;
		this.serverVersion = new ServerVersion(this);

		ConfigurationSerialization.registerClass(KeysData.class);
		ConfigurationSerialization.registerClass(ContainerData.class);
		ConfigurationSerialization.registerClass(ContainerDataBuilder.class);
		ConfigurationSerialization.registerClass(ParticleEffect.class);
		ConfigurationSerialization.registerClass(ParticleDustOptions.class);
		this.nbtAPI = new RegisterNbtAPI(this, false);
		this.settings = new Settings();
		this.randomUntility = new RandomUntility();
		this.matrialList = new MatrialList();
		this.enchantmentList = new EnchantmentList();
		this.particleEffectList = new ParticleEffectList();
		this.runTask = new RunTask(this);
		this.runTask.start();
		this.spawnedContainers = new SpawnedContainers();
		this.makeLootTable = new MakeLootTable();
		this.saveDataTask = new SaveDataTask(this);
		this.saveDataTask.start();
		this.checkChunkLoadUnload = new CheckChunkLoadUnload(this);
		this.heavyTasks = new HeavyTasks();
		this.spawnContainerEffectsTask = new SpawnContainerEffectsTask(this);
		reloadFiles();
		this.spawnContainerRandomLoc = new SpawnContainerRandomLoc();
		Bukkit.getPluginManager().registerEvents(new PlayerClick(), this);
		Bukkit.getPluginManager().registerEvents(new MobDropListener(), this);
		Bukkit.getPluginManager().registerEvents(new OpenContainer(), this);
		Bukkit.getPluginManager().registerEvents(new LinkTool(), this);
		Bukkit.getPluginManager().registerEvents(new CloseContainer(), this);
		Bukkit.getPluginManager().registerEvents(checkChunkLoadUnload, this);
		new RegisterMenuAPI(this);
		commandRegister = new CommandRegister(this, "lootbox");
		commandRegister.registerSubclass(new GuiCommand(), new ReloadCommand(), new GetKeyCommand());
		this.mobList = new MobList();
		heavyTasks.start();
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

	@Override
	public void onDisable() {
		saveFiles();
	}

	public void reloadFiles() {
		this.settings.reload();

		ContainerDataCache.getInstance().reload();
		GuiTempletSettings.getInstance().reload();
		LootItems.getInstance().reload();
		ItemData.getInstance().reload();
		KeyDropData.getInstance().reload();
		ChatMessages.messagesReload(this);

	}

	public void saveFiles() {
		ContainerDataCache.getInstance().save();
		LootItems.getInstance().save();
		ItemData.getInstance().save();
		KeyDropData.getInstance().save();
	}

	public static Lootboxes getInstance() {
		return plugin;
	}

	public HeavyTasks getHeavyTasks() {
		return heavyTasks;
	}

	public SpawnContainerEffectsTask getSpawnContainerEffectsTask() {
		return spawnContainerEffectsTask;
	}

	public ServerVersion getServerVersion() {
		return serverVersion;
	}

	public RunTask getRunTask() {
		return this.runTask;
	}

	public CheckChunkLoadUnload getCheckChunkLoadUnload() {
		return checkChunkLoadUnload;
	}

	public RegisterNbtAPI getNbtAPI() {
		return nbtAPI;
	}

	public Settings getSettings() {
		return settings;
	}

	public LandProtectingLoader getLandProtectingLoader() {
		return landProtectingLoader;
	}

	public MatrialList getMatrialList() {
		return matrialList;
	}

	public MobList getMobList() {
		return mobList;
	}

	public EnchantmentList getEnchantmentList() {
		return enchantmentList;
	}

	public SpawnContainerRandomLoc getSpawnLootContainer() {
		return spawnContainerRandomLoc;
	}

	public SaveDataTask getSaveDataTask() {
		return saveDataTask;
	}

	public RandomUntility getRandomUntility() {
		return randomUntility;
	}

	public MakeLootTable getMakeLootTable() {
		return makeLootTable;
	}

	public SpawnedContainers getSpawnedContainers() {
		return spawnedContainers;
	}

	public ParticleEffectList getParticleEffectList() {
		return particleEffectList;
	}

	public boolean isPlaceholderAPIMissing() {
		return placeholderAPIMissing;
	}
}
