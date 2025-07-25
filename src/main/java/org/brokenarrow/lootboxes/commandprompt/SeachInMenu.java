package org.brokenarrow.lootboxes.commandprompt;

import org.broken.arrow.library.prompt.SimpleConversation;
import org.broken.arrow.library.prompt.SimplePrompt;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.menus.EntityTypeListMenu;
import org.brokenarrow.lootboxes.menus.MaterialList;
import org.brokenarrow.lootboxes.menus.MenuKeys;
import org.brokenarrow.lootboxes.menus.ParticleAnimation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.menus.MenuKeys.ENTITY_TYPE_LISTMENU;
import static org.brokenarrow.lootboxes.menus.MenuKeys.PARTICLE_ANIMANTION;
import static org.brokenarrow.lootboxes.settings.ChatMessages.SEACH_FOR_ITEM_TYPE_NAME;

public class SeachInMenu extends SimpleConversation {

	private MenuKeys menuAccess;
	private final MenuKeys menuKey;
	private final String lootTable;
	private final Object itemToEdit;

	public SeachInMenu(final MenuKeys menuAccess, final MenuKeys menuKey, final String nameOfTableOrContainer, final Object itemToEdit) {
		super(Lootboxes.getInstance());
		this.menuAccess = menuAccess;
		this.menuKey = menuKey;
		this.lootTable = nameOfTableOrContainer;
		this.itemToEdit = itemToEdit;
	}

	@Override
	public Prompt getFirstPrompt() {
		return new itemSeach();
	}

	public class itemSeach extends SimplePrompt {
		public itemSeach() {

		}

		@Override
		protected String getPrompt(final ConversationContext context) {
			return SEACH_FOR_ITEM_TYPE_NAME.languageMessagePrefix();
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
			if (menuAccess == ENTITY_TYPE_LISTMENU)
				new EntityTypeListMenu(menuKey, lootTable, (String) itemToEdit, input).menuOpen(getPlayer(context));
			if (menuAccess == PARTICLE_ANIMANTION)
				new ParticleAnimation(lootTable, input).menuOpen(getPlayer(context));
			if (menuAccess == MenuKeys.ENTITY_TYPE_LISTMENU)
				new EntityTypeListMenu(menuKey, lootTable, (String) itemToEdit, input).menuOpen(getPlayer(context));
			else
				new MaterialList(menuKey, itemToEdit, lootTable, input).menuOpen(getPlayer(context));

			return null;
		}
	}
}
