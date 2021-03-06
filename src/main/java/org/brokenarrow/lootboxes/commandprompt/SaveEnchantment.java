package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.CustomizeItem;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.utility.Item.Tuple;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class SaveEnchantment extends SimpleConversation {
	private final String lootTable;
	private final String itemToEdit;
	private final Enchantment enchantment;

	public SaveEnchantment(final String lootTable, final String itemToEdit, final Enchantment enchantment) {
		this.lootTable = lootTable;
		this.itemToEdit = itemToEdit;
		this.enchantment = enchantment;
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new setLevel();
	}

	public class setLevel extends SimplePromp {
		private final LootItems lootItems = LootItems.getInstance();
		private final ItemData itemData = ItemData.getInstance();

		@Override
		protected String getPrompt(final ConversationContext context) {
			return SAVE_ENCHANTMENT_SET_LEVEL.languageMessages();
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
			final int level;
			try {
				level = Integer.parseInt(input);
			} catch (final NumberFormatException ignore) {
				SAVE_ENCHANTMENT_NOT_A_NUMBER.sendMessage(getPlayer(context), input);

				return getFirstPrompt();

			}
			final LootData data = lootItems.getLootData(lootTable, itemToEdit);
			String fileName = data.getItemdataFileName();
			final String itemdataPath = data.getItemdataPath();
			ItemStack item = itemData.getCacheItemData(fileName, itemdataPath);
			final Map<Enchantment, Tuple<Integer, Boolean>> enchantmentMap = new HashMap<>();
			final String itemKeyPath;
			enchantmentMap.put(enchantment, new Tuple<>(level, false));
			if (item == null) {
				item = CreateItemUtily.of(data.getMaterial()).addEnchantments(enchantmentMap, false).makeItemStack();
				itemKeyPath = itemData.setCacheItemData(lootTable, item.getType() + "", item);
				if (fileName == null)
					fileName = itemData.getFileName();
			} else {
				if (item.getItemMeta() != null && !item.getItemMeta().getEnchants().isEmpty())
					for (final Map.Entry<Enchantment, Integer> entry : item.getItemMeta().getEnchants().entrySet())
						enchantmentMap.put(entry.getKey(), new Tuple<>(entry.getValue(), false));

				item = CreateItemUtily.of(item).addEnchantments(enchantmentMap, true).makeItemStack();
				itemKeyPath = itemData.updateCacheItemData(fileName, itemdataPath, item);
			}
			final LootData.Builder builder = lootItems.getLootData(lootTable, itemToEdit).getBuilder();
			builder.setHaveMetadata(true).setItemdataFileName(fileName).setItemdataPath(itemKeyPath);

			SAVE_ENCHANTMENT_CONFIRM.sendMessage(getPlayer(context), input);

			lootItems.setCachedLoot(lootTable, itemToEdit, builder.build());
			new CustomizeItem.EnchantMents(lootTable, itemToEdit, "").menuOpen(getPlayer(context));
			return null;
		}
	}
}
