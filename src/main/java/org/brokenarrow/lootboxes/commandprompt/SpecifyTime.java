package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.menus.ModifyContinerData;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.settings.ChatMessages.SPECIFY_TIME_CONFIRM;
import static org.brokenarrow.lootboxes.settings.ChatMessages.SPECIFY_TIME_TYPE_TIME;

public class SpecifyTime extends SimpleConversation {

	private final String container;
	private final ContainerData containerData = ContainerData.getInstance();

	public SpecifyTime(String container) {
		this.container = container;
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new PromptInput();
	}

	public class PromptInput extends SimplePromp {

		@Override
		protected String getPrompt(ConversationContext context) {
			return SPECIFY_TIME_TYPE_TIME.languageMessages();
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			ContainerDataBuilder data = containerData.getCacheContainerData(String.valueOf(container));
			if (data != null) {
				ContainerDataBuilder.Builder builder = data.getBuilder();
				builder.setCooldown(Long.parseLong(input));
				containerData.setContainerData(container, builder.build());
				SPECIFY_TIME_CONFIRM.sendMessage(getPlayer(context), input);
			}
			new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(getPlayer(context));
			return null;
		}
	}
}
