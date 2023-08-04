package org.brokenarrow.lootboxes.commands;

import org.broken.arrow.command.library.command.CommandHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.settings.ChatMessages;
import org.brokenarrow.lootboxes.settings.GuiTempletSettings;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

import static org.brokenarrow.lootboxes.settings.ChatMessages.RELOAD;

public class ReloadCommand extends CommandHolder {

	public ReloadCommand() {
		super("reload");
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String @NotNull [] cmdArgs)  {
		Lootboxes plugin = Lootboxes.getInstance();
		try {
			GuiTempletSettings.getInstance().reload();
			plugin.getSettings().reload();
			ChatMessages.messagesReload(plugin);
			plugin.getSpawnLootContainer().setRandomSpawnedContainer();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (getPlayer() != null)
				RELOAD.sendMessage(getPlayer());
			else
				Lootboxes.getInstance().getLogger().log(Level.INFO, "You have successful reload the config files");
		}
		return true;
	}

}
