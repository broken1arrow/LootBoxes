package org.brokenarrow.lootboxes.untlity;

import com.google.common.base.Enums;
import org.bukkit.Effect;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;

public class ConvetParticlesUntlity {

	public static List<Particle> convertStringList(final List<String> particles) {
		final List<Particle> particleList = new ArrayList<>();
		for (final String particle : particles) {
			final Particle partic = getParticle(particle);
			if (partic != null)
				particleList.add(partic);
		}
		return particleList;
	}

	public static Particle getParticle(String particle) {
		if (particle == null) return null;
		final Particle[] particles = Particle.values();
		particle = particle.toUpperCase();

		for (final Particle partic : particles) {
			if (partic.name().equals(particle))
				return partic;
		}
		return null;
	}

	public static Effect checkParticleOld(final String particle) {
		if (ServerVersion.olderThan(ServerVersion.v1_9)) {
			if (particle != null && Enums.getIfPresent(Effect.class, particle).orNull() != null)
				return Effect.valueOf(particle);

		} else
			return null;
		return null;
	}
}
