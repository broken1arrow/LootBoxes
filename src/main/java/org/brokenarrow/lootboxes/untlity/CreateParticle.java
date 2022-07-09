package org.brokenarrow.lootboxes.untlity;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ParticleDustOptions;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

public class CreateParticle {
	private final ServerVersion version = Lootboxes.getInstance().getServerVersion();
	private final Effect effect;
	private final Particle particle;
	private final Material material;
	private final Class<?> dataType;
	private final ParticleDustOptions particleDustOptions;
	private final int data;
	private final World world;
	private final double x;
	private final double y;
	private final double z;

	public CreateParticle(@NotNull ParticleEffect effect, @NotNull Location location) {
		this(effect, location.getWorld(), location.getX(), location.getY(), location.getZ());
	}

	public CreateParticle(@NotNull ParticleEffect effect, World world, double x, double y, double z) {
		this.particle = effect.getParticle();
		this.effect = effect.getEffect();
		this.material = effect.getMaterial();
		this.data = effect.getData();
		this.dataType = effect.getDataType();
		this.particleDustOptions = effect.getParticleDustOptions();
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void create() {
		ParticleDustOptions particleDustOptions = this.particleDustOptions;
		if (particleDustOptions != null) {
			if (version.newerThan(ServerVersion.Version.v1_16) && particleDustOptions.getToColor() != null)
				spawnDustTransitionParticle(new Particle.DustTransition(particleDustOptions.getFromColor(), particleDustOptions.getToColor(), particleDustOptions.getSize()));
			else
				spawnDustOptionsParticle(new Particle.DustOptions(particleDustOptions.getFromColor(), particleDustOptions.getSize()));
		} else
			checkTypeParticle();
	}

	public void spawnDustOptionsParticle(Particle.DustOptions dustOptions) {
		if (this.effect != null)
			this.world.playEffect(new Location(this.world, this.x, this.y, this.z), this.effect, this.data);
		else
			this.world.spawnParticle(particle, this.x, this.y, this.z, 0, 0.0, 0.0, 0.0, dustOptions);
	}

	public void spawnDustTransitionParticle(Particle.DustTransition dustOptions) {
		if (this.effect != null)
			this.world.playEffect(new Location(this.world, this.x, this.y, this.z), this.effect, this.data);
		else
			this.world.spawnParticle(particle, this.x, this.y, this.z, 0, 0.0, 0.0, 0.0, dustOptions);
	}

	public void checkTypeParticle() {
		if (this.effect != null)
			this.spawnEffect();
		else
			spawnParticle();
	}

	public void spawnEffect() {
		if (this.material != null) {
			if (this.dataType == Material.class)
				this.world.playEffect(new Location(this.world, this.x, this.y, this.z), this.effect, this.material);
			if (this.dataType == MaterialData.class)
				this.world.playEffect(new Location(this.world, this.x, this.y, this.z), this.effect, this.material.getData());
		} else
			this.world.playEffect(new Location(this.world, this.x, this.y, this.z), this.effect, this.data);
	}

	public void spawnParticle() {

		if (particle == null) return;
		if (this.material != null && this.dataType != Void.class) {
			if (this.dataType == BlockData.class)
				this.world.spawnParticle(particle, this.x, this.y, this.z, 0, 0.0, 0.0, 0.0, 3, this.material.createBlockData());
			if (this.dataType == ItemStack.class)
				this.world.spawnParticle(particle, this.x, this.y, this.z, 0, 0.0, 0.0, 0.0, 3, new ItemStack(this.material));
		} else if (this.dataType == Void.class)
			this.world.spawnParticle(particle, this.x, this.y, this.z, 0, 0.0, 0.0, 0.0, 3);
	}
}
