package org.brokenarrow.lootboxes.lootdata;

import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.brokenarrow.lootboxes.untlity.RandomUntility.chance;
import static org.brokenarrow.lootboxes.untlity.RandomUntility.randomIntNumber;

public class MakeLootTable {
	LootItems lootItems = LootItems.getInstance();
	ItemData itemData = ItemData.getInstance();
	private int minimumAmountOfItems = 0;
	private int maxAmountOfItems = 0;

	public ItemStack[] makeLottable(String table) {
		List<ItemStack> itemStacks = new ArrayList<>();
		List<ItemStack> backupItemstacks = new ArrayList<>();
		int amountIfItemsMax = 0;
		int backupcounter = -1;
		setGlobalValues(table);

		for (LootData lootData : lootItems.getCachedTableContents(table).values()) {

			if (backupcounter == -1) {
				backupcounter = Math.max(randomIntNumber(this.minimumAmountOfItems, this.maxAmountOfItems), 0);
			}
			int amountOfItems = randomNumber(lootData);

			if (backupcounter > 0 && amountIfItemsMax == backupcounter)
				backupItemstacks.add(createItem(lootData, amountOfItems));

			if (amountIfItemsMax > this.maxAmountOfItems)
				break;
			amountIfItemsMax++;
			if (!chance(lootData.getChance()))
				return new ItemStack[0];

			itemStacks.add(createItem(lootData, amountOfItems));
		}
		if (itemStacks.isEmpty())
			return backupItemstacks.toArray(new ItemStack[0]);
		else
			return itemStacks.toArray(new ItemStack[0]);
	}

	private void setGlobalValues(String table) {
		this.maxAmountOfItems = lootItems.getLootData(table, "Global_Values").getMaximum();
		this.minimumAmountOfItems = lootItems.getLootData(table, "Global_Values").getMinimum();
	}

	private int randomNumber(LootData lootData) {
		return Math.max(randomIntNumber(lootData.getMinimum(), lootData.getMaximum()), 0);
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
