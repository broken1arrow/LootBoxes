package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class ContainerDataLinkedLootTable extends SimpleConversation {

	private ContainerDataBuilder containerData;
	private String key;
	private final ContainerDataCache container = ContainerDataCache.getInstance();

	public ContainerDataLinkedLootTable(ContainerDataBuilder containerData, String key) {
		this.containerData = containerData;
		this.key = key;
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new Commandprompt();
	}

	public class Commandprompt extends SimplePromp {

		@Override
		protected String getPrompt(ConversationContext context) {
			return null;
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {


			ContainerDataBuilder.Builder builder = containerData.getBuilder();
			if (!containerData.getLootTableLinked().isEmpty())
				CONTAINER_DATA_LINKED_LOOTTABLE_CANGE_NAME.sendMessage(getPlayer(context), containerData.getLootTableLinked(), input);
			else {
				CONTAINER_DATA_LINKED_LOOTTABLE_NEW_LOOTTABLE.sendMessage(getPlayer(context), input);
			}
			if (containerData.getLootTableLinked().equals(input))
				CONTAINER_DATA_LINKED_LOOTTABLE_NEW_NAME_IS_SAME.sendMessage(getPlayer(context), containerData.getLootTableLinked(), input);

			builder.setContainerDataLinkedToLootTable(input);
			container.setContainerData(key, builder.build());

			return null;
		}
	}
}
