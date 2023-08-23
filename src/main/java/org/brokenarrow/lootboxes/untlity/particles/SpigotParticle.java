package org.brokenarrow.lootboxes.untlity.particles;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.untlity.ConvertParticlesUnity;
import org.brokenarrow.lootboxes.untlity.ServerVersion.Version;
import org.bukkit.Particle;

import javax.annotation.Nullable;

public class SpigotParticle {

	Particle particle;
	private Class<?> dataType;

	public SpigotParticle(final Particle particle) {
		this.particle = particle;
		if (particle == null) return;
		dataType = particle.getDataType();
	}

	public SpigotParticle(final Object particle) {
		if (particle == null) return;
		if (Lootboxes.getInstance().getServerVersion().atLeast(Version.v1_9)){
			this.particle = ConvertParticlesUnity.getParticle((String) particle);
			dataType = this.particle.getDataType();
		}
	}

	@Nullable
	public Particle getParticle() {
		return particle;
	}
	public Class<?> getDataType() {
		return dataType;
	}
}
