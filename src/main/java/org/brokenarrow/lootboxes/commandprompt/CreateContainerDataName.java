package org.brokenarrow.lootboxes.commandprompt;

import org.broken.arrow.prompt.library.SimpleConversation;
import org.broken.arrow.prompt.library.SimplePrompt;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.containerdata.ModifyContainerData;
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
		super(Lootboxes.getInstance());
		this.material = material;
	}

	@Override
	public Prompt getFirstPrompt() {
		return new PromptInput();
	}


	public class PromptInput extends SimplePrompt {


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
			new ModifyContainerData().menuOpen(getPlayer(context));
			return null;
		}
	}
}
