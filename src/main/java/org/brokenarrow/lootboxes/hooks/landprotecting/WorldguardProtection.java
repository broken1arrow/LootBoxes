package org.brokenarrow.lootboxes.hooks.landprotecting;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;

public class WorldguardProtection implements ProtectingProvider {
	private final WorldGuard worldGuard;
	private final BooleanFlag allowPlaceChest;

	public WorldguardProtection() {

		worldGuard = WorldGuard.getInstance();
		FlagRegistry flags = worldGuard.getFlagRegistry();
		BooleanFlag allowPlaceChest = new BooleanFlag("Lootboxes-random-spawn-chest");
		this.allowPlaceChest = allowPlaceChest;
		try {

			flags.register(allowPlaceChest);
		} catch (FlagConflictException | IllegalStateException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public boolean isAllowedToSpawnContainer(Location location) {
		if (location.getWorld() == null) return false;
		RegionContainer container = worldGuard.getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));
		if (regions != null) {
			BlockVector3 position = BlockVector3.at(location.getX(),
					location.getY(), location.getZ());
			ApplicableRegionSet set = regions.getApplicableRegions(position);
			if (set.size() != 0) {

				// retrieve the highest priority
				ProtectedRegion pr = set.getRegions().iterator().next();
				for (ProtectedRegion pRegion : set.getRegions()) {
					if (pRegion.getPriority() > pr.getPriority()) {
						pr = pRegion;
					}
				}
				Boolean flag = pr.getFlag(this.allowPlaceChest);
				return flag != null ? flag : false;
			} else {
				for (ProtectedRegion reg : regions.getRegions().values()) {
					if (reg.contains(position)) {
						continue;
					}

					Boolean flag = reg.getFlag(this.allowPlaceChest);
					if (flag != null)
						return flag;
				}
			}
			return false;
		}
		return false;
	}
}
