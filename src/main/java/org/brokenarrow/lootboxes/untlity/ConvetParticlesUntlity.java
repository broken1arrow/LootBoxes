package org.brokenarrow.lootboxes.untlity;

import com.google.common.base.Enums;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.bukkit.Effect;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;

public class ConvetParticlesUntlity {

	public static List<Object> convertStringList(final List<String> particles) {
		if (particles == null) return null;

		final List<Object> particleList = new ArrayList<>();
		for (final String particle : particles) {
			final Particle partic = getParticle(particle);
			if (partic != null)
				particleList.add(partic);
			else {
				final Effect effect = getEffect(particle);
				if (effect != null) {
					particleList.add(effect);
				}
			}
		}
		return particleList;
	}

	public static List<Object> convertParticleEffectList(final List<ParticleEffect> particles) {
		if (particles == null) return null;

		final List<Object> particleList = new ArrayList<>();
		for (final ParticleEffect particle : particles) {
			if (particle == null) continue;

			final Particle partic = particle.getParticle();
			if (partic != null)
				particleList.add(partic);
			else {
				final Effect effect = particle.getEffect();
				if (effect != null) {
					particleList.add(effect);
				}
			}
		}
		return particleList;
	}

	public static Object getParticleOrEffect(Object particle) {
		if (particle == null) return null;

		Particle partc = getParticle(String.valueOf(particle));
		if (partc != null)
			return partc;
		if (Lootboxes.getInstance().getServerVersion().olderThan(ServerVersion.Version.v1_9)) {
			return getEffect(String.valueOf(particle));
		}
		return null;
	}

	public static boolean isParticleThisClazz(Object particle, Class<?>... clazz) {
		if (particle == null) return false;
		for (Class<?> obj : clazz) {
			if (obj == null) continue;
			Particle partc = getParticle(String.valueOf(particle));

			if (partc != null) {
				if (obj == partc.getDataType()) {
					return true;
				}
			}
			if (Lootboxes.getInstance().getServerVersion().olderThan(ServerVersion.Version.v1_9)) {
				Effect effect = getEffect(String.valueOf(particle));
				if (effect != null)
					if (obj == effect.getData()) {
						return true;
					}
			}
		}
		return false;
	}

	public static Particle getParticle(String particle) {
		if (particle == null) return null;
		final Particle[] particles = Particle.values();
		particle = particle.toUpperCase();
		particle = replaceOldParticle(particle);

		for (final Particle partic : particles) {
			if (partic.name().equals(particle))
				return partic;
		}
		return null;
	}

	public static Effect getEffect(String particle) {
		if (particle == null) return null;
		final Effect[] effects = Effect.values();
		particle = particle.toUpperCase();

		for (final Effect effect : effects) {
			if (effect.name().equals(particle))
				return effect;
		}
		return null;
	}

	public static Effect.Type getEffectType(String effectType) {
		if (effectType == null) return null;
		final Effect[] effects = Effect.values();
		effectType = effectType.toUpperCase();

		for (final Effect effect : effects) {
			if (effect.getType().name().equals(effectType))
				return effect.getType();
		}
		return null;
	}

	public static String replaceOldParticle(final String particle) {
		ServerVersion version = Lootboxes.getInstance().getServerVersion();

		if (version.atLeast(ServerVersion.Version.v1_17))
			if (particle.equals("BARRIER"))
				return "BLOCK_MARKER";
		return particle;
	}

	public static Effect checkParticleOld(final String particle) {

		if (Lootboxes.getInstance().getServerVersion().olderThan(ServerVersion.Version.v1_9)) {
			if (particle != null && Enums.getIfPresent(Effect.class, particle).orNull() != null)
				return Effect.valueOf(particle);
		} else
			return null;
		return null;
	}
}
