package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.ParticleDustOptions;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.ParticleSettings;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.builder.ParticleDustOptions.convertToColor;
import static org.brokenarrow.lootboxes.menus.ParticleSettings.Type.*;
import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class SetNumbers extends SimpleConversation {

	private final ParticleSettings.Type dataType;
	private final Object particle;
	private final String container;
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final ContainerDataBuilder data;

	public SetNumbers(ParticleSettings.Type dataType, String container, Object particle) {
		this.container = container;
		this.data = containerDataCache.getCacheContainerData(container);
		this.dataType = dataType;
		this.particle = particle;
	}

	@Override
	protected void onConversationEnd(ConversationAbandonedEvent event) {
		if (event.getContext().getForWhom() instanceof Player)
			new ParticleSettings(container, particle).menuOpen((Player) event.getContext().getForWhom());
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new FirstNumberValue();
	}


	public class FirstNumberValue extends SimplePromp {

		@Override
		protected String getPrompt(ConversationContext context) {
			if (dataType == SET_DATA)
				return SET_DATA_ON_PARTICLE_START_TYPE.languageMessagePrefix();
			if (dataType == SET_COLORS)
				return SET_COLOR_ON_PARTICLE_START_TYPE.languageMessages();
			if (dataType == SET_PARTICLE_SIZE)
				return SET_PARTICLE_SIZE_START_TYPE.languageMessages();
			return null;
		}


		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {

			if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("cancel")) {
				new ParticleSettings(container, particle).menuOpen(getPlayer(context));
				return null;
			}

			final ParticleEffect particleEffect = data.getParticleEffect(particle);
			if (particleEffect == null) return null;
			if (dataType == SET_DATA) {
				float number = numberCheck(getPlayer(context), input);
				if (number == -1) {
					SET_DATA_ON_PARTICLE_ZERO_OR_LESS.sendMessage(getPlayer(context), number);
					return new FirstNumberValue();
				}
				final ParticleEffect.Builder particleBuilder = particleEffect.getBuilder();
				particleBuilder.setData(Integer.parseInt(input));
				containerDataCache.setParticleEffects(container, particle, particleBuilder);
			}
			if (dataType == SET_COLORS) {
				final ParticleEffect.Builder particleBuilder = particleEffect.getBuilder();
				final ParticleDustOptions dustOptions = particleEffect.getParticleDustOptions();

				particleBuilder.setDustOptions(new ParticleDustOptions(convertToColor(input), dustOptions == null || dustOptions.getSize() <= 0 ? (float) 0.5 : dustOptions.getSize()));
				containerDataCache.setParticleEffects(container, particle, particleBuilder);
				if (Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_17))
					return new SecondNumberValue();
			}
			if (dataType == SET_PARTICLE_SIZE) {
				float number = numberCheck(getPlayer(context), input);
				if (number == -1) {
					SET_PARTICLE_SIZE_ZERO_OR_LESS.sendMessage(getPlayer(context), number);
					return new FirstNumberValue();
				}

				final ParticleEffect.Builder particleBuilder = particleEffect.getBuilder();
				particleBuilder.setDustOptions(buildParticleEffect(particleEffect.getParticleDustOptions(), number));

				containerDataCache.setParticleEffects(container, particle, particleBuilder);
			}
			new ParticleSettings(container, particle).menuOpen(getPlayer(context));
			return null;
		}
	}

	public class SecondNumberValue extends SimplePromp {

		@Override
		protected String getPrompt(ConversationContext context) {
			return SET_PARTICLE_SIZE_NEXT_COLOR.languageMessages();
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			final ParticleEffect particleEffect = data.getParticleEffect(particle);
			if (particleEffect == null) return null;
			ParticleDustOptions dustOptions = particleEffect.getParticleDustOptions();
			final ParticleEffect.Builder particleBuilder = particleEffect.getBuilder();

			particleBuilder.setDustOptions(new ParticleDustOptions(dustOptions.getFromColor(), convertToColor(input), dustOptions.getSize() <= 0 ? (float) 0.5 : dustOptions.getSize()));
			containerDataCache.setParticleEffects(container, particle, particleBuilder);
			return null;
		}
	}

	public ParticleDustOptions buildParticleEffect(ParticleDustOptions dustOptions, float input) {
		if (dustOptions == null) return null;

		if (dustOptions.getToColor() != null)
			return new ParticleDustOptions(dustOptions.getFromColor(), dustOptions.getToColor(), input);
		else
			return new ParticleDustOptions(dustOptions.getFromColor(), input);

	}

	public float numberCheck(Player player, String input) {
		try {
			return Float.parseFloat((input));
		} catch (NumberFormatException e) {
			NOT_VALID_NUMBER.sendMessage(player, input);
		}

		return -1;
	}
}
