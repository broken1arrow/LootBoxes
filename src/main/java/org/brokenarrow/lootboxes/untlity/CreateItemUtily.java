package org.brokenarrow.lootboxes.untlity;

import org.broken.arrow.library.itemcreator.CreateItemStack;
import org.broken.arrow.library.itemcreator.ItemCreator;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Make itemstacks simple with this class.
 */
public final class CreateItemUtily {
	private static final ItemCreator itemCreator;

	static {
		itemCreator = Lootboxes.getInstance().getItemCreator();
	}

	public static void load() {}

	/**
	 * Start to create simple item. Some have no displayname or
	 * lore. Finish creation with {@link CreateItemStack#makeItemStack()}
	 *
	 * @param item String name,Matrial or Itemstack.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static CreateItemStack of(final Object item) {
		CreateItemStack createItemStack = itemCreator.of(item);
		if (createItemStack.isGlow())
			createItemStack.setItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
		return createItemStack;
	}

	/**
	 * Start to create simple item. Some have no displayname or
	 * lore, but have metadata. Finish creation with {@link CreateItemStack#makeItemStack()}
	 *
	 * @param item          String name,Matrial or Itemstack.
	 * @param itemMetaKey   set metadata key
	 * @param itemMetaValue set metadata value
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static CreateItemStack of(final Object item, final String itemMetaKey, String itemMetaValue) {
		CreateItemStack createItemStack = itemCreator.of(item).setItemMetaData(itemMetaKey, itemMetaValue);
		if (createItemStack.isGlow())
			createItemStack.setItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
		return createItemStack;
	}

	/**
	 * Start to create an item. Finish creation with {@link CreateItemStack#makeItemStack()}
	 * <p>
	 * This method uses varargs and add it to list, Like this "a","b","c".
	 * You can also skip adding any value to lore too.
	 *
	 * @param item        String name,Matrial or Itemstack.
	 * @param displayName name on the item.
	 * @param lore        as varargs.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static CreateItemStack of(final Object item, final String displayName, final String... lore) {
		CreateItemStack createItemStack = itemCreator.of(item, displayName, Arrays.asList(lore));
		if (createItemStack.isGlow())
			createItemStack.setItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS));

		return createItemStack;
	}


	/**
	 * Start to create an item. Finish creation with {@link CreateItemStack#makeItemStack()}
	 *
	 * @param item        String name,Matrial or Itemstack.
	 * @param displayName name on the item.
	 * @param lore        on the item.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static CreateItemStack of(boolean glow,final Object item, final String displayName, final List<String> lore) {
		CreateItemStack createItemStack = itemCreator.of(item, displayName, lore);
		if (glow) {
			createItemStack.setItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
			createItemStack.setGlow(true);
		}
		if (item != null && item.toString().equals("FIREWORK_STAR"))
			createItemStack.setRgb("0,0,0,0");
		return createItemStack;
	}

	/**
	 * Start to create an item. Finish creation with {@link CreateItemStack#makeItemStackArray()}
	 * <p>
	 * This method uses varargs and add it to list, Like this "a","b","c".
	 * You can also skip adding any value to lore too.
	 *
	 * @param itemArray   string name.
	 * @param displayName item name.
	 * @param lore        as varargs.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static <T> CreateItemStack of(final Iterable<T> itemArray, final String displayName, final String... lore) {
		CreateItemStack createItemStack = itemCreator.of(itemArray, displayName, Arrays.asList(lore));
		if (createItemStack.isGlow())
			createItemStack.setItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
		return createItemStack;
	}

	/**
	 * Start to create an item. Finish creation with {@link CreateItemStack#makeItemStackArray()}
	 *
	 * @param itemArray   string name.
	 * @param displayName item name.
	 * @param lore        on the item.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static <T> CreateItemStack of(final Iterable<T> itemArray, final String displayName, final List<String> lore) {
		CreateItemStack createItemStack = itemCreator.of(itemArray, displayName, lore);
		if (createItemStack.isGlow())
			createItemStack.setItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
		return createItemStack;
	}

	/**
	 * Start to create an item. Finish creation with {@link CreateItemStack#makeItemStackArray()}
	 *
	 * @param item  String name,Matrial or Itemstack.
	 * @param color you want to use.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static <T> CreateItemStack of(final Object item, final String color) {
		CreateItemStack createItemStack = itemCreator.of(item, color);
		if (createItemStack.isGlow())
			createItemStack.setItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
		return createItemStack;
	}
}
