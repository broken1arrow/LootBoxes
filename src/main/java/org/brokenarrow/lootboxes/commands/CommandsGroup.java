package org.brokenarrow.lootboxes.commands;

import org.brokenarrow.lootboxes.untlity.command.CommandGroupUtility;
import org.brokenarrow.lootboxes.untlity.command.CommandGroupUtilityAPI;

public class CommandsGroup implements CommandGroupUtilityAPI {

	@Override
	public void registerSubcommands(CommandGroupUtility parent) {
		parent.registerSubcommand(new GuiCommand(parent));
		parent.registerSubcommand(new MainCommand(parent));

	}

}
