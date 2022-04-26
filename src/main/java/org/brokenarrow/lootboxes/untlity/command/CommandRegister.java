package org.brokenarrow.lootboxes.untlity.command;

import org.brokenarrow.lootboxes.untlity.errors.Valid;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommandRegister {

	private final CommandGroupUtility commandGroupUtility;
	private final List<CommandGroupUtilityAPI> commandGroupClazz = new ArrayList<>();

	public CommandRegister(Plugin plugin, String label, CommandGroupUtilityAPI commandGroupClazz) {
		this.commandGroupUtility = new CommandGroupUtility(label, Collections.singletonList(label), plugin);
		if (this.commandGroupClazz.contains(commandGroupClazz))
			new Valid.CatchExceptions("This class " + commandGroupClazz.getClass().getName() + " is alredy registed.");
		this.commandGroupClazz.add(commandGroupClazz);
		registerSubclass();
	}

	public CommandGroupUtility getCommandGroupUtility() {
		return commandGroupUtility;
	}

	public void registerSubclass() {
		for (CommandGroupUtilityAPI subclass : this.commandGroupClazz) {
			subclass.registerSubcommands(getCommandGroupUtility());
		}

	}
}
