package org.brokenarrow.lootboxes.builder;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.untlity.ConvertParticlesUnity;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.brokenarrow.lootboxes.untlity.particles.SpigotParticle;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ParticleEffect implements ConfigurationSerializable {

	private final SpigotParticle spigotParticle;
	private final Effect effect;
	private final Material material;
	private final int data;
	private final Class<?> dataType;
	private final ParticleDustOptions particleDustOptions;
	private final Builder builder;

	private ParticleEffect(final Builder builder) {
		this.spigotParticle = builder.spigotParticle;
		this.effect = builder.effect;
		this.material = builder.material;
		this.data = builder.data;
		this.dataType = builder.dataType;
		this.particleDustOptions = builder.dustOptions;
		this.builder = builder;
	}

	public SpigotParticle getSpigotParticle() {
		return spigotParticle;
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
		if (spigotParticle != null)
			return spigotParticle.getDataType();
		return dataType;
	}

	public ParticleDustOptions getParticleDustOptions() {
		return particleDustOptions;
	}

	public Builder getBuilder() {
		return builder;
	}


	public static class Builder {
		private SpigotParticle spigotParticle;
		private Effect effect;
		private Material material;
		private int data;
		private Class<?> dataType;
		private ParticleDustOptions dustOptions;

		public Builder setSpigotParticle(final SpigotParticle spigotParticle) {
			this.spigotParticle = spigotParticle;
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
				"particle=" + spigotParticle +
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
		if (this.spigotParticle != null)
			particleData.put("Particle", this.spigotParticle.getParticle() + "");
		else
			particleData.put("Particle", "");
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

		final String particle = (String) map.get("Particle");
		final Effect effect = ConvertParticlesUnity.getEffect((String) map.get("Effect"));
		String icon = (String) map.get("Material");
		Material material = null;
		if (icon != null) {
			icon = icon.toUpperCase();
			material = Material.getMaterial(icon);
		}
		final int data = (int) map.get("Data");
		Class<?> dataType = null;
		if (effect != null)
			dataType = effect.getData();
		ParticleDustOptions options = (ParticleDustOptions) map.get("DustOptions");

		final Builder builder = new Builder();
		if (options == null)
			options = (ParticleDustOptions) map.get("Transition");

		return builder
				.setSpigotParticle(new SpigotParticle(particle))
				.setEffect(effect)
				.setMaterial(material)
				.setDustOptions(options)
				.setData(data)
				.setDataType(dataType)
				.build();
	}

}
