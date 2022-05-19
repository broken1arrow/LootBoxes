package org.brokenarrow.lootboxes.commands;

import org.brokenarrow.lootboxes.commandprompt.CreateTable;
import org.brokenarrow.lootboxes.untlity.command.SubCommandsUtility;

import java.util.List;

public class MainCommand extends SubCommandsUtility {

	public MainCommand() {
		super("loot");

	}

	@Override
	protected void onCommand() {
		if (hasPerm("not.in.use"))
			new CreateTable().start(getPlayer());
	}

	@Override
	protected List<String> tabComplete() {
		if (getArgs().length == 1)
			return completeLastWord("rbg");
		return null;
	}
}
