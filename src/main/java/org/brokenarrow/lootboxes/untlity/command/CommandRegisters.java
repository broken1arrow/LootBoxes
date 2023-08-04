package org.brokenarrow.lootboxes.untlity.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.logging.Level;

public final class CommandRegisters {

	private static CommandGroupUtility commandGroupUtility;
	private static Plugin PLUGIN;

	public CommandRegisters(@NotNull Plugin plugin, @NotNull String command) {
		PLUGIN = plugin;
		commandGroupUtility = new CommandGroupUtility(command, Collections.singletonList(command));
	}

	public static CommandGroupUtility getCommandGroupUtility() {
		return commandGroupUtility;
	}

	public void registerSubclass(SubCommandsUtility... subCommands) {
		if (subCommands == null || subCommands.length < 1) {
			PLUGIN.getLogger().log(Level.WARNING, "subCommands is empty or null and your subcommands will not work");
			return;
		}
		for (SubCommandsUtility subcommand : subCommands)
			getCommandGroupUtility().registerSubcommand(subcommand);
	}


	public void registerSubclass() {
		/*for (CommandGroupUtilityAPI subclass : this.commandGroupClazz) {
			getCommandGroupUtility().registerSubcommands(getCommandGroupUtility());
		}*/

	}

	public static Plugin getPLUGIN() {
		return PLUGIN;
	}

	public static void register(String fallbackPrefix, Command command) {
		try {
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

			commandMap.register(fallbackPrefix, command);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public abstract static class registerSubCommands {

	}
}
