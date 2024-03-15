package org.brokenarrow.lootboxes.commandprompt;

import org.broken.arrow.prompt.library.SimpleConversation;
import org.broken.arrow.prompt.library.SimplePrompt;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.SettingsContainerData;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.settings.ChatMessages.SET_PERMISSION;

public class SetPermission extends SimpleConversation {

    private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
    private final String container;

    public SetPermission(String container) {
        super(Lootboxes.getInstance());
        this.container = container;
    }

    @Override
    protected void onConversationEnd(ConversationAbandonedEvent event) {
        if (event.getContext().getForWhom() instanceof Player)
            new SettingsContainerData(container).menuOpen((Player) event.getContext().getForWhom());
    }

    @Override
    public Prompt getFirstPrompt() {
        return new FirstNumberValue();
    }

    public class FirstNumberValue extends SimplePrompt {

        @Override
        protected String getPrompt(ConversationContext context) {
            return SET_PERMISSION.languageMessages();
        }


        @Nullable
        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {

            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("cancel")) {
                new SettingsContainerData(container).menuOpen(getPlayer(context));
                return null;
            }
            ContainerDataBuilder cacheContainerData = containerDataCache.getCacheContainerData(container);
            ContainerDataBuilder.Builder builder = cacheContainerData.getBuilder();
            builder.setPermissionForRandomSpawn(input);
            containerDataCache.setContainerData(container, builder.build());
            new SettingsContainerData(container).menuOpen(getPlayer(context));
            return null;
        }
    }

}
