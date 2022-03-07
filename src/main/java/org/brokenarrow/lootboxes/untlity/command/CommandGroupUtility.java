package org.brokenarrow.lootboxes.untlity.command;

import org.brokenarrow.lootboxes.untlity.errors.Valid;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CommandGroupUtility implements CommandGroupUtilityAPI {

	private final Plugin plugin;
	private CommandsUtility mainCommand;
	private final List<SubCommandsUtility> subcommands = new ArrayList<>();

	public CommandGroupUtility(final String label, final List<String> aliases, Plugin plugin) {
		this.plugin = plugin;
		this.register(label, aliases);
	}

	@Override
	public void register(final String label, final List<String> aliases) {
		//Valid.checkBoolean(!isRegistered(), "Main command already registered as: " + mainCommand);
		mainCommand = new MainCommand(label);

		if (aliases != null)
			mainCommand.setAliases(aliases);
		RegisterCommand.register(plugin.getName(), mainCommand);
		//mainCommand.register(label);

		// Sort A-Z
		//subcommands.sort(Comparator.comparing(CommandsUtility::getLabel));

		// Check for collision
		//checkSubCommandAliasesCollision();
	}


	@Override
	public void registerSubcommands(CommandGroupUtility commandGroupUtility) {

	}


	@Override
	public final void registerSubcommand(final SubCommandsUtility command) {
		Valid.checkNotNull(mainCommand, "Cannot add subcommands when main command is missing! Call register()");
		Valid.checkBoolean(!subcommands.contains(command), "Subcommand /" + mainCommand.getLabel() + " " + command.getLabel() + " already registered when trying to add " + command.getClass());

		subcommands.add(command);
		subcommands.sort(Comparator.comparing(CommandsUtility::getLabel));
	}

	@Override
	public String getMainLabel() {
		return mainCommand.getMainLabel().isEmpty() ? mainCommand.getLabel() : mainCommand.getMainLabel();
	}

	public final class MainCommand extends CommandsUtility {

		public MainCommand(String lable) {
			super(lable, plugin);

		}

		@Override
		protected void onCommand() {
			if (getArgs().length < 1) return;

			final String argument = getArgs()[0];
			final SubCommandsUtility command = findSubcommand(argument);
			if (command != null) {

				final String oldSublabel = command.getLabel();
				try {
					// Simulate our main label
					command.setLabel(argument);
					// Run the command
					command.execute(getSender(), getLabel(), getArgs().length == 1 ? new String[]{} : Arrays.copyOfRange(getArgs(), 1, getArgs().length));

				} finally {
					// Restore old sublabel after the command has been run
					command.setLabel(oldSublabel);
				}

			}

		}

		@Override
		protected List<String> tabComplete() {
			if (getArgs().length == 1)
				return tabCompleteSubcommands(getSender(), getArgs()[0]);
			//return completeLastWord("menu");

			return null;
		}

		private SubCommandsUtility findSubcommand(final String label) {
			for (final SubCommandsUtility command : subcommands) {

				//for (final String alias : command.getLabel())
				String alias = command.getSublabels();

				if (alias.equalsIgnoreCase(label))
					return command;
			}

			return null;
		}

		private List<String> tabCompleteSubcommands(final CommandSender sender, String param) {
			param = param.toLowerCase();
			final List<String> tab = new ArrayList<>();
			for (final SubCommandsUtility subcommand : subcommands) {
				if (hasPerm(subcommand.getPermission())) {
					String label = subcommand.getSublabels();
					//for (final String label : subcommand.getLabel())
					if (!label.trim().isEmpty() && label.startsWith(param))
						tab.add(label);
				}
			}
			return tab;
		}
	}
}
