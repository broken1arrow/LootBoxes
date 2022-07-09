package org.brokenarrow.lootboxes.builder;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.untlity.ConvetParticlesUntlity;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ParticleEffect implements ConfigurationSerializable {

	private final Particle particle;
	private final Effect effect;
	private final Material material;
	private final int data;
	private final Class<?> dataType;

	private final ParticleDustOptions particleDustOptions;
	private final Builder builder;

	private ParticleEffect(final Builder builder) {
		this.particle = builder.particle;
		this.effect = builder.effect;
		this.material = builder.material;
		this.data = builder.data;
		this.dataType = builder.dataType;
		this.particleDustOptions = builder.dustOptions;
		this.builder = builder;
	}


	@Nullable
	public Particle getParticle() {
		return particle;
	}

	@Nullable
	public Effect getEffect() {
		return effect;
	}

	@Nullable
	public Material getMaterial() {
		return material;
	}

	public int getData() {
		return data;
	}

	public Class<?> getDataType() {
		return dataType;
	}
	
	public ParticleDustOptions getParticleDustOptions() {
		return particleDustOptions;
	}

	public Builder getBuilder() {
		return builder;
	}


	public static class Builder {
		private Particle particle;
		private Effect effect;
		private Material material;
		private int data;
		private Class<?> dataType;
		private ParticleDustOptions dustOptions;


		public Builder setParticle(final Particle particle) {
			this.particle = particle;
			return this;
		}

		public Builder setEffect(final Effect effect) {
			this.effect = effect;
			return this;
		}

		public Builder setMaterial(final Material material) {
			this.material = material;
			return this;
		}

		public Builder setDataType(final Class<?> dataType) {
			this.dataType = dataType;
			return this;
		}

		public Builder setData(final int data) {
			this.data = data;
			return this;
		}

		public Builder setDustOptions(final ParticleDustOptions dustOptions) {
			this.dustOptions = dustOptions;
			return this;
		}


		public ParticleEffect build() {
			return new ParticleEffect(this);
		}
	}

	@Override
	public String toString() {
		return "ParticleEffect{" +
				"particle=" + particle +
				", effect=" + effect +
				", material=" + material +
				", data=" + data +
				", dataType=" + dataType +
				", particleDustOptions=" + particleDustOptions +
				", builder=" + builder +
				'}';
	}

	@NotNull
	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> particleData = new LinkedHashMap<>();
		particleData.put("Particle", this.particle + "");
		particleData.put("Effect", this.effect + "");
		particleData.put("Material", this.material + "");
		particleData.put("Data", this.data);
		final ParticleDustOptions dustOptions = this.particleDustOptions;
		if (dustOptions != null) {
			if (Lootboxes.getInstance().getServerVersion().newerThan(ServerVersion.Version.v1_16) && dustOptions.getToColor() != null) {
				particleData.put("Transition", new ParticleDustOptions(dustOptions.getFromColor(), dustOptions.getToColor(), dustOptions.getSize()));
			} else {
				particleData.put("DustOptions", new ParticleDustOptions(dustOptions.getFromColor(), dustOptions.getSize()));
			}

		}
		return particleData;
	}

	public static ParticleEffect deserialize(final Map<String, Object> map) {

		final Particle particle = ConvetParticlesUntlity.getParticle((String) map.get("Particle"));
		final Effect effect = ConvetParticlesUntlity.getEffect((String) map.get("Effect"));
		String icon = (String) map.get("Material");
		Material material = null;
		if (icon != null) {
			icon = icon.toUpperCase();
			material = Material.getMaterial(icon);
		}
		final int data = (int) map.get("Data");
		Class<?> dataType = null;
		if (particle != null)
			dataType = particle.getDataType();
		if (effect != null)
			dataType = effect.getData();
		ParticleDustOptions options = (ParticleDustOptions) map.get("DustOptions");

		final Builder builder = new Builder();
		if (options == null)
			options = (ParticleDustOptions) map.get("Transition");

		return builder
				.setParticle(particle)
				.setEffect(effect)
				.setMaterial(material)
				.setDustOptions(options)
				.setData(data)
				.setDataType(dataType)
				.build();
	}

	/*   if you uncommit this you get class not found error */
	/*public Particle.DustOptions something(ParticleDustOptions options) {
		if (ServerVersion.newerThan(ServerVersion.v1_16))
			return new Particle.DustTransition(Color.FUCHSIA, Color.AQUA, 1);
		return null;
	}*/

}
