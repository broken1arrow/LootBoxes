package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.menus.EditKeysToOpen;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SetKeyName extends SimpleConversation {

	private final ItemStack[] itemStacks;
	private final String containerData;
	private final ContainerData containerDataInstance = ContainerData.getInstance();

	public SetKeyName(ItemStack[] itemStacks, String containerData) {
		this.itemStacks = itemStacks;
		this.containerData = containerData;
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new Commandprompt();
	}

	public class Commandprompt extends SimplePromp {

		@Override
		protected String getPrompt(ConversationContext context) {
			return "Type name on the key";
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {

			if (containerDataInstance.containsKeyName(containerData, input))
				return getFirstPrompt();

			for (ItemStack item : itemStacks) {
				if (item == null) continue;
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
			new EditKeysToOpen.SaveNewKeys(containerData).menuOpen(getPlayer(context));
			return null;
		}
	}
}
