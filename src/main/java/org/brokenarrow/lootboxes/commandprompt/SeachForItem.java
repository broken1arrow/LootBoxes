package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.menus.EntityTypeListMenu;
import org.brokenarrow.lootboxes.menus.MatrialList;
import org.brokenarrow.lootboxes.menus.MenuKeys;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.menus.MenuKeys.ENTITY_TYPE_LISTMENU;
import static org.brokenarrow.lootboxes.settings.ChatMessages.SEACH_FOR_ITEM_TYPE_NAME;

public class SeachForItem extends SimpleConversation {

	private MenuKeys menuAcces;
	private final MenuKeys menuKey;
	private final String lootTable;
	private final String itemToEdit;

	public SeachForItem(MenuKeys menuAcces, MenuKeys menuKey, String lootTable, String itemToEdit) {
		this.menuAcces = menuAcces;
		this.menuKey = menuKey;
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
			return SEACH_FOR_ITEM_TYPE_NAME.languageMessages();
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			if (menuAcces == ENTITY_TYPE_LISTMENU)
				new EntityTypeListMenu(menuKey, lootTable, itemToEdit, input).menuOpen(getPlayer(context));
			else
				new MatrialList(menuKey, itemToEdit, lootTable, input).menuOpen(getPlayer(context));

			return null;
		}
	}
}
