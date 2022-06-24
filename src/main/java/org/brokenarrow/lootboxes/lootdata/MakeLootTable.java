package org.brokenarrow.lootboxes.lootdata;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.RandomUntility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.brokenarrow.lootboxes.lootdata.LootItems.YamlKey.GLOBAL_VALUES;

public class MakeLootTable {
	LootItems lootItems = LootItems.getInstance();
	ItemData itemData = ItemData.getInstance();
	private int minimumAmountOfItems = 0;
	private int maxAmountOfItems = 0;
	private final RandomUntility random = Lootboxes.getInstance().getRandomUntility();

	public ItemStack[] makeLottable(String table) {
		List<ItemStack> itemStacks = new ArrayList<>();
		List<ItemStack> backupItemstacks = new ArrayList<>();
		int amountIfItemsMax = 0;
		setGlobalValues(table);

		for (LootData lootData : lootItems.getCachedTableContents(table).values()) {
			if (lootData.getMaterial() == Material.AIR) continue;


			int amountOfItems = randomNumber(lootData);

			if (this.minimumAmountOfItems > 0)
				backupItemstacks.add(createItem(lootData, amountOfItems <= 0 ? 1 : amountOfItems));

			if (amountIfItemsMax > this.maxAmountOfItems)
				break;
			if (!random.chance(lootData.getChance()))
				continue;
			amountIfItemsMax++;
			itemStacks.add(createItem(lootData, amountOfItems));
		}
		if (itemStacks.isEmpty())
			return setBackupItems(backupItemstacks);
		else
			return itemStacks.toArray(new ItemStack[itemStacks.size() + 2]);
	}

	private ItemStack[] setBackupItems(List<ItemStack> backupItemstacks) {
		List<ItemStack> itemstacks = new ArrayList<>();
		int size = backupItemstacks.size();
		int amount = Math.max(random.randomIntNumber(this.minimumAmountOfItems, this.maxAmountOfItems), 1);
		for (int i = 0; i < amount; i++) {
			itemstacks.add(backupItemstacks.get(random.randomIntNumber(this.minimumAmountOfItems, size - 1)));
		}
		return itemstacks.toArray(new ItemStack[itemstacks.size() + 1]);
	}

	private void setGlobalValues(String table) {
		LootData lootData = lootItems.getLootData(table, GLOBAL_VALUES.getKey());
		if (lootData == null) {
			Lootboxes.getInstance().getLogger().log(Level.WARNING, "this table " + table + " don´t have Global_Values set.");
			return;
		}

		this.maxAmountOfItems = lootData.getMaximum();
		this.minimumAmountOfItems = lootData.getMinimum();
	}

	private int randomNumber(LootData lootData) {
		return Math.max(random.randomIntNumber(lootData.getMinimum(), lootData.getMaximum()), 0);
	}

	private ItemStack createItem(LootData lootData, int amountOfItems) {
		ItemStack itemStack = null;
		Material matrial = null;
		if (lootData.getMaterial() == null) return null;

		if (lootData.isHaveMetadata()) {
			itemStack = this.itemData.getCacheItemData(lootData.getItemdataFileName(), lootData.getItemdataPath());

		} else
			matrial = lootData.getMaterial();
		return CreateItemUtily.of(itemStack != null ? itemStack : matrial).setAmoutOfItems(amountOfItems).makeItemStack();
	}
}
