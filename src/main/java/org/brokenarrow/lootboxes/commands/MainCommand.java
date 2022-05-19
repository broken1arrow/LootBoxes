package org.brokenarrow.lootboxes.commands;

import org.brokenarrow.lootboxes.commandprompt.CreateTable;
import org.brokenarrow.lootboxes.untlity.command.SubCommandsUtility;

import java.util.List;

public class MainCommand extends SubCommandsUtility {

	public MainCommand() {
		super("loot");
		setPermission("not.in.use");
	}

	@Override
	protected void onCommand() {
		new CreateTable().start(getPlayer());
	}

	@Override
	protected List<String> tabComplete() {
		System.out.println("labels deddddd " + getLabel());
		if (getArgs().length == 1)

			return completeLastWord("rbg");

		return null;
	}
}
