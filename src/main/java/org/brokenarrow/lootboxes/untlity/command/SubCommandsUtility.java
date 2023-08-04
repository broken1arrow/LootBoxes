package org.brokenarrow.lootboxes.untlity.command;

import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.untlity.command.CommandRegisters.getCommandGroupUtility;

public abstract class SubCommandsUtility extends CommandsUtility {

	private String sublabels;

	protected SubCommandsUtility(@NotNull String sublabel) {
		super(getCommandGroupUtility().getMainLabel());
		this.sublabels = sublabel;

	}

	public String getSublabels() {
		return sublabels;
	}

	public void setSublabels(String sublabels) {
		this.sublabels = sublabels;
	}
}
