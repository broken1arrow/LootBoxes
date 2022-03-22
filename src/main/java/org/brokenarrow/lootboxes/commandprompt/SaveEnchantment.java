package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.CustomizeItem;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SaveEnchantment extends SimpleConversation {
	private final String lootTable;
	private final String itemToEdit;
	private final Enchantment enchantment;

	public SaveEnchantment(String lootTable, String itemToEdit, Enchantment enchantment) {
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
		protected String getPrompt(ConversationContext context) {
			return "Type in the enchantments level for the item";
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			int level;
			try {
				level = Integer.parseInt(input);
			} catch (NumberFormatException ignore) {
				System.out.println("test v" + getPlayer(context));
				getPlayer(context).sendMessage("Is not valid number, you input: " + input + ".");
				getPlayer(context).sendMessage(getPlayer(context).getUniqueId(), "Is not valid number, you input: " + input + ".");
				return getFirstPrompt();

			}
			LootData data = lootItems.getLootData(lootTable, itemToEdit);
			String fileName = data.getItemdataFileName();
			String itemdataPath = data.getItemdataPath();
			ItemStack item = itemData.getCacheItemData(fileName, itemdataPath);

			String filePatch;
			if (item == null) {

				item = CreateItemUtily.of(data.getMaterial()).addEnchantments(enchantment).setEnchantmentsLevel(level).makeItemStack();
				filePatch = itemData.setCacheItemData(item.getType() + "", item);
				if (fileName == null)
					fileName = itemData.getFileName();
			} else {
				Set<Enchantment> enchants = new HashSet<>();
				if (item.getItemMeta() != null && !item.getItemMeta().getEnchants().isEmpty())
					enchants.addAll(item.getItemMeta().getEnchants().keySet());
				enchants.add(enchantment);
				item = CreateItemUtily.of(item).addEnchantments(Arrays.asList(enchants.toArray())).setEnchantmentsLevel(level).makeItemStack();
				filePatch = itemData.updateCacheItemData(itemdataPath, item);
			}
			LootData.Builder builder = lootItems.getLootData(lootTable, itemToEdit).getBuilder();
			builder.setHaveMetadata(true).setItemdataFileName(fileName).setItemdataPath(filePatch);

			lootItems.setCachedLoot(lootTable, itemToEdit, builder.build());
			new CustomizeItem.EnchantMents(lootTable, itemToEdit, "").menuOpen(getPlayer(context));
			return null;
		}
	}
}
