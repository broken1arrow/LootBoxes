package org.brokenarrow.lootboxes.commands;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.settings.ChatMessages;
import org.brokenarrow.lootboxes.settings.GuiTempletSettings;
import org.brokenarrow.lootboxes.untlity.command.SubCommandsUtility;

import java.util.logging.Level;

import static org.brokenarrow.lootboxes.settings.ChatMessages.RELOAD;

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
		} finally {
			if (getPlayer() != null)
				RELOAD.sendMessage(getPlayer());
			else
				Lootboxes.getInstance().getLogger().log(Level.INFO, "You have successful reload the config files");
		}

	}
}
