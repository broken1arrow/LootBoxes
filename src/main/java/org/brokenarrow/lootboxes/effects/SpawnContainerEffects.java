package org.brokenarrow.lootboxes.effects;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.api.HeavyLoad;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.listener.CheckChunkLoadUnload;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.CreateParticle;
import org.bukkit.Location;

import java.util.List;

public class SpawnContainerEffects implements HeavyLoad {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private CheckChunkLoadUnload checkChunkLoadUnload;
	private static final Lootboxes plugin = Lootboxes.getInstance();

	private final Location[] containerLocations;
	private final List<ParticleEffect> effectType;
	private final long time;
	private static int locationInlist;
	private final double MAX_MS_PER_TICK = 4.5;

	public SpawnContainerEffects(final List<ParticleEffect> effectType, final Integer runTime, final Location... containerLocations) {
		this.containerLocations = containerLocations.clone();
		this.effectType = effectType;
		time = System.currentTimeMillis() + (1000L * (runTime != null ? runTime : 10));
	}

	private void spawnEffects() {
		if (checkChunkLoadUnload == null)
			this.checkChunkLoadUnload = plugin.getCheckChunkLoadUnload();
		//for (Location containerLocation : containerLocations) {
		final long stoptime = (long) (System.nanoTime() + (1000_000 * MAX_MS_PER_TICK));
		while (System.nanoTime() <= stoptime) {
			if (locationInlist >= containerLocations.length) {
				locationInlist = 0;
				break;
			}
			final Location containerLocation = containerLocations[locationInlist];
			locationInlist++;
			if (containerLocation == null) return;
			if (this.checkChunkLoadUnload.getChunkData(containerLocation.getBlockX() >> 4, containerLocation.getBlockZ() >> 4) == null || !containerLocation.getWorld().isChunkLoaded(containerLocation.getBlockX() >> 4, containerLocation.getBlockZ() >> 4)) {
				plugin.getSpawnContainerEffectsTask().removeLocationInList(containerLocation);
				return;
			}
			final List<ParticleEffect> effectType;
			if (this.effectType != null && !this.effectType.isEmpty())
				effectType = this.effectType;
			else
				effectType = containerDataCache.getParticleEffectList(containerDataCache.getLocationData(containerLocation).getContinerData());

			effect(containerLocation, effectType);
		}
	}

	public void effect(final Location containerLocation, final List<ParticleEffect> effectType) {
		if (containerLocation.getWorld() == null) return;
		final double X = containerLocation.getBlockX() + Math.random();
		final double Y = containerLocation.getBlockY() + Math.random();
		final double Z = containerLocation.getBlockZ() + Math.random();
		if (effectType != null && !effectType.isEmpty()) {
			for (final ParticleEffect particleEffect : effectType) {

				if (particleEffect == null) continue;

				new CreateParticle(particleEffect, containerLocation.getWorld(), X, Y, Z).create();
			/*	final Particle particle = particleEffect.getParticle();
				if (particle == null) continue;
				if (plugin.getServerVersion().newerThan(ServerVersion.Version.v1_9)) {
					if (!particle.name().contains("BLOCK_MARKER")) {
						if (particle.name().equals("REDSTONE")) {
							if (plugin.getServerVersion().newerThan(ServerVersion.Version.v1_13)) {

								final Particle.DustOptions dustOptions = particleEffect.getDustOptions();
								containerLocation.getWorld().spawnParticle(particle, X, Y, Z, 0, 0.0, 0.0, 0.0, 1.0, dustOptions);
							} else
								containerLocation.getWorld().spawnParticle(particle, X, Y, Z, 0, 5.0, 0.0, 5.0, 3);
						} else
							containerLocation.getWorld().spawnParticle(particle, X, Y, Z, 0, 0.0, 0.0, 0.0, 1.0);
					} else if (particleEffect.getMaterial() != null)
						containerLocation.getWorld().spawnParticle(Particle.valueOf("BLOCK_MARKER"), X, Y, Z, 0, 0.0, 0.0, 0.0, 1.0, particleEffect.getMaterial().createBlockData());
					else
						Lootboxes.getInstance().getLogger().log(Level.WARNING, "This particle " + particle + " is not valid");
				} else {
					final Effect effect = particleEffect.getEffect();
					if (effect != null)
						containerLocation.getWorld().playEffect(new Location(containerLocation.getWorld(), X, Y, Z), effect, 6);
					else
						containerLocation.getWorld().playEffect(new Location(containerLocation.getWorld(), X, Y, Z), Effect.valueOf("COLOURED_DUST"), Integer.MAX_VALUE);
				}*/
			}
		}
	}

	@Override
	public void compute() {
		spawnEffects();
	}

	@Override
	public boolean reschedule() {
		return System.currentTimeMillis() <= rescheduleMaxRunTime();
		//return true;
	}

	@Override
	public double getMilliPerTick() {
		return 5;
	}

	@Override
	public boolean computeWithDelay(final int conter) {
		return true;
	}

	@Override
	public long rescheduleMaxRunTime() {
		return time;
	}
}
