package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.ModifyContinerData;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class CreateContainerDataName extends SimpleConversation {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final Material material;

	public CreateContainerDataName(Material material) {
		this.material = material;
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new PromptInput();
	}


	public class PromptInput extends SimplePromp {


		@Override
		protected String getPrompt(ConversationContext context) {
			return CREATE_CONTAINER_DATA_NAME_CREATE_NEW.languageMessagePrefix();
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {

			if (containerDataCache.containsContainerData(input)) {
				CREATE_CONTAINER_DATA_NAME_ALREDY_EXIST.sendMessage(getPlayer(context), input);
				return getFirstPrompt();
			}
			CREATE_CONTAINER_DATA_NAME_CONFIRM.sendMessage(getPlayer(context), input);
			containerDataCache.setNewContainerData(input, material);
			new ModifyContinerData().menuOpen(getPlayer(context));
			return null;
		}
	}
}
