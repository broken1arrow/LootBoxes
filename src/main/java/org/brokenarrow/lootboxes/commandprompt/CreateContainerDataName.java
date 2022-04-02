package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.menus.ModifyContinerData;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateContainerDataName extends SimpleConversation {
	private final ContainerData containerData = ContainerData.getInstance();
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
			return "type the name on the containers list ";
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			System.out.println("contains " + containerData.containsContainerData(input));
			if (containerData.containsContainerData(input)) {
				getPlayer(context).sendMessage(getPlayer(context).getUniqueId(), "this name alredy exist");
				return getFirstPrompt();
			}
			containerData.putCacheContainerData(input, material);
			new ModifyContinerData().menuOpen(getPlayer(context));
			return null;
		}
	}
}
