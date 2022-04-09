package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.menus.EditKeysToOpen;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SetKeyName extends SimpleConversation {

	private final ItemStack[] itemStacks;
	private final String containerData;
	private final ContainerData containerDataInstance = ContainerData.getInstance();
	private static final Map<Player, StoreData> chachedPlayer = new HashMap<>();

	public SetKeyName(ItemStack[] itemStacks, String containerData) {
		this.itemStacks = itemStacks;
		this.containerData = containerData;
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new Commandprompt();
	}

	@Override
	protected void onConversationEnd(ConversationAbandonedEvent event) {
		if (event.getCanceller() instanceof Player) {
			final Player player = (Player) event.getCanceller();
			chachedPlayer.remove(player);
		}
		System.out.println("removed " + chachedPlayer);
	}

	public class Commandprompt extends SimplePromp {

		@Override
		protected String getPrompt(ConversationContext context) {
			Player player = getPlayer(context);

			for (int i = 0; i < itemStacks.length; i++) {
				ItemStack itemStack = itemStacks[i];
				if (itemStack == null) continue;
				StoreData data = chachedPlayer.get(player);
				Set<Integer> numbersUsed = new HashSet<>();
				if (data != null)
					numbersUsed.addAll(data.getNumbersUsed());
				numbersUsed.add(i);
				chachedPlayer.put(player, new StoreData(i, numbersUsed));
				return "Type name on the key";
			}
			return "Type name on the key";
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {

			if (containerDataInstance.containsKeyName(containerData, input))
				return getFirstPrompt();
			Player player = getPlayer(context);
			int placeInList = chachedPlayer.get(player).getNumber();
			ItemStack item = itemStacks[placeInList];

			if (item != null) {
				if (item.hasItemMeta()) {
					ItemMeta meta = item.getItemMeta();
					if (meta != null) {
						ContainerDataBuilder.KeysData data = new ContainerDataBuilder.KeysData(
								input,
								meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name().toLowerCase(),
								containerDataInstance.getCacheContainerData(containerData).getLootTableLinked(),
								item.getAmount(),
								item.getType(),
								meta.hasLore() ? meta.getLore() : new ArrayList<>());
						containerDataInstance.setKeyData(containerData, input, data);
					}
				} else {
					ContainerDataBuilder.KeysData data = new ContainerDataBuilder.KeysData(
							input,
							item.getType().name().toLowerCase(),
							containerDataInstance.getCacheContainerData(containerData).getLootTableLinked(),
							item.getAmount(),
							item.getType(),
							new ArrayList<>());
					containerDataInstance.setKeyData(containerData, input, data);
				}
			}
			if (!checkAllItems(player)) {
				return getFirstPrompt();
			}

			new EditKeysToOpen.SaveNewKeys(containerData).menuOpen(getPlayer(context));
			return null;
		}

		private boolean checkAllItems(Player player) {
			StoreData data = chachedPlayer.get(player);
			boolean hasCheckAllItems = false;
			if (data != null) {
				List<Integer> addnumbers = new ArrayList<>();
				for (int i = 0; i < itemStacks.length; i++) {
					ItemStack itemStack = itemStacks[i];
					if (itemStack == null) continue;
					addnumbers.add(i);
				}
				for (Integer number : addnumbers) {
					hasCheckAllItems = data.getNumbersUsed().contains(number);
				}
			}
			return hasCheckAllItems;
		}
	}

	public class StoreData {
		private final int number;
		private final Set<Integer> numbersUsed;

		public StoreData(int number, Set<Integer> numbersUsed) {
			this.number = number;
			this.numbersUsed = numbersUsed;
		}

		public int getNumber() {
			return number;
		}

		public Set<Integer> getNumbersUsed() {
			return numbersUsed;
		}
	}
}

