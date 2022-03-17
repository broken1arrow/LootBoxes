package org.brokenarrow.lootboxes.commands;

import org.brokenarrow.lootboxes.commandprompt.testprompt;
import org.brokenarrow.lootboxes.untlity.command.CommandGroupUtilityAPI;
import org.brokenarrow.lootboxes.untlity.command.SubCommandsUtility;

import java.util.List;

public class MainCommand extends SubCommandsUtility {

	public MainCommand(CommandGroupUtilityAPI parent) {
		super(parent, "loot");
	}

	@Override
	protected void onCommand() {
		new testprompt().start(getPlayer());
	}

	@Override
	protected List<String> tabComplete() {
		System.out.println("labels deddddd " + getLabel());
		if (getArgs().length == 1)

			return completeLastWord("rbg");

		return null;
	}
}
