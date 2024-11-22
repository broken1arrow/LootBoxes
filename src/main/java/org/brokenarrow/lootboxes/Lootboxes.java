package org.brokenarrow.lootboxes;


import org.broken.arrow.command.library.CommandRegister;
import org.broken.arrow.itemcreator.library.ItemCreator;
import org.broken.arrow.menu.button.manager.library.MenusSettingsHandler;
import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.RegisterMenuAPI;
import org.broken.arrow.nbt.library.RegisterNbtAPI;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.ParticleDustOptions;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.commands.GetKeyCommand;
import org.brokenarrow.lootboxes.commands.GuiCommand;
import org.brokenarrow.lootboxes.commands.ReloadCommand;
import org.brokenarrow.lootboxes.effects.SpawnContainerEffectsTask;
import org.brokenarrow.lootboxes.hooks.landprotecting.LandProtectingLoader;
import org.brokenarrow.lootboxes.listener.CheckChunkLoadUnload;
import org.brokenarrow.lootboxes.listener.CloseContainer;
import org.brokenarrow.lootboxes.listener.LinkTool;
import org.brokenarrow.lootboxes.listener.MobDropListener;
import org.brokenarrow.lootboxes.listener.OpenContainer;
import org.brokenarrow.lootboxes.listener.PlayerClick;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.lootdata.MakeLootTable;
import org.brokenarrow.lootboxes.runTask.HeavyTasks;
import org.brokenarrow.lootboxes.runTask.RunTask;
import org.brokenarrow.lootboxes.runTask.SaveDataTask;
import org.brokenarrow.lootboxes.settings.ChatMessages;
import org.brokenarrow.lootboxes.settings.GuiTempletSettings;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.tasks.SpawnContainerRandomLoc;
import org.brokenarrow.lootboxes.tasks.SpawnedContainers;
import org.brokenarrow.lootboxes.untlity.EnchantmentList;
import org.brokenarrow.lootboxes.untlity.MatrialList;
import org.brokenarrow.lootboxes.untlity.MobList;
import org.brokenarrow.lootboxes.untlity.ParticleEffectList;
import org.brokenarrow.lootboxes.untlity.RandomUntility;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.logging.Level;


public class Lootboxes extends JavaPlugin {
	private RunTask runTask;
	private static Lootboxes plugin;
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
	private ItemCreator itemCreator;
	private RegisterMenuAPI menuApi;
	private MenusSettingsHandler menusCache;

	@Override
	public void onLoad() {
		landProtectingLoader = new LandProtectingLoader(this);
	}

	@Override
	public void onEnable() {
		plugin = this;
		this.serverVersion = new ServerVersion(this);
		this.itemCreator = new ItemCreator(this);
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
		this.spawnedContainers = new SpawnedContainers();
		this.makeLootTable = new MakeLootTable();
		this.saveDataTask = new SaveDataTask(this);
		this.saveDataTask.start();
		this.checkChunkLoadUnload = new CheckChunkLoadUnload(this);
		this.heavyTasks = new HeavyTasks();
		this.spawnContainerEffectsTask = new SpawnContainerEffectsTask(this);
		this.settings.reload();
		if (settings.getSettingsData().isSingleMenuFile())
			this.menusCache = new MenusSettingsHandler(this, "language/menus_" + this.settings.getSettingsData().getLanguage() + ".yml", true);
		else
			this.menusCache = new MenusSettingsHandler(this, "menus", false);
		reloadFiles();
		this.spawnContainerRandomLoc = new SpawnContainerRandomLoc();
		Bukkit.getPluginManager().registerEvents(new PlayerClick(), this);
		Bukkit.getPluginManager().registerEvents(new MobDropListener(), this);
		Bukkit.getPluginManager().registerEvents(new OpenContainer(), this);
		Bukkit.getPluginManager().registerEvents(new LinkTool(), this);
		Bukkit.getPluginManager().registerEvents(new CloseContainer(), this);
		Bukkit.getPluginManager().registerEvents(checkChunkLoadUnload, this);
		this.menuApi = new RegisterMenuAPI(this);
		commandRegister = new CommandRegister( );
		this.registerCommands();
		this.mobList = new MobList();
		heavyTasks.start();
		this.runTask.start();
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
		this.getLogger().log(Level.INFO, "Has started Lootboxes");

	}

	@Override
	public void onDisable() {
		saveFiles();
	}

	public void reloadFiles() {
		this.settings.reload();
		this.menusCache.reload();
		ContainerDataCache.getInstance().reload();
		File file = new File(plugin.getDataFolder() + "/language/guitemplets_" + this.settings.getSettingsData().getLanguage() + ".yml");

		if (file.exists()) {
			GuiTempletSettings.getInstance().reload();
			File newFile = new File(plugin.getDataFolder() + "/language/guitemplets_old_" + this.settings.getSettingsData().getLanguage() + ".yml");
			file.renameTo(newFile);
		}
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

	public void registerCommands() {
		commandRegister.registerMainCommand(this.getName(), "lootboxes|loot");
		commandRegister.registerSubCommand(
						new GuiCommand()
										.setPermission("lootboxes.command.menu")
										.setPermissionMessage("you don´t have lootboxes.admin.* or the 'lootboxes.command.menu' permission.")
		);
		commandRegister.registerSubCommand(new ReloadCommand()
						.setPermission("lootboxes.command.reload")
						.setPermissionMessage("you don´t have lootboxes.admin.* or the 'lootboxes.command.reload' permission.")
		);
		commandRegister.registerSubCommand(new GetKeyCommand()
						.setPermission("lootboxes.command.key")
						.setPermissionMessage("you don´t have lootboxes.admin.* or the 'lootboxes.command.key' permission.")
		);
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

	public ItemCreator getItemCreator() {
		return itemCreator;
	}

	public ServerVersion getServerVersion() {
		return serverVersion;
	}

	public RegisterMenuAPI getMenuApi() {
		return menuApi;
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

	public MenusSettingsHandler getMenusCache() {
		return menusCache;
	}

	@Nullable
	public MenuTemplate getMenu(String menuName) {
		return menusCache.getTemplate(menuName);
	}

	@Nullable
	public MenuButtonData getMenuButton(String menuName, int slot) {
		return menusCache.getMenuButton(menuName, slot);
	}

	public boolean isPlaceholderAPIMissing() {
		return placeholderAPIMissing;
	}
}
