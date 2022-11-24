package org.brokenarrow.lootboxes.untlity.blockVisualization;

import lombok.NonNull;
import org.broken.lib.rbg.TextTranslator;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.untlity.errors.Valid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;
import static org.brokenarrow.lootboxes.untlity.ServerVersion.Version.*;

public final class BlockVisualizerUtility {
	private static final Map<Location, VisualizeData> visualizedBlocks = new ConcurrentHashMap<>();
	private static final Lootboxes LOOTBOXES = Lootboxes.getInstance();

	public static void visualize(@NotNull Block block, Material mask, String blockName) {
		visualize(null, block, mask, blockName);
	}

	public static void visualize(Player viwer, @NotNull Block block, Material mask, String blockName) {
		if (block == null) {
			throw new NullPointerException("block is marked non-null but is null");
		} else {
			if (isVisualized(block))
				stopVisualizing(block);
			Valid.checkBoolean(!isVisualized(block), "Block at " + block.getLocation() + " already visualized");
			Location location = block.getLocation();
			FallingBlock falling = spawnFallingBlock(location, mask, blockName);
			VisualizeData visualizeData = new VisualizeData(viwer, falling, blockName, mask);
			Iterator<Player> players = block.getWorld().getPlayers().iterator();
			if (viwer == null) {
				while (players.hasNext()) {
					Player player = players.next();
					visualizeData.addPlayersAllowed(player);
					sendBlockChange(2, player, location, LOOTBOXES.getServerVersion().olderThan(v1_9) ? mask : Material.BARRIER);
				}
			} else {
				while (players.hasNext()) {
					Player player = players.next();
					if (player.hasPermission("cch.admin.block_visualize") || player.getUniqueId().equals(viwer.getUniqueId())) {
						visualizeData.addPlayersAllowed(player);
						sendBlockChange(2, player, location, LOOTBOXES.getServerVersion().olderThan(v1_9) ? mask : Material.BARRIER);
					}
				}
			}
			visualizedBlocks.put(location, visualizeData);
		}
	}

	private static FallingBlock spawnFallingBlock(Location location, Material mask, String blockName) {
		if (LOOTBOXES.getServerVersion().olderThan(v1_9)) {
			return null;
		} else {
			FallingBlock falling = spawnFallingBlock(location.clone().add(0.5D, 0.0, 0.5D), mask);
			falling.setDropItem(false);
			falling.setVelocity(new Vector(0, 0, 0));
			setCustomName(falling, blockName);
			apply(falling, true);
			//apply(falling, false);
			return falling;
		}
	}

	public static void stopVisualizing(@NonNull Block block) {
		if (block == null) {
			throw new NullPointerException("block is marked non-null but is null");
		} else {
			Valid.checkBoolean(isVisualized(block), "Block at " + block.getLocation() + " not visualized");
			VisualizeData visualizeData = visualizedBlocks.remove(block.getLocation());
			FallingBlock fallingBlock = visualizeData.getFallingBlock();

			if (fallingBlock != null) {
				fallingBlock.remove();
			}
			Set<Player> playersAllowed = visualizeData.getPlayersAllowed();
			Iterator<Player> players;
			if (playersAllowed != null && !playersAllowed.isEmpty())
				players = playersAllowed.iterator();
			else
				players = block.getWorld().getPlayers().iterator();
			while (players.hasNext()) {
				Player player = players.next();
				sendBlockChange(1, player, block.getLocation().getBlock());
			}
		}

	}

	public static void stopAll() {
		for (Location location : visualizedBlocks.keySet()) {
			Block block = location.getBlock();
			if (isVisualized(block)) {
				stopVisualizing(block);
			}
		}

	}

	public static boolean isVisualized(@NonNull Block block) {
		if (block == null) {
			throw new NullPointerException("block is marked non-null but is null");
		} else {
			return visualizedBlocks.containsKey(block.getLocation());
		}
	}

	private static void setCustomName(Entity en, String name) {
		try {
			en.setCustomNameVisible(true);
			en.setCustomName(translateColor(name));
		} catch (NoSuchMethodError ignored) {
		}

	}

	private static String translateColor(String name) {
		return TextTranslator.toSpigotFormat(name);
	}

	public static void sendBlockChange(int delayTicks, Player player, Location location, Material material) {
		if (delayTicks > 0) {
			runtaskLater(delayTicks, () -> {
				sendBlockChange0(player, location, material);
			}, false);
		} else {
			sendBlockChange0(player, location, material);
		}

	}

	public static void sendBlockChange(int delayTicks, Player player, Block block) {
		if (delayTicks > 0) {
			runtaskLater(delayTicks, () -> {
				sendBlockChange0(player, block);
			}, false);
		} else {
			sendBlockChange0(player, block);
		}

	}

	private static void sendBlockChange0(Player player, Block block) {
		try {
			player.sendBlockChange(block.getLocation(), block.getBlockData());
		} catch (NoSuchMethodError var3) {
			player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
		}

	}

