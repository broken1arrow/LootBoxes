package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.untlity.errors.Valid;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SimplePromp extends ValidatingPrompt implements Cloneable {

	protected abstract String getPrompt(ConversationContext context);

	@NotNull
	@Override
	public final String getPromptText(final @NotNull ConversationContext context) {
		return getPrompt(context);
	}

	@Override
	protected boolean isInputValid(@NotNull ConversationContext context, @Nullable String input) {
		return true;
	}

	/**
	 * Called when the whole conversation is over. This is called before {@link SimpleConversation#onConversationEnd(ConversationAbandonedEvent)}
	 *
	 * @param conversation
	 * @param event
	 */
	public void onConversationEnd(final SimpleConversation conversation, final ConversationAbandonedEvent event) {
	}

	/**
	 * Converts the {@link ConversationContext} into a {@link Player}
	 * or throws an error if it is not a player
	 *
	 * @param ctx conversation context.
	 * @return
	 */
	protected final Player getPlayer(final ConversationContext ctx) {
		Valid.checkBoolean(ctx.getForWhom() instanceof Player, "Conversable is not a player but: " + ctx.getForWhom());

		return (Player) ctx.getForWhom();
	}

	@Nullable
	@Override
	public final Prompt acceptInput(@NotNull ConversationContext context, String input) {
		if (isInputValid(context, input))
			return acceptValidatedInput(context, input);

		else {
			final String failPrompt = getFailedValidationText(context, input);

			/*if (failPrompt != null)
				tellLater(1, context.getForWhom(), Variables.replace("&c" + failPrompt, getPlayer(context)));*/

			// Redisplay this prompt to the user to re-collect input
			return this;
		}
	}

	@Override
	public SimplePromp clone() {
		try {
			// TODO: copy mutable state here, so the clone can't change the internals of the original
			return (SimplePromp) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

}
