package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.EditCreateLootTable;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class testprompt extends SimpleConversation {
	private final LootItems lootItems = LootItems.getInstance();

	public testprompt() {
		setTimeout(50);
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new Command();
	}

	@Override
	protected void onConversationEnd(ConversationAbandonedEvent event, boolean canceledFromInactivity) {
		System.out.println("is enneded " + canceledFromInactivity);
	}

	@Override
	protected ConversationCanceller getCanceller() {
		return new SimpleCanceller("one");
	}

	public class Command extends SimplePromp {

		@Override
		protected String getPrompt(ConversationContext context) {
			return "Starning the test";
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			if (!lootItems.getSettings().containsKey(input))
				lootItems.addTable(input);
			new EditCreateLootTable().menuOpen(getPlayer(context));
			return null;
		}
	}

}
