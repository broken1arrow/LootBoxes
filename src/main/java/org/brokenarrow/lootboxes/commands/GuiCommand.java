package org.brokenarrow.lootboxes.commands;

import org.broken.arrow.library.command.command.CommandHolder;
import org.brokenarrow.lootboxes.menus.MainMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GuiCommand extends CommandHolder {

	public GuiCommand() {
		super("menu");

	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String @NotNull [] cmdArgs) {
		Player player = getPlayer();
		if (player != null) {
			new MainMenu().menuOpen(player);
		} else {
			sender.sendMessage("You can't run this command from console.");
		}
		return false;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String @NotNull [] cmdArgs) {
		return new ArrayList<>();
	}

}
