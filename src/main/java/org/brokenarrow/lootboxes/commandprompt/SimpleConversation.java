package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.untlity.errors.Valid;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class SimpleConversation implements ConversationAbandonedListener {


	private String prefix;
	private int timeout = 60;

	protected abstract Prompt getFirstPrompt();


	/**
	 * Start a conversation with the player, throwing error if {@link Player#isConversing()}
	 *
	 * @param player player some shall start conversing.
	 */
	public final void start(final Player player) {
		Valid.checkBoolean(!player.isConversing(), "Player " + player.getName() + " is already conversing!");

		// Do not allow open inventory since they cannot type anyways
		player.closeInventory();

		// Setup
		final CustomConversation conversation = new CustomConversation(player);
		final CustomCanceller canceller = new CustomCanceller(Lootboxes.getInstance(), this.timeout);

		canceller.setConversation(conversation);

		conversation.getCancellers().add(canceller);
		conversation.getCancellers().add(getCanceller());

		conversation.addConversationAbandonedListener(this);

		conversation.begin();
	}

	/**
	 * Return the canceller that listens for certain words to exit the convo,
	 * by default we use {@link SimpleCanceller} that listens to quit|cancel|exit
	 *
	 * @return cancel message.
	 */
	protected ConversationCanceller getCanceller() {
		return new SimpleCanceller("quit", "cancel", "exit");
	}

	@Override
	public void conversationAbandoned(@NotNull ConversationAbandonedEvent event) {
		final ConversationContext context = event.getContext();
		final Conversable conversing = context.getForWhom();

		final Object source = event.getSource();
		final boolean timeout = (boolean) context.getAllSessionData().getOrDefault("FLP#TIMEOUT", false);

		// Remove the session data so that they are invisible to other plugnis
		context.getAllSessionData().remove("FLP#TIMEOUT");

		if (source instanceof CustomConversation) {
			final SimplePromp lastPrompt = ((CustomConversation) source).getLastSimplePrompt();

			if (lastPrompt != null)
				lastPrompt.onConversationEnd(this, event);
		}

		onConversationEnd(event, timeout);
	/*	if (conversing instanceof Player) {
			final Player player = (Player) conversing;

			(event.gracefulExit() ? CompSound.SUCCESSFUL_HIT : CompSound.NOTE_BASS).play(player, 1F, 1F);

			if (menuToReturnTo != null && reopenMenu())
				menuToReturnTo.newInstance().displayTo(player);
		}*/
	}

	/**
	 * Fired when the user quits this conversation (see {@link #getCanceller()}, or
	 * simply quits the game)
	 *
	 * @param event                  some get called when conversation ended.
	 * @param canceledFromInactivity true if user failed to enter input in the period set in {@link #getTimeout()}
	 */
	protected void onConversationEnd(final ConversationAbandonedEvent event, boolean canceledFromInactivity) {
		this.onConversationEnd(event);
	}

	/**
	 * Called when the whole conversation is over. This is called before {@link SimpleConversation#onConversationEnd(ConversationAbandonedEvent)}
	 *
	 * @param conversation message send from server to player.
	 * @param event        some get called when conversation ended.
	 */
	protected void onConversationEnd(final SimpleConversation conversation, final ConversationAbandonedEvent event) {
	}

	/**
	 * Fired when the user quits this conversation (see {@link #getCanceller()}, or
	 * simply quits the game)
	 *
	 * @param event some get called when conversation ended.
	 */
	protected void onConversationEnd(final ConversationAbandonedEvent event) {
	}

	/**
	 * Get conversation prefix before each message
	 * <p>
	 * By default we use the plugins tell prefix
	 * <p>
	 * TIP: You can use {@link SimplePrefix}
	 *
	 * @return prefix you set or the plugin name if not set a prefix.
	 */
	protected ConversationPrefix getPrefix() {
		return new SimplePrefix(this.prefix != null ? this.prefix : Lootboxes.getInstance().getName());
	}

	protected void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	protected boolean insertPrefix() {
		return this.prefix != null && !this.prefix.isEmpty();
	}

	protected int getTimeout() {
		return timeout;
	}

	protected void setTimeout(int timeout) {
		this.timeout = timeout;
	}


	public class CustomConversation extends Conversation {
		private SimplePromp lastSimplePrompt;

		public CustomConversation(@NotNull Conversable forWhom) {
			super(Lootboxes.getInstance(), forWhom, SimpleConversation.this.getFirstPrompt());
			localEchoEnabled = false;

			if (insertPrefix() && SimpleConversation.this.getPrefix() != null)
				prefix = SimpleConversation.this.getPrefix();

		}

		public SimplePromp getLastSimplePrompt() {
			return lastSimplePrompt;
		}

		@Override
		public void outputNextPrompt() {
			if (currentPrompt == null)
				abandon(new ConversationAbandonedEvent(this));

			else {
				// Save the time when we showed the question to the player
				// so that we only show it once per the given threshold
				final String promptClass = currentPrompt.getClass().getSimpleName();
				final String question = currentPrompt.getPromptText(context);

				try {
					//	Map<String, Void /*dont have expiring set class*/> askedQuestions = new HashMap<>();
					//	askedQuestions.put()
					final Object askedQuestions = context.getAllSessionData().getOrDefault("Asked_" + promptClass, getTimeout());

					if (!askedQuestions.equals(question)) {
						//askedQuestions.put(question, null);

						context.setSessionData("Asked_" + promptClass, askedQuestions);
						context.getForWhom().sendRawMessage(prefix.getPrefix(context) + question);
					}
				} catch (final NoSuchMethodError ex) {
					// Unfortunately, old MC version was detected
				}

				// Save last prompt if it is our class
				if (currentPrompt instanceof SimplePromp)
					lastSimplePrompt = ((SimplePromp) currentPrompt).clone();

				if (!currentPrompt.blocksForInput(context)) {
					currentPrompt = currentPrompt.acceptInput(context, null);
					outputNextPrompt();
				}
			}
		}
	}

	private final class CustomCanceller extends InactivityConversationCanceller {

		/**
		 * Creates an InactivityConversationCanceller.
		 *
		 * @param plugin         The owning plugin.
		 * @param timeoutSeconds The number of seconds of inactivity to wait.
		 */
		public CustomCanceller(@NotNull Plugin plugin, int timeoutSeconds) {
			super(plugin, timeoutSeconds);
		}

		@Override
		protected void cancelling(Conversation conversation) {
			conversation.getContext().setSessionData("FLP#TIMEOUT", true);
		}
	}

	public final class SimpleCanceller implements ConversationCanceller {
		private final List<String> cancelPhrases;

		public SimpleCanceller(String... cancelPhrases) {
			this(Arrays.asList(cancelPhrases));
		}

		public SimpleCanceller(List<String> cancelPhrases) {
			Valid.checkBoolean(!cancelPhrases.isEmpty(), "Cancel phrases are empty for conversation cancel listener!");

			this.cancelPhrases = cancelPhrases;
		}

		@Override
		public void setConversation(@NotNull Conversation conversation) {

		}

		@Override
		public boolean cancelBasedOnInput(@NotNull ConversationContext context, @NotNull String input) {
			for (final String phrase : this.cancelPhrases)
				if (input.equalsIgnoreCase(phrase))
					return true;

			return false;
		}

		@NotNull
		@Override
		public ConversationCanceller clone() {
			return new SimpleCanceller(cancelPhrases);
		}
	}

	public final class SimplePrefix implements ConversationPrefix {

		private final String prefix;

		public SimplePrefix(String prefix) {
			this.prefix = prefix;
		}

		@NotNull
		@Override
		public String getPrefix(@NotNull ConversationContext context) {
			return this.prefix;
		}
	}
}
