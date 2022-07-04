package org.brokenarrow.lootboxes.commandprompt;

import org.bukkit.entity.Player;

public abstract class Conversion {

	private final SimpleConversation simpleConversation;
	private final SimplePromp simplePromp;

	public Conversion(SimpleConversation simpleConversation) {
		this(simpleConversation, null);
	}

	public Conversion(SimpleConversation simpleConversation, SimplePromp simplePromp) {
		this.simpleConversation = simpleConversation;
		this.simplePromp = simplePromp;
	}

	public void startConversion(Player player) {
		if (this.simpleConversation != null) {
			this.simpleConversation.start(player);
		} else if (simplePromp != null)
			simplePromp.start(player);
	}

}
