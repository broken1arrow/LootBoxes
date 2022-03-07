package org.brokenarrow.lootboxes.untlity.command;

import java.util.List;

public interface CommandGroupUtilityAPI {

	default void registerSubcommands(CommandGroupUtility parent) {

	}

	default void registerSubcommand(final SubCommandsUtility command) {
	}

	default void register(final String label, final List<String> aliases) {

	}

	default String getMainLabel() {
		return "";
	}

}
