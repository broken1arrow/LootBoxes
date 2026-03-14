package org.brokenarrow.lootboxes.commandprompt;

import org.broken.arrow.library.prompt.SimpleConversation;
import org.broken.arrow.library.prompt.SimplePrompt;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class ContainerDataLinkedLootTable extends SimpleConversation {
	private final ContainerDataCache container = Lootboxes.getInstance().getContainerDataCache();
	private final LootContainerData containerData;
	private final String containerKey;
;

	public ContainerDataLinkedLootTable(LootContainerData containerData, String containerKey) {
		super(Lootboxes.getInstance());
		this.containerData = containerData;
		this.containerKey = containerKey;
	}

	@Override
	public Prompt getFirstPrompt() {
		return new Commandprompt();
	}

	public class Commandprompt extends SimplePrompt {

		@Override
		protected String getPrompt(ConversationContext context) {
			return null;
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {

			if (!containerData.getLootTableLinked().isEmpty())
				CONTAINER_DATA_LINKED_LOOTTABLE_CANGE_NAME.sendMessage(getPlayer(context), containerData.getLootTableLinked(), input);
			else {
				CONTAINER_DATA_LINKED_LOOTTABLE_NEW_LOOTTABLE.sendMessage(getPlayer(context), input);
			}
			if (containerData.getLootTableLinked().equals(input))
				CONTAINER_DATA_LINKED_LOOTTABLE_NEW_NAME_IS_SAME.sendMessage(getPlayer(context), containerData.getLootTableLinked(), input);

			container.write(containerKey, (Consumer<LootContainerData.LootContainerBuilder>) builder -> builder.setContainerDataLinkedToLootTable(input));
			return null;
		}
	}
}
