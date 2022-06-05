package org.brokenarrow.lootboxes.commands;

import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.command.SubCommandsUtility;
import org.brokenarrow.lootboxes.untlity.command.TabUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.KeyMeta.MOB_DROP_CONTAINER_DATA_NAME;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.MOB_DROP_KEY_NAME;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholders;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholdersLore;

public class GetKeyCommand extends SubCommandsUtility {

	private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();

	public GetKeyCommand() {
		super("key");

		setPermission("lootboxes.command.key");
		setPermissionMessage("you don´t have lootboxes.admin.* or the children permissions");
	}

	@Override
	protected void onCommand() {
		String[] args = getArgs();

		if (args.length >= 4) {
			Player player = Bukkit.getPlayer(args[0]);
			if (player == null)
				player = Bukkit.getOfflinePlayer(args[0]).getPlayer();

			Map<String, Object> map = new HashMap<>();
			KeysData keysData = containerDataCacheInstance.getCacheKey(args[1], args[2]);
			map.put(MOB_DROP_KEY_NAME.name(), keysData.getKeyName());
			map.put(MOB_DROP_CONTAINER_DATA_NAME.name(), args[1]);

			String placeholderDisplayName = translatePlaceholders(keysData.getDisplayName(), keysData.getKeyName(),
					keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
			List<String> placeholdersLore = translatePlaceholdersLore(keysData.getLore(), keysData.getKeyName(),
					keysData.getLootTableLinked().length() > 0 ? keysData.getLootTableLinked() : "No table linked", keysData.getAmountNeeded(), keysData.getItemType());
			if (player != null)
				player.getInventory().addItem(CreateItemUtily.of(keysData.getItemType(), placeholderDisplayName, placeholdersLore).setItemMetaDataList(map).setAmoutOfItems(Integer.parseInt(args[3])).makeItemStack());

		}
	}

	@Override
	protected List<String> tabComplete() {
		String players = joinRange(0, getArgs());
		String containerData = joinRange(1, getArgs());
		String key = joinRange(2, getArgs());
		Set<String> keySet = containerDataCacheInstance.getCacheContainerData().keySet();


		if (getArgs().length == 1) {
			return TabUtil.complete(players, Bukkit.getOnlinePlayers().stream().map(Player::getDisplayName).collect(Collectors.toList()));
		}
		if (getArgs().length == 2)
			return TabUtil.complete(containerData, keySet);
		if (getArgs().length == 3) {
			List<String> list = new ArrayList<>();
			for (String value : keySet) {
				Map<String, KeysData> cacheKeys = containerDataCacheInstance.getCacheKeys(value);
				if (cacheKeys != null)
					list.addAll(cacheKeys.keySet());
			}
			return TabUtil.complete(key, list);
		}
		if (getArgs().length == 4) {
			return completeLastWord("<amount>");
		}
		return new ArrayList<>();
	}
}
