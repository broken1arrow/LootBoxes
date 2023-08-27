package org.brokenarrow.lootboxes.hooks.landprotecting;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class LandProtectingLoader {
	private final ProtectingProvider[] protectingProvider;

	public LandProtectingLoader(Plugin plugin) {
		Set<ProtectingProvider> providers = new HashSet<>();
		if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
			providers.add(new WorldguardProtection());
		}
		if (Bukkit.getServer().getPluginManager().getPlugin("Lands") != null) {
			providers.add(new LandsProtection(plugin));
		}

		this.protectingProvider = providers.toArray(new ProtectingProvider[0]);
	}

	public ProtectingProvider[] getProtectingProvider() {
		return protectingProvider;
	}

	public boolean checkIfAllProvidersAllowSpawnContainer(Location location) {
		if (location == null)
			return true;
		if (protectingProvider == null || protectingProvider.length == 0)
			return true;
		for (ProtectingProvider provider : protectingProvider)
			if (!provider.isAllowedToSpawnContainer(location))
				return false;
		return true;
	}

}
