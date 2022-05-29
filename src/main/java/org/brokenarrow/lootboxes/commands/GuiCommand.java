package org.brokenarrow.lootboxes.commands;

import org.brokenarrow.lootboxes.menus.MainMenu;
import org.brokenarrow.lootboxes.untlity.command.SubCommandsUtility;

import java.util.ArrayList;
import java.util.List;

public class GuiCommand extends SubCommandsUtility {

	public GuiCommand() {
		super("menu");
		setPermission("lootboxes.command.menu");
		setPermissionMessage("you don´t have lootboxes.admin.* or the children permissions");

	}

	@Override
	protected void onCommand() {
		new MainMenu().menuOpen(getPlayer());
	}

	@Override
	protected List<String> tabComplete() {
		return new ArrayList<>();
	}
}
