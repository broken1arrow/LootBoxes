package org.brokenarrow.lootboxes.commandprompt;

import org.broken.arrow.prompt.library.SimpleConversation;
import org.broken.arrow.prompt.library.SimplePrompt;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.loottable.EditCreateLootTable;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class CreateTable extends SimpleConversation {
	private final LootItems lootItems = LootItems.getInstance();

	public CreateTable() {
		super(Lootboxes.getInstance());
		setTimeout(80);
	}

	@Override
	public Prompt getFirstPrompt() {
		return new Command();
	}

	@Override
	protected void onConversationEnd(ConversationAbandonedEvent event, boolean canceledFromInactivity) {
		if (event.getContext().getForWhom() instanceof Player)
			new EditCreateLootTable().menuOpen((Player) event.getContext().getForWhom());
	}


	public class Command extends SimplePrompt {

		@Override
		protected String getPrompt(ConversationContext context) {

			return CREATE_TABLE_TYPE_NAME.languageMessagePrefix();
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
