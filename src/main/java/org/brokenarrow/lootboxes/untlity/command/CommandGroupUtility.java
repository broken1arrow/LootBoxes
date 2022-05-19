package org.brokenarrow.lootboxes.untlity.command;


import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static org.brokenarrow.lootboxes.untlity.command.CommandRegister.getPLUGIN;


public class CommandGroupUtility {

	private final Plugin plugin = getPLUGIN();
	private CommandsUtility mainCommand;
	private final List<SubCommandsUtility> subcommands = new ArrayList<>();

	public CommandGroupUtility(final String label, final List<String> aliases) {
		String[] splited = label.split("\\|");
		if (splited.length > 1) {
			for (String mainCommand : splited) {
				this.register(mainCommand, Collections.singletonList(mainCommand));
			}
		} else {
			this.register(label, aliases);
		}
	}

	public void register(final String label, final List<String> aliases) {
		//Valid.checkBoolean(!isRegistered(), "Main command already registered as: " + mainCommand);
		mainCommand = new MainCommand(label);

		if (aliases != null)
			mainCommand.setAliases(aliases);
		CommandRegister.register(plugin.getName(), mainCommand);
		//mainCommand.register(label);

		// Sort A-Z
		//subcommands.sort(Comparator.comparing(CommandsUtility::getLabel));

		// Check for collision
		//checkSubCommandAliasesCollision();
	}


	public final void registerSubcommand(final SubCommandsUtility command) {

		subcommands.add(command);
		subcommands.sort(Comparator.comparing(CommandsUtility::getLabel));
	}


	public String getMainLabel() {
		return mainCommand.getMainLabel().isEmpty() ? mainCommand.getLabel() : mainCommand.getMainLabel();
	}

	public final class MainCommand extends CommandsUtility {

		public MainCommand(String lable) {
			super(lable, getPLUGIN());
			setPermission(null);
		}

		@Override
		protected void onCommand() {

			if (getArgs().length < 1) {
				noPermissionRunSubCommands(subcommands, "test");
				return;
			}

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
				return tabCompleteSubcommands(getSender(), getArgs()[0], true);

			if (getArgs().length > 1) {
				final SubCommandsUtility cmd = findSubcommand(getArgs()[0]);

				if (cmd != null)
					return cmd.tabComplete(getSender(), getLabel(), Arrays.copyOfRange(getArgs(), 1, getArgs().length));
			}
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

		private List<String> tabCompleteSubcommands(final CommandSender sender, String param, boolean overridePermission) {
			param = param.toLowerCase();
			final List<String> tab = new ArrayList<>();
			for (final SubCommandsUtility subcommand : subcommands) {
				if (hasPerm(subcommand.getPermission()) || overridePermission) {
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
