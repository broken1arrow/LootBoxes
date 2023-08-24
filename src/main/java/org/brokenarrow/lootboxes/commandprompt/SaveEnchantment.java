package org.brokenarrow.lootboxes.commandprompt;

import org.broken.arrow.itemcreator.library.utility.Tuple;
import org.broken.arrow.prompt.library.SimpleConversation;
import org.broken.arrow.prompt.library.SimplePrompt;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.Enchantments;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
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
		super(Lootboxes.getInstance());
		this.lootTable = lootTable;
		this.itemToEdit = itemToEdit;
		this.enchantment = enchantment;
	}

	@Override
	public Prompt getFirstPrompt() {
		return new setLevel();
	}

	public class setLevel extends SimplePrompt {
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

			final String itemDataPath = data.getItemDataPath();
			ItemStack item = itemData.getCacheItemData(lootTable, itemDataPath);
			final Map<Enchantment, Tuple<Integer, Boolean>> enchantmentMap = new HashMap<>();
			final String itemKeyPath;
			enchantmentMap.put(enchantment, new Tuple<>(level, false));
			if (item == null) {
				ItemStack itemStack =  item = CreateItemUtily.of(data.getMaterial()).addEnchantments(enchantmentMap, false).setCopyOfItem(true).makeItemStack();
				itemKeyPath = itemData.setCacheItemData(lootTable, item.getType() + "", itemStack);
			} else {
				if (item.getItemMeta() != null && !item.getItemMeta().getEnchants().isEmpty())
					for (final Map.Entry<Enchantment, Integer> entry : item.getItemMeta().getEnchants().entrySet()) {
						if (entry.getKey().equals(enchantment))
							continue;
						enchantmentMap.put(entry.getKey(), new Tuple<>(entry.getValue(), false));
					}

				ItemStack itemStack = CreateItemUtily.of(item).addEnchantments(enchantmentMap, true).setCopyOfItem(true).makeItemStack();
				itemKeyPath = itemData.updateCacheItemData(lootTable, itemDataPath, itemStack);
			}
			final LootData.Builder builder = data.getBuilder();
			builder.setHaveMetadata(true).setLootTableName(lootTable).setItemDataPath(itemKeyPath);

			SAVE_ENCHANTMENT_CONFIRM.sendMessage(getPlayer(context), input);

			lootItems.setCachedLoot(lootTable, itemToEdit, builder.build());
			new Enchantments(lootTable, itemToEdit, "").menuOpen(getPlayer(context));
			return null;
		}
	}
}
