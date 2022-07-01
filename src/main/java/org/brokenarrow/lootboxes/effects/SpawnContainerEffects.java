package org.brokenarrow.lootboxes.effects;

import com.google.common.base.Enums;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.api.HeavyLoad;
import org.brokenarrow.lootboxes.listener.CheckChunkLoadUnload;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.*;

import java.util.List;
import java.util.logging.Level;

import static org.brokenarrow.lootboxes.untlity.ConvetParticlesUntlity.checkParticleOld;

public class SpawnContainerEffects implements HeavyLoad {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private CheckChunkLoadUnload checkChunkLoadUnload;
	private static final Lootboxes plugin = Lootboxes.getInstance();

	private final Location[] containerLocations;
	private final List<Particle> effectType;
	private final long time;
	private static int locationInlist;
	private final double MAX_MS_PER_TICK = 4.5;

	public SpawnContainerEffects(final List<Particle> effectType, final Integer runTime, final Location... containerLocations) {
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
			final List<Particle> effectType;
			if (this.effectType != null && !this.effectType.isEmpty())
				effectType = this.effectType;
			else
				effectType = containerDataCache.getCacheContainerData(containerDataCache.getLocationData(containerLocation).getContinerData()).getParticleEffects();

			effect(containerLocation, effectType);
		}
	}

	public void effect(final Location containerLocation, final List<Particle> effectType) {
		if (containerLocation.getWorld() == null) return;
		final double X = containerLocation.getBlockX() + Math.random();
		final double Y = containerLocation.getBlockY() + Math.random();
		final double Z = containerLocation.getBlockZ() + Math.random();
		if (effectType != null && !effectType.isEmpty()) {
			for (final Particle particle : effectType) {
				if (ServerVersion.newerThan(ServerVersion.v1_9)) {
					if (particle == null) continue;

					if (!particle.name().contains("BLOCK_MARKER")) {
						if (particle.name().equals("REDSTONE")) {
							if (ServerVersion.newerThan(ServerVersion.v1_13)) {

								final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 127, 210), 0.7F);
								containerLocation.getWorld().spawnParticle(particle, X, Y, Z, 0, 0.0, 0.0, 0.0, 1.0, dustOptions);
							} else
								containerLocation.getWorld().spawnParticle(particle, X, Y, Z, 0, 5.0, 0.0, 5.0, 3);
						} else
							containerLocation.getWorld().spawnParticle(particle, X, Y, Z, 0, 0.0, 0.0, 0.0, 1.0);
					} else if (Enums.getIfPresent(Material.class, particle.name()).orNull() != null)
						containerLocation.getWorld().spawnParticle(Particle.valueOf("BLOCK_MARKER"), X, Y, Z, 0, 0.0, 0.0, 0.0, 1.0, Material.valueOf(particle.name()).createBlockData());
					else
						Lootboxes.getInstance().getLogger().log(Level.WARNING, "This particle " + particle + " is not valid");
				} else {
					final Effect particleEffect = checkParticleOld(particle.name());
					if (particleEffect != null)
						containerLocation.getWorld().playEffect(new Location(containerLocation.getWorld(), X, Y, Z), particleEffect, 6);
					else
						containerLocation.getWorld().playEffect(new Location(containerLocation.getWorld(), X, Y, Z), Effect.valueOf("COLOURED_DUST"), Integer.MAX_VALUE);
				}
			}
		} else {
			if (ServerVersion.newerThan(ServerVersion.v1_13)) {
				final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 127, 210), 0.7F);
				containerLocation.getWorld().spawnParticle(Particle.REDSTONE, X, Y, Z, 0, 0.0, 0.0, 0.0, 1.0, dustOptions);
			} else {
				containerLocation.getWorld().spawnParticle(Particle.REDSTONE, X, Y, Z, 0, 0.0, 0.0, 0.0, 1.0);
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
