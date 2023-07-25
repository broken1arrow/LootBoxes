package org.brokenarrow.lootboxes.listener;

import org.broken.arrow.nbt.library.RegisterNbtAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.ModifyContinerData;
import org.brokenarrow.lootboxes.untlity.RunTimedTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static org.brokenarrow.lootboxes.settings.ChatMessages.ADD_CONTINERS_YOU_DROP_LINK_TOOL;
import static org.brokenarrow.lootboxes.settings.ChatMessages.ADD_CONTINERS_YOU_SWITCH_SLOT_LINK_TOOL;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER;

public class LinkTool implements Listener {

	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final Lootboxes lootboxes = Lootboxes.getInstance();
	private final RegisterNbtAPI nbt = lootboxes.getNbtAPI();

	@EventHandler
	public void swichSlot(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if (!player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;
		ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
		if (itemStack == null) return;

		String metadata = nbt.getCompMetadata().getMetadata(itemStack, ADD_AND_REMOVE_CONTAINERS.name());
		if (metadata == null)
			metadata = nbt.getCompMetadata().getMetadata(itemStack, ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name());

		if (metadata != null) {
			ADD_CONTINERS_YOU_SWITCH_SLOT_LINK_TOOL.sendMessage(player);
			event.getPlayer().getInventory().setItem(event.getPreviousSlot(), new ItemStack(Material.AIR));
			String playerMetadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();

			player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());
			RunTimedTask.runtaskLater(20, () -> new ModifyContinerData.AlterContainerDataMenu(playerMetadata).menuOpen(player), false);
		}
	}

	@EventHandler
	public void swichSlot(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (!player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;
		ItemStack itemStack = event.getItemDrop().getItemStack();
		String metadata = nbt.getCompMetadata().getMetadata(itemStack, ADD_AND_REMOVE_CONTAINERS.name());
		if (metadata == null)
			metadata = nbt.getCompMetadata().getMetadata(itemStack, ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name());

		if (metadata != null) {
			ADD_CONTINERS_YOU_DROP_LINK_TOOL.sendMessage(player);
			Inventory inventory = player.getInventory();
			int heldItemSlot = event.getPlayer().getInventory().getHeldItemSlot();
			inventory.setItem(heldItemSlot, new ItemStack(Material.AIR));

			event.getItemDrop().setPickupDelay(500);
			event.getItemDrop().remove();
			String playerMetadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();

			player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());
			RunTimedTask.runtaskLater(20, () -> new ModifyContinerData.AlterContainerDataMenu(playerMetadata).menuOpen(player), false);
		}
	}
}
