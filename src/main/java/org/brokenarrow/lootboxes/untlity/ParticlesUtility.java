package org.brokenarrow.lootboxes.untlity;

import org.brokenarrow.lootboxes.untlity.ServerVersion.Version;
import org.bukkit.Particle;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParticlesUtility {

	private final ServerVersion version;
	private List<Object> particleList;

	public ParticlesUtility(ServerVersion version) {
		this.version = version;
	}

	public List<Object> getParticlesList(String particleToSearchFor) {
		if (particleToSearchFor != null && !particleToSearchFor.isEmpty())
			return this.particleList.stream().map(Particle.class::cast).filter((effect) -> effect.name().contains(particleToSearchFor.toUpperCase())).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
		return particleList;
	}

	public List<Object> getParticles() {
		this.particleList = Stream.of(Particle.values()).filter(this::sortOut).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
		return this.particleList;
	}

	public boolean sortOut(final Particle particle) {
		return !(particle.name().equals("LEGACY_BLOCK_CRACK")
				|| particle.name().equals("LEGACY_BLOCK_DUST") || particle.name().equals("LEGACY_FALLING_DUST")
				|| particle.name().equals("MOB_APPEARANCE"));
	}

	public boolean checkIfParticle(final Object particle) {
		return (version.atLeast(Version.v1_9) && particle instanceof Particle);
	}

	public boolean checkParticleClass(final Object particle, Class<?> clazz) {
		return (version.atLeast(Version.v1_9) && particle instanceof Particle) && ((Particle) particle).getDataType() == clazz;
	}
}