	private static void sendBlockChange0(Player player, Location location, Material material) {
		try {
			player.sendBlockChange(location, material.createBlockData());
		} catch (NoSuchMethodError var4) {
			player.sendBlockChange(location, material, (byte) material.getId());
		}

	}

	public static void apply(@NonNull Object instance, boolean key) {
	/*	if (instance instanceof FallingBlock) {
			FallingBlock entity = ((FallingBlock) instance);
			if (ServerVersion.olderThan(ServerVersion.v1_13)) {
				menulibrary.dependencies.nbt.nbtapi.NBTEntity nbtEntity = new menulibrary.dependencies.nbt.nbtapi.NBTEntity((Entity) instance);
				nbtEntity.setInteger("NoGravity", key ? 0 : 1);
				entity.setGlowing(key);
			}else {
				System.out.println("instance  fffffff");
				entity.setGravity(!key);
				entity.setGlowing(key);
			}
		}*/
		if (instance instanceof Entity) {
			Entity entity = ((Entity) instance);
			if (LOOTBOXES.getServerVersion().olderThan(v1_13)) {
				menulibrary.dependencies.nbt.nbtapi.NBTEntity nbtEntity = new menulibrary.dependencies.nbt.nbtapi.NBTEntity((Entity) instance);
				nbtEntity.setInteger("NoGravity", !key ? 0 : 1);
				entity.setGlowing(key);
			} else {
				entity.setGravity(!key);
				entity.setGlowing(key);
			}
		}
	}

	private static FallingBlock spawnFallingBlock(Location loc, Material material) {
		return spawnFallingBlock(loc, material, (byte) 0);
	}

	private static FallingBlock spawnFallingBlock(Location loc, Material material, byte data) {
		if (loc.getWorld() == null) return null;

		if (LOOTBOXES.getServerVersion().atLeast(v1_13)) {
			if (LOOTBOXES.getServerVersion().atLeast(v1_16))
				return loc.getWorld().spawnFallingBlock(loc, material.createBlockData());
			else
				return loc.getWorld().spawnFallingBlock(loc, material, data);
		} else {
			try {
				return (FallingBlock) loc.getWorld().getClass().getMethod("spawnFallingBlock", Location.class, Integer.TYPE, Byte.TYPE).invoke(loc.getWorld(), loc, material.getId(), data);
			} catch (ReflectiveOperationException var4) {
				var4.printStackTrace();
				return null;
			}
		}
	}

	private BlockVisualizerUtility() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static final class VisualTask extends BukkitRunnable {
		private static final VisualTask instance = new VisualTask();

		public static VisualTask getInstance() {
			return instance;
		}

		BukkitTask task;

		public void start() {
			if (task == null)
				task = runTaskTimer(LOOTBOXES, 0L, 20 * 6L);
		}

		public boolean checkTaskRunning() {
			return task != null && (Bukkit.getScheduler().isQueued(task.getTaskId()) || Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId()));
		}
		public void stop() {
			if (checkTaskRunning())
				task.cancel();
		}

		@Override
		public void run() {
			if (visualizedBlocks.isEmpty()) {
				stop();
				return;
			}
			for (Map.Entry<Location, VisualizeData> visualizeBlocks : visualizedBlocks.entrySet()) {
				Location location = visualizeBlocks.getKey();
				if (LOOTBOXES.getServerVersion().newerThan(v1_9) && (location.getBlock().getType() == Material.BARRIER || location.getBlock().getType() == Material.AIR)) {
					stopVisualizing(location.getBlock());
					continue;
				}

				VisualizeData visualizeData = visualizeBlocks.getValue();
				if (visualizeData.getViwer() == null)
					for (Player player : visualizeData.getPlayersAllowed())
						visualize(player, location.getBlock(), visualizeData.getMask(), visualizeData.getText());
				else
					visualize(visualizeData.getViwer(), location.getBlock(), visualizeData.getMask(), visualizeData.getText());
			}
		}
	}

	public static final class VisualizeData {
		private final Player viwer;
		private final Set<Player> playersAllowed;
		private final FallingBlock fallingBlock;
		private final String text;
		private final Material mask;

		public VisualizeData(Player viwer, FallingBlock fallingBlock, String text, Material mask) {
			this(viwer, new HashSet<>(), fallingBlock, text, mask);
		}

		public VisualizeData(Player viwer, Set<Player> playersAllowed, FallingBlock fallingBlock, String text, Material mask) {
			this.viwer = viwer;
			this.playersAllowed = playersAllowed;
			this.fallingBlock = fallingBlock;
			this.text = text;
			this.mask = mask;
		}

		public Player getViwer() {
			return viwer;
		}

		public void addPlayersAllowed(Player viwer) {
			playersAllowed.add(viwer);
		}

		public Set<Player> getPlayersAllowed() {
			return playersAllowed;
		}

		public FallingBlock getFallingBlock() {
			return fallingBlock;
		}

		public String getText() {
			return text;
		}

		public Material getMask() {
			return mask;
		}
	}
}