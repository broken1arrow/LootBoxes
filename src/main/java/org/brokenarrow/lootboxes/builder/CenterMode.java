package org.brokenarrow.lootboxes.builder;

import org.brokenarrow.lootboxes.settings.ChatMessages;
import org.jetbrains.annotations.NotNull;

public enum CenterMode {
    PLAYER_FOLLOW,
    PLAYER_ORIGIN,
    WORLD_ORIGIN;

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
        switch (this) {
            case PLAYER_FOLLOW:
                return ChatMessages.CENTER_MODE_DESCRIPTION_PLAYER_FOLLOW.languageMessages();
            case PLAYER_ORIGIN:
                return ChatMessages.CENTER_MODE_DESCRIPTION_PLAYER_ORIGIN.languageMessages();
            case WORLD_ORIGIN:
                return ChatMessages.CENTER_MODE_DESCRIPTION_WORLD_ORIGIN.languageMessages();
            default:
                return "";
        }
    }
}
