package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.menus.ParticleSettings;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.menus.ParticleSettings.Type.*;
import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class SetNumbers extends SimpleConversation {

	private final ParticleSettings.Type dataType;

	public SetNumbers(ParticleSettings.Type dataType) {
		this.dataType = dataType;
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new FirstNumberValue();
	}


	public class FirstNumberValue extends SimplePromp {

		@Override
		protected String getPrompt(ConversationContext context) {
			if (dataType == SETDATA)
				return SET_DATA_ON_PARTICLE_START_TYPE.languageMessagePrefix();
			if (dataType == SETCOLORS)
				return SET_COLOR_ON_PARTICLE_START_TYPE.languageMessages();
			if (dataType == SET_PARTICLE_SIZE)
				return SET_PARTICLE_SIZE_START_TYPE.languageMessages();
			return null;
		}


		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			if (dataType == SETDATA) {

				return null;
			}
			if (dataType == SETCOLORS) {

				return null;
			}
			if (dataType == SET_PARTICLE_SIZE) {

				return null;
			}
			return null;
		}
	}
}
