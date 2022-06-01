package org.brokenarrow.lootboxes.hooks.landprotecting;

import me.angeschossen.lands.api.exceptions.FlagConflictException;
import me.angeschossen.lands.api.flags.Flag;
import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.flags.types.LandFlag;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class LandsProtection implements ProtectingProvider {
	private final LandsIntegration lands;
	
	public LandsProtection(Plugin plugin) {
		lands = new LandsIntegration(plugin);
		try {
			lands.registerFlag(new LandFlag(plugin, "Lootboxesspawn"));
		} catch (FlagConflictException exception) {
			exception.printStackTrace();
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean isAllowedToSpawnContainer(Location location) {
		Area landsArea = lands.getAreaByLoc(location);
		Flag flag = Flags.get("Lootboxesspawn");
		System.out.println("landsArea " + landsArea);
		if (landsArea == null) {
			if (!lands.isClaimed(location) && flag != null)
				return lands.getLandWorld(location.getWorld()).hasFlag(location, (LandFlag) flag);
			return true;
		}

		return flag == null || landsArea.hasFlag((LandFlag) flag);

	}
}
