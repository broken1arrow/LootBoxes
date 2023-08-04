package org.brokenarrow.lootboxes.commands;

import org.broken.arrow.command.library.command.CommandHolder;
import org.brokenarrow.lootboxes.menus.MainMenu;
import org.bukkit.command.CommandSender;
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
		new MainMenu().menuOpen(getPlayer());
		return true;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String @NotNull [] cmdArgs) {
		return new ArrayList<>();
	}

}
