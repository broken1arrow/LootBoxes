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

	private boolean spawnEffects() {
		if (checkChunkLoadUnload == null)
			this.checkChunkLoadUnload = plugin.getCheckChunkLoadUnload();
		//for (Location containerLocation : containerLocations) {
		/*final long stoptime = (long) (System.nanoTime() + (1000_000 * MAX_MS_PER_TICK));
		while (System.nanoTime() <= stoptime) {*/
		if (locationInlist >= containerLocations.length) {
			locationInlist = 0;
		}
		final Location containerLocation = containerLocations[locationInlist];
		locationInlist++;
		Boolean aBoolean = plugin.getSpawnedContainers().getHasRefill().get(containerLocation);
		if (aBoolean != null && aBoolean)
			if (containerLocation != null) {
				if (this.checkChunkLoadUnload.getChunkData(containerLocation.getBlockX() >> 4, containerLocation.getBlockZ() >> 4) == null || !containerLocation.getWorld().isChunkLoaded(containerLocation.getBlockX() >> 4, containerLocation.getBlockZ() >> 4)) {
					plugin.getSpawnContainerEffectsTask().removeLocationInList(containerLocation);
				}
				final List<ParticleEffect> effectType;
				if (this.effectType != null && !this.effectType.isEmpty())
					effectType = this.effectType;
				else
					effectType = containerDataCache.getParticleEffectList(containerDataCache.getLocationData(containerLocation).getContainerData());
				if (effectType == null)
					plugin.getSpawnContainerEffectsTask().removeLocationInList(containerLocation);
				effect(containerLocation, effectType);
				//}
				return true;
			}
		return false;
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
			}
		}
	}

	@Override
	public boolean compute() {
		return spawnEffects();
	}

	@Override
	public boolean reschedule() {
		return System.currentTimeMillis() <= rescheduleMaxRunTime();
		//return true;
	}

	@Override
	public double getMilliPerTick() {
		return 4.5;
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
