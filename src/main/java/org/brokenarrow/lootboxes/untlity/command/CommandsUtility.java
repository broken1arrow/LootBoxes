package org.brokenarrow.lootboxes.untlity.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.brokenarrow.lootboxes.untlity.command.CommandRegisters.getPLUGIN;

public abstract class CommandsUtility extends Command {

	private CommandSender sender;
	private String label = "";
	private String[] args;
	protected static Plugin pluginType = getPLUGIN();

	public CommandsUtility(@NotNull String name, @NotNull Plugin plugin) {
		this(name);
		pluginType = plugin;
	}

	public CommandsUtility(@NotNull String name) {
		super(name);
		setLabel(name);
		setAliases(Collections.singletonList(name));
	}

	protected CommandsUtility(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
		super(name, description, usageMessage, aliases);
	}


	@Override
	public boolean execute(final @NotNull CommandSender sender, final @NotNull String commandLabel, final String[] args) {

		if (!pluginType.isEnabled()) {
			pluginType.getLogger().warning("This plugin " + pluginType.getName() + " is not loaded");
			sender.sendMessage("This plugin " + pluginType.getName() + " is not loaded");
			return false;
		}

		this.sender = sender;
		this.label = commandLabel;
		this.args = args;
		try {

			if (getPermission() != null)
				checkPerm(getPermission());

			onCommand();
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		return true;
	}

	@NotNull
	@Override
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		this.sender = sender;
		this.label = alias;
		this.args = args;

		if (hasPerm(getPermission())) {
			List<String> suggestions = tabComplete();
			if (suggestions == null)
				suggestions = new ArrayList<>();

			return suggestions;
		}
		return new ArrayList<>();
	}

	@NotNull
	@Override
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
		return tabComplete(sender, alias, args);
	}

	@SafeVarargs
	protected final <T> List<String> completeLastWord(final T... suggestions) {
		return TabUtil.complete(getLastArg(), suggestions);
	}

	protected final <T> List<String> completeLastWord(final Iterable<T> suggestions, Function<T, String> toString) {
		final List<String> list = new ArrayList<>();

		for (final T suggestion : suggestions)
			list.add(toString.apply(suggestion));

		return TabUtil.complete(getLastArg(), list.toArray());
	}

	/**
	 * Join an array together using the given deliminer
	 *
	 * @param start
	 * @param stop
	 * @param array
	 * @param delimiter
	 * @return
	 */
	public static String joinRange(final int start, final int stop, final String[] array, final String delimiter) {
		String joined = "";

		for (int i = start; i < range(stop, 0, array.length); i++)
			joined += (joined.isEmpty() ? "" : delimiter) + array[i];

		return joined;
	}

	protected static int range(final int value, final int min, final int max) {
		return Math.min(Math.max(value, min), max);
	}

	/**
	 * Joins an array together using spaces from the given start index
	 *
	 * @param startIndex
	 * @param array
	 * @return
	 */
	public static String joinRange(final int startIndex, final String[] array) {
		return joinRange(startIndex, array.length, array);
	}

	/**
	 * Join an array together using spaces using the given range
	 *
	 * @param startIndex
	 * @param stopIndex
	 * @param array
	 * @return
	 */
	public static String joinRange(final int startIndex, final int stopIndex, final String[] array) {
		return joinRange(startIndex, stopIndex, array, " ");
	}

	protected abstract void onCommand();

	protected List<String> tabComplete() {
		return null;
	}

	public String[] getArgs() {
		return args;
	}

	@Nullable
	@Override
	public String getPermission() {
		return super.getPermission();
	}

	/**
	 * Sets the permission required for this command to run. If you set the
	 * permission to null we will not require any permission (unsafe).
	 *
	 * @param permission permission you want to set.
	 */
	@Override
	public final void setPermission(final String permission) {
		super.setPermission(permission);
	}

	@NotNull
	@Override
	public String getLabel() {
		return label;
	}

	protected final boolean isPlayer() {
		return sender instanceof Player;
	}

	protected final CommandSender getSender() {
		return sender;
	}

	protected final String getLastArg() {
		return getArgs().length > 0 ? getArgs()[args.length - 1] : "";
	}

	protected final boolean hasPerm(String permission) {
		return this.hasPerm(sender, permission);
	}

	/**
	 * Attempts to get the sender as player, only works if the sender is actually a player,
	 * otherwise we return null
	 *
	 * @return
	 */
	protected final Player getPlayer() {
		return isPlayer() ? (Player) getSender() : null;
	}

	public final String getMainLabel() {
		return super.getLabel();
	}


	@NotNull
	@Override
	public final String getPermissionMessage() {
		return getOrDefault(super.getPermissionMessage(), "You don´t have permission to run this command");
	}

	public final void noPermissionRunSubCommands(List<SubCommandsUtility> subcommands, @NonNull final String perm) {
		List<String> list = new ArrayList<>();
		if (isPlayer()) {
			list.add("&8>>>>&7 Sub commands for command /" + getMainLabel() + ":&8 <<<<<<");
			list.add("");
			list.add("");
			for (final SubCommandsUtility command : subcommands) {
				list.add("&6 /" + command.getMainLabel() + " " + command.getSublabels() + "&3  permission:&4 " + command.getPermission());

			}
			for (String message : list) {
				if (getPlayer() != null)
					getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
				else
					System.out.println("[Lootboxes] " + message);
			}
		}
	}

	public final void checkPerm(@NonNull final String perm) throws CommandException {
		if (isPlayer() && !hasPerm(perm)) {
			getPlayer().sendMessage(getPermissionMessage().replace("{permission}", perm));
			//throw new CommandException(getPermissionMessage().replace("{permission}", perm));
		}
	}

	protected final void checkConsole() throws CommandException {
		if (!isPlayer())
			throw new CommandException("&c" + "You can´t run this command from console");
	}

	protected final boolean hasPerm(CommandSender sender, String permission) {
		return permission == null || (isPlayer() && sender.hasPermission(permission.replace("{label}", getLabel())));
	}

	public static <T> T getOrDefault(final T value, final T defaults) {
		return value != null ? value : defaults;
	}
}
