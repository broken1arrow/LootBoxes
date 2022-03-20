package org.brokenarrow.lootboxes.commands;

import org.brokenarrow.lootboxes.menus.MainMenu;
import org.brokenarrow.lootboxes.untlity.command.CommandGroupUtilityAPI;
import org.brokenarrow.lootboxes.untlity.command.SubCommandsUtility;

import java.util.List;

public class GuiCommand extends SubCommandsUtility {

	public GuiCommand(CommandGroupUtilityAPI parent) {
		super(parent, "menu");
	}

	@Override
	protected void onCommand() {
		new MainMenu().menuOpen(getPlayer());
	}

	@Override
	protected List<String> tabComplete() {
		return completeLastWord("test");
	}
}
