package org.brokenarrow.lootboxes.commands;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.settings.ChatMessages;
import org.brokenarrow.lootboxes.settings.GuiTempletSettings;
import org.brokenarrow.lootboxes.untlity.command.SubCommandsUtility;

public class ReloadCommand extends SubCommandsUtility {

	public ReloadCommand() {
		super("reload");
		setPermission("lootboxes.command.reload");
		setPermissionMessage("you don´t have lootboxes.admin.* or the children permission");
	}

	@Override
	protected void onCommand() {
		Lootboxes plugin = Lootboxes.getInstance();
		try {
			GuiTempletSettings.getInstance().reload();
			plugin.getSettings().reload();
			ChatMessages.messagesReload(plugin);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
