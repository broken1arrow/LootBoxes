package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.menus.CustomizeItem;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.settings.ChatMessages.SEACH_FOR_ENCHANTMENT_TYPE_NAME;

public class SeachForEnchantment extends SimpleConversation {

	private final String lootTable;
	private final String itemToEdit;

	public SeachForEnchantment(String lootTable, String itemToEdit) {
		this.lootTable = lootTable;
		this.itemToEdit = itemToEdit;
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new ItemSeach();
	}

	public class ItemSeach extends SimplePromp {
		@Override
		protected String getPrompt(ConversationContext context) {
			return SEACH_FOR_ENCHANTMENT_TYPE_NAME.languageMessagePrefix();
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {

			new CustomizeItem.EnchantMents(lootTable, itemToEdit, input).menuOpen(getPlayer(context));
			return null;
		}
	}
}
