package org.brokenarrow.lootboxes.commands;

import org.broken.arrow.command.library.command.CommandHolder;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.command.TabUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.KeyMeta.MOB_DROP_CONTAINER_DATA_NAME;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.MOB_DROP_KEY_NAME;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholders;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholdersLore;

public class GetKeyCommand  extends CommandHolder {

	private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();

	public GetKeyCommand() {
		super("key");


	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String @NotNull [] cmdArgs) {

		if (cmdArgs.length >= 3) {
			Player player = Bukkit.getPlayer(cmdArgs[0]);
			if (player == null)
				player = Bukkit.getOfflinePlayer(cmdArgs[0]).getPlayer();

			Map<String, Object> map = new HashMap<>();
			KeysData keysData = containerDataCacheInstance.getCacheKey(cmdArgs[1], cmdArgs[2]);
			map.put(MOB_DROP_KEY_NAME.name(), keysData.getKeyName());
			map.put(MOB_DROP_CONTAINER_DATA_NAME.name(), cmdArgs[1]);
			int amount = 1;
			if (cmdArgs.length >= 4)
				amount = Integer.parseInt(cmdArgs[3]);

			String placeholderDisplayName = translatePlaceholders(keysData.getDisplayName(), keysData.getKeyName(),
					keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
			List<String> placeholdersLore = translatePlaceholdersLore(keysData.getLore(), keysData.getKeyName(),
					keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
			if (player != null)
				player.getInventory().addItem(CreateItemUtily.of(keysData.getItemType(), placeholderDisplayName, placeholdersLore).setItemMetaDataList(map).setAmoutOfItems(amount).makeItemStack());

		}
		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String @NotNull [] cmdArgs) {
		String players = joinRange(0);
		String containerData = joinRange(1);
		String key = joinRange(2);
		Set<String> keySet = containerDataCacheInstance.getCacheContainerData().keySet();
		ContainerDataBuilder containerDataBuilder = null;
		if (cmdArgs.length >= 2) {
			containerDataBuilder = containerDataCacheInstance.getCacheContainerData(containerData.trim());
		}
		if (cmdArgs.length == 1) {
			return TabUtil.complete(players, Bukkit.getOnlinePlayers().stream().map(Player::getDisplayName).collect(Collectors.toList()));
		}
		if (cmdArgs.length == 2)
			return TabUtil.complete(containerData, keySet);
		if (cmdArgs.length >= 3) {
			if (containerDataBuilder != null) {
				return TabUtil.complete(key, containerDataBuilder.getKeysData().keySet());
			}
		}
		if (cmdArgs.length == 4) {
			return completeLastWord("<amount>");
		}
		return new ArrayList<>();
	}

}
