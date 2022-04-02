package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Particle;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParticleEffectList {


	private final List<Particle> particleList;

	public ParticleEffectList() {
		this.particleList = Stream.of(Particle.values()).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
		for (Particle effect : particleList) {
			System.out.println("particle: " + effect);
		}
	}

	public List<Particle> getParticleList(String enchantMentsToSearchFor) {
		if (enchantMentsToSearchFor != null && !enchantMentsToSearchFor.isEmpty())
			return particleList.stream().filter((effect) -> effect.name().contains(enchantMentsToSearchFor.toUpperCase())).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
		return particleList;
	}
}
