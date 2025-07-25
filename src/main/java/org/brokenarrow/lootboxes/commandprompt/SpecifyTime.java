package org.brokenarrow.lootboxes.commandprompt;

import org.broken.arrow.library.prompt.SimpleConversation;
import org.broken.arrow.library.prompt.SimplePrompt;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.settings.ChatMessages.SPECIFY_TIME_CONFIRM;
import static org.brokenarrow.lootboxes.settings.ChatMessages.SPECIFY_TIME_TYPE_TIME;

public class SpecifyTime extends SimpleConversation {

	private final String container;
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();

	public SpecifyTime(String container) {
		super(Lootboxes.getInstance());
		this.container = container;
	}

	@Override
	public Prompt getFirstPrompt() {
		return new PromptInput();
	}

	public class PromptInput extends SimplePrompt {

		@Override
		protected String getPrompt(ConversationContext context) {
			return SPECIFY_TIME_TYPE_TIME.languageMessagePrefix();
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			ContainerDataBuilder data = containerDataCache.getCacheContainerData(String.valueOf(container));
			if (data != null) {
				ContainerDataBuilder.Builder builder = data.getBuilder();
				try {
					builder.setCooldown(Long.parseLong(input));
				} catch (NumberFormatException e) {
					getPlayer(context).sendRawMessage("this " + input + " are not valid number");
					return getFirstPrompt();
				}
				containerDataCache.setContainerData(container, builder.build());
				SPECIFY_TIME_CONFIRM.sendMessage(getPlayer(context), input);
			}
			new AlterContainerDataMenu(container).menuOpen(getPlayer(context));
			return null;
		}
	}
}
