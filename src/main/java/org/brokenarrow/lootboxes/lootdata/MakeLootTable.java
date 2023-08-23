package org.brokenarrow.lootboxes.lootdata;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.RandomUntility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.brokenarrow.lootboxes.lootdata.LootItems.YamlKey.GLOBAL_VALUES;

public class MakeLootTable {
	LootItems lootItems = LootItems.getInstance();
	ItemData itemData = ItemData.getInstance();
	private int minimumAmountOfItems = 0;
	private int maxAmountOfItems = 0;
	private final RandomUntility random = Lootboxes.getInstance().getRandomUntility();

	@Nullable
	public ItemStack[] makeLootTable(String table) {
		List<ItemStack> itemStacks = new ArrayList<>();
		List<ItemStack> backupItemstacks = new ArrayList<>();
		int amountIfItemsMax = 0;
		setGlobalValues(table);
		Map<String, LootData> lootDataMap = lootItems.getCachedTableContents(table);
		if (lootDataMap == null) return null;

		for (LootData lootData : lootDataMap.values()) {
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
		if (checkAir(itemStacks))
			return setBackupItems(backupItemstacks);
		else
			return itemStacks.toArray(new ItemStack[itemStacks.size() + 2]);
	}

	private ItemStack[] setBackupItems(List<ItemStack> backupItemstacks) {
		if (this.minimumAmountOfItems <= 0) return new ItemStack[0];

		List<ItemStack> itemstacks = new ArrayList<>();
		int size = backupItemstacks.size();
		int amount = Math.max(random.randomIntNumber(this.minimumAmountOfItems, this.maxAmountOfItems), 1);
		for (int i = 0; i < amount; i++) {
			itemstacks.add(backupItemstacks.get(random.randomIntNumber(this.minimumAmountOfItems, size - 1)));
		}
		return itemstacks.toArray(new ItemStack[itemstacks.size() + 1]);
	}

	/**
	 * This method check if added stacks is air or if list is null.
	 *
	 * @param itemStacks list ofr items to check
	 * @return true if item is air or null.
	 */
	private boolean checkAir(List<ItemStack> itemStacks) {
		if (itemStacks == null) return true;

		return itemStacks.stream().allMatch(item -> item == null || item.getType().isAir());
	}

	private boolean setGlobalValues(String table) {
		LootData lootData = lootItems.getLootData(table, GLOBAL_VALUES.getKey());
		if (lootData == null) {
			Lootboxes.getInstance().getLogger().log(Level.WARNING, "this table '" + table + "' don´t have 'Global_Values' set.");
			return false;
		}

		this.maxAmountOfItems = lootData.getMaximum();
		this.minimumAmountOfItems = lootData.getMinimum();
		return true;
	}

	private int randomNumber(LootData lootData) {
		return Math.max(random.randomIntNumber(lootData.getMinimum(), lootData.getMaximum()), 0);
	}

	private ItemStack createItem(LootData lootData, int amountOfItems) {
		ItemStack itemStack = null;
		Material matrial = null;
		if (lootData.getMaterial() == null) return null;

		if (lootData.isHaveMetadata()) {
			itemStack = this.itemData.getCacheItemData(lootData.getLootTableName(), lootData.getItemDataPath());

		} else
			matrial = lootData.getMaterial();
		return CreateItemUtily.of(itemStack != null ? itemStack : matrial).setAmountOfItems(amountOfItems).makeItemStack();
	}
}
