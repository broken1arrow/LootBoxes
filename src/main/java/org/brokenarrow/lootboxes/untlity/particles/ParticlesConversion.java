package org.brokenarrow.lootboxes.untlity.particles;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.brokenarrow.lootboxes.untlity.ServerVersion.Version;
import org.bukkit.Effect;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.ConvertParticlesUnity.getEffect;
import static org.brokenarrow.lootboxes.untlity.ConvertParticlesUnity.getParticle;

public class ParticlesConversion {


	public List<Object> convertStringList(final List<String> particles) {
		if (particles == null) return null;
		ServerVersion version = Lootboxes.getInstance().getServerVersion();
		final List<Object> particleList = new ArrayList<>();
		for (final String particle : particles) {
			final Effect effect = getEffect(particle);

			if (version.atLeast(Version.v1_9)) {
				final Particle particleEffect = getParticle(particle);
				if (particleEffect != null)
					particleList.add(particleEffect);
				else {
					if (effect != null) {
						particleList.add(effect);
					}
				}
			} else {
				if (effect != null) {
					particleList.add(effect);
				}
			}
		}
		return particleList;
	}

	public  Map<Object, ParticleEffect> convertToParticleEffect(List<?> objectList) {
		ServerVersion version = Lootboxes.getInstance().getServerVersion();
		Map<Object, ParticleEffect> map = new HashMap<>();
		if (objectList == null || objectList.isEmpty()) return map;

		for (final Object particle : objectList) {
			final ParticleEffect.Builder builder = new ParticleEffect.Builder();
			if (version.atLeast(Version.v1_9) && particle instanceof Particle) {
				final Particle part = (Particle) particle;
				builder.setSpigotParticle(new SpigotParticle(part)).setDataType(part.getDataType());
			} else if (particle instanceof Effect) {

				final Effect part = (Effect) particle;
				builder.setEffect(part).setDataType(part.getData());
			}
			if ((version.atLeast(Version.v1_9) && particle instanceof Particle) || particle instanceof Effect)
				map.put(particle, builder.build());
		}

		return map;
	}

	public List<Object> convertParticleEffectList(final List<ParticleEffect> particles) {
		if (particles == null) return null;
		ServerVersion version = Lootboxes.getInstance().getServerVersion();

		final List<Object> particleList = new ArrayList<>();
		for (final ParticleEffect particle : particles) {
			if (particle == null) continue;

			if (version.atLeast(Version.v1_9) && particle.getSpigotParticle() != null) {
				final Particle particleEffect = particle.getSpigotParticle().getParticle();
				if (particleEffect != null)
					particleList.add(particleEffect);
				else {
					final Effect effect = particle.getEffect();
					if (effect != null) {
						particleList.add(effect);
					}
				}
			} else {
				final Effect effect = particle.getEffect();
				if (effect != null) {
					particleList.add(effect);
				}
			}
		}
		return particleList;
	}

	public Object getParticleOrEffect(Object particle) {
		if (particle == null) return null;

		if (Lootboxes.getInstance().getServerVersion().olderThan(ServerVersion.Version.v1_9)) {
			return getEffect(String.valueOf(particle));
		} else {
			return getParticle(String.valueOf(particle));
		}
	}
}
