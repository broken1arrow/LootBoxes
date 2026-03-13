package org.brokenarrow.lootboxes.commandprompt;

import org.broken.arrow.library.prompt.SimpleConversation;
import org.broken.arrow.library.prompt.SimplePrompt;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.containerdata.WorldsAllowed;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.settings.ChatMessages.SELECT_WORLD_EXIST_PROMPT;
import static org.brokenarrow.lootboxes.settings.ChatMessages.SELECT_WORLD_PROMPT;

public class AddWorldPrompt extends SimpleConversation {
    private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
    private final String containerDataName;
    private final ContainerDataBuilder containerDataBuilder;

    public AddWorldPrompt(@NotNull final String containerDataName, @NotNull final ContainerDataBuilder containerDataBuilder) {
        super(Lootboxes.getInstance());

        this.containerDataName = containerDataName;
        this.containerDataBuilder = containerDataBuilder;
    }

    @Override
    public Prompt getFirstPrompt() {
        return new PromptAddWorld();
    }

    public class PromptAddWorld extends SimplePrompt {
        public PromptAddWorld() {

        }

        @Override
        protected String getPrompt(final ConversationContext context) {
            return SELECT_WORLD_PROMPT.languageMessagePrefix();
        }

        @Nullable
        @Override
        protected Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
            Player player = getPlayer();
            if (input.equals("exit") || input.equals("quit") || input.equals("q")) {
                if (player != null)
                    new WorldsAllowed(containerDataName).menuOpen(player);
                return null;
            }

            if (!input.isEmpty() && !containerDataBuilder.contains(input)) {
                containerDataBuilder.getBuilder().addWorld(input);
                containerDataCache.setContainerData(containerDataName, containerDataBuilder.getBuilder().addWorld(input).build());
            } else {
                SELECT_WORLD_EXIST_PROMPT.sendMessage(player);
                return new PromptAddWorld();
            }

            if (player != null)
                new WorldsAllowed(containerDataName).menuOpen(player);
            return null;
        }
    }
}
