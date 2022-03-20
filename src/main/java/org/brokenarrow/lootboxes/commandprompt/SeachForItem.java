package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.menus.CustomizeItem;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeachForItem extends SimpleConversation {

	private final String lootTable;
	private final String itemToEdit;

	public SeachForItem(String lootTable, String itemToEdit) {
		this.lootTable = lootTable;
		this.itemToEdit = itemToEdit;
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new itemSeach();
	}

	public class itemSeach extends SimplePromp {
		public itemSeach() {

		}

		@Override
		protected String getPrompt(ConversationContext context) {
			return "Seach for item or items, type in parts or whole name of item";
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {

			new CustomizeItem.ChangeItem(lootTable, itemToEdit, input).menuOpen(getPlayer(context));

			return null;
		}
	}
}