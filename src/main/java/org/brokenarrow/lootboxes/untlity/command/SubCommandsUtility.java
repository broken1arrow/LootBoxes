package org.brokenarrow.lootboxes.untlity.command;

import org.jetbrains.annotations.NotNull;

public abstract class SubCommandsUtility extends CommandsUtility {

	private String sublabels;

	protected SubCommandsUtility(@NotNull CommandGroupUtilityAPI parent, @NotNull String sublabel) {
		super(parent.getMainLabel());
		this.sublabels = sublabel;

	}

	public String getSublabels() {
		return sublabels;
	}

	public void setSublabels(String sublabels) {
		this.sublabels = sublabels;
	}
}
