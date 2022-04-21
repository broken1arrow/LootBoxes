package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.EditCreateLootTable;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class CreateTable extends SimpleConversation {
	private final LootItems lootItems = LootItems.getInstance();

	public CreateTable() {
		setTimeout(80);
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new Command();
	}

	@Override
	protected void onConversationEnd(ConversationAbandonedEvent event, boolean canceledFromInactivity) {
	}


	public class Command extends SimplePromp {

		@Override
		protected String getPrompt(ConversationContext context) {
			return CREATE_TABLE_TYPE_NAME.languageMessages();
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			
			if (!lootItems.getCachedLoot().containsKey(input)) {
				lootItems.addTable(input);
			} else {
				CREATE_TABLE_DUPLICATE.sendMessage(getPlayer(context), input);
				return getFirstPrompt();
			}
			CREATE_TABLE_CONFIRM.sendMessage(getPlayer(context), input);
			new EditCreateLootTable().menuOpen(getPlayer(context));
			return null;
		}
	}

}
