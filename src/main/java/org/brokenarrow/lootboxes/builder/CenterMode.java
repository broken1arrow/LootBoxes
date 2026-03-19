package org.brokenarrow.lootboxes.builder;

import org.brokenarrow.lootboxes.settings.ChatMessages;
import org.jetbrains.annotations.NotNull;

public enum CenterMode {
    PLAYER_FOLLOW(ChatMessages.CENTER_MODE_DESCRIPTION_PLAYER_FOLLOW.languageMessages()),
    PLAYER_ORIGIN(ChatMessages.CENTER_MODE_DESCRIPTION_PLAYER_ORIGIN.languageMessages()),
    WORLD_ORIGIN(ChatMessages.CENTER_MODE_DESCRIPTION_WORLD_ORIGIN.languageMessages());

    private final String description;

    CenterMode(String description) {
        this.description = description;
    }

    @NotNull
    public static CenterMode of(String string) {
        if (string == null)
            return PLAYER_FOLLOW;
        String center = string.toUpperCase();
        try {
            return CenterMode.valueOf(center);
        } catch (IllegalArgumentException ignore) {
            return PLAYER_FOLLOW;
        }

    }

    public String getDescription() {
        return description;
    }
}
