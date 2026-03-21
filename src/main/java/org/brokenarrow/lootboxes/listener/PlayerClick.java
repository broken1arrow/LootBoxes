package org.brokenarrow.lootboxes.listener;

import org.broken.arrow.library.nbt.RegisterNbtAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.*;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.LootContainerRandomCache;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.RunTimedTask;
import org.brokenarrow.lootboxes.untlity.ServerVersion.Version;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;
import static org.brokenarrow.lootboxes.untlity.BlockChecks.checkBlockIsContainer;
import static org.brokenarrow.lootboxes.untlity.BlockChecks.getInventory;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER;
import static org.brokenarrow.lootboxes.untlity.ModifyBlock.getFacing;

public class PlayerClick implements Listener {
    private final Settings settings = Lootboxes.getInstance().getSettings();
    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
    private final Lootboxes lootboxes = Lootboxes.getInstance();
    private final RegisterNbtAPI nbt = lootboxes.getNbtAPI();
    private final SettingsData setting = Lootboxes.getInstance().getSettings().getSettingsData();

    @EventHandler
    public void playerPlaceBlock(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();
        if (blockPlaced.getType() != Material.AIR) {
            Player player = event.getPlayer();
            if (!player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;
            if (!player.hasPermission("lootboxes.link.containers")) {
                YOU_DONT_HAVE_PERMISSION_TO_LINK.sendMessage(player, "lootboxes.link.containers");
                event.setCancelled(true);
                return;
            }
            ItemStack itemHand = event.getItemInHand();
            CustomContainer customContainer = Lootboxes.getInstance().getCustomLootContainersCache().getSimilarContainer(blockPlaced.getType(), itemHand);
            if (customContainer != null) {
                Location location = blockPlaced.getLocation();

                String metadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();
                LootContainerData data = containerDataCache.getCacheContainerData(metadata);
                LocationData locationData = containerDataCache.getContainerLocationCache().getLocationData(location);

                if (locationData != null) {
                    ADD_CONTINERS_THIS_CONTAINER_IS_USED_ALREDY.sendMessage(player, locationData.getContainerKey());
                    event.setCancelled(true);
                    return;
                }
                if (addData(itemHand, blockPlaced, data, location, metadata)) {
                    ADD_CONTINERS_LEFT_CLICK_BLOCK.sendMessage(player, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                    if (player.getGameMode() == GameMode.SURVIVAL) {
                        player.getInventory().setItemInMainHand(CreateItemUtily.of(false, itemHand,
                                        setting.getPlaceContainerDisplayName(), setting.getPlaceContainerLore())
                                .setItemMetaData(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name(), metadata).makeItemStack());
                    }
                } else
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerBreakBlock(BlockBreakEvent event) {
        Block blockPlaced = event.getBlock();
        if (blockPlaced.getType() != Material.AIR) {
            Player player = event.getPlayer();
            clearRandomSpawnedContainers(event.getBlock());

            if (!player.hasPermission("lootboxes.link.containers")) return;
            if (checkBlockIsContainer(blockPlaced)) {
                Location location = blockPlaced.getLocation();
                if (!player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;

                String metadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();
                LootContainerData data = containerDataCache.getCacheContainerData(metadata);
                if (data == null) return;
                removeData(data, location, metadata);
                ADD_CONTINERS_RIGHT_CLICK_BLOCK.sendMessage(player, location);
            }
        }
    }

    @EventHandler
    public void playerClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (!player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;
        if (!player.hasPermission("lootboxes.link.containers")) {
            YOU_DONT_HAVE_PERMISSION_TO_LINK.sendMessage(player, "lootboxes.link.containers");
            event.setCancelled(true);
            return;
        }

        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {
            String metadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();
            player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());

            RunTimedTask.runtaskLater(5, () -> new AlterContainerDataMenu(metadata).menuOpen(player), false);
            ADD_CONTINERS_TURN_OFF_ADD_CONTAINERS.sendMessage(player);
        }
        if (block == null) return;

        Location location = block.getLocation();
        if (checkBlockIsContainer(block)) {
            String metadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();
            LootContainerData data = containerDataCache.getCacheContainerData(metadata);
            LocationData locationData = containerDataCache.getContainerLocationCache().getLocationData(location);
            String itemMetadata = null;
            if (event.getItem() != null)
                itemMetadata = nbt.getCompMetadata().getMetadata(event.getItem(), ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name());

            if (itemMetadata != null) {
                return;
            }

            if (locationData != null && action == Action.LEFT_CLICK_BLOCK) {
                ADD_CONTINERS_THIS_CONTAINER_IS_USED_ALREDY.sendMessage(player, locationData.getContainerKey());
                event.setCancelled(true);
                return;
            }

            final ContainerData containerDataMap = data.getLinkedContainerData(location);
            if (containerDataMap == null && action == Action.LEFT_CLICK_BLOCK) {
                if (addData(null, block, data, location, metadata)) {
                    ADD_CONTINERS_LEFT_CLICK_BLOCK.sendMessage(player, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                    event.setCancelled(true);
                }
            } else if (action == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                removeData(data, location, metadata);
                ADD_CONTINERS_RIGHT_CLICK_BLOCK.sendMessage(player, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            }
        }
    }

    public void removeData(LootContainerData data, Location location, String metadata) {
        Map<BlockKey, ContainerData> containerDataMap = data.getLinkedContainerData();
        containerDataMap.remove(BlockKey.of(location));

        containerDataCache.write(metadata, (Consumer<LootContainerData>) builder -> builder.setContainerData(containerDataMap));
    }

    public boolean addData(@Nullable final ItemStack itemHand, final Block block, final LootContainerData data, final Location location, final String containerKey) {
        return containerDataCache.write(containerKey, builder -> {
            Map<BlockKey, ContainerData> containerDataMap = data.getLinkedContainerData();
            if (lootboxes.getServerVersion().olderThan(Version.v1_13)) {
                BlockState blockState = block.getState();
                if (blockState.getData() instanceof DirectionalContainer) {
                    MaterialData materialData = blockState.getData();
                    containerDataMap.put(BlockKey.of(location), new ContainerData(getFacing(materialData.getData()), itemHand != null ? itemHand : new ItemStack(block.getType())));
                    builder.setContainerData(containerDataMap);
                    if (data.getIcon() == null || data.getIcon() == Material.AIR)
                        builder.setIcon(block.getType());
                    return true;
                }
                return false;
            }
            if (block.getBlockData() instanceof Directional) {
                Directional container = (Directional) block.getBlockData();
                containerDataMap.put(BlockKey.of(location), new ContainerData(container.getFacing(), itemHand != null ? itemHand : new ItemStack(block.getType())));
                builder.setContainerData(containerDataMap);
                if (data.getIcon() == null || data.getIcon() == Material.AIR)
                    builder.setIcon(block.getType());
                return true;
            }
            return false;
        });
    }

    @EventHandler
    public void playerLeftSever(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name()))
            player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());
        if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name()))
            player.removeMetadata(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name(), Lootboxes.getInstance());

    }

    @EventHandler
    public void playerJoinSever(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name()))
            player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());
        if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name()))
            player.removeMetadata(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name(), Lootboxes.getInstance());
    }

    private void clearRandomSpawnedContainers(Block block) {
        final Location location = block.getLocation();
        LootContainerRandomCache.RandomLootData randomLootContainer = Lootboxes.getInstance().getLootContainerRandomCache().getCachedLootContainerLocation(location);
        if (randomLootContainer != null) {
            final Inventory inventory = getInventory(location);
            if (inventory != null) {
                if (inventory.getContents().length > 0) {
                    for (ItemStack itemStack : inventory) {
                        if (itemStack == null) continue;
                        location.getWorld().dropItemNaturally(location, itemStack);
                    }
                }
                inventory.clear();
            } else {
                final ItemStack[] containerContents = randomLootContainer.getContent();
                if (containerContents != null) {
                    for (ItemStack itemStack : containerContents) {
                        if (itemStack == null) continue;
                        location.getWorld().dropItemNaturally(location, itemStack);
                    }
                }
            }
            if (settings.getSettingsData().isRemoveContainerWhenPlayerClose()) {
                location.getBlock().setType(Material.AIR);
            }
            Lootboxes.getInstance().getLootContainerRandomCache().removeCachedLootContainerLocation(location);
        }
    }
}
