package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContainerDataLinkedLootTable extends SimpleConversation {

	private ContainerDataBuilder containerData;
	private String key;
	private final ContainerData container = ContainerData.getInstance();

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
				getPlayer(context).sendRawMessage("You change the loottable from " + containerData.getLootTableLinked() + " to " + input);

			if (containerData.getLootTableLinked().equals(input))
				getPlayer(context).sendRawMessage("Your change do not change the loottable is same as the old, old " + containerData.getLootTableLinked() + " new name " + input);

			builder.setContainerDataLinkedToLootTable(input);
			container.setContainerData(key, builder.build());

			return null;
		}
	}
}
