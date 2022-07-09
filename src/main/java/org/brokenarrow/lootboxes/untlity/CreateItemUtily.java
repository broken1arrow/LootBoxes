package org.brokenarrow.lootboxes.untlity;

import org.brokenarrow.menu.library.utility.Item.CreateItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Make itemstacks simple with this class.
 */
public final class CreateItemUtily {

	private CreateItemUtily() {
	}

	/**
	 * Start to create simple item. Some have no displayname or
	 * lore. Finish creation with {@link CreateItemStack#makeItemStack()}
	 *
	 * @param item String name,Matrial or Itemstack.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static CreateItemStack of(final Object item) {
		return CreateItemStack.of(item, null, (List<String>) null);
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
		return CreateItemStack.of(item).setItemMetaData(itemMetaKey, itemMetaValue);
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
		return CreateItemStack.of(item, displayName, Arrays.asList(lore));
	}


	/**
	 * Start to create an item. Finish creation with {@link CreateItemStack#makeItemStack()}
	 *
	 * @param item        String name,Matrial or Itemstack.
	 * @param displayName name on the item.
	 * @param lore        on the item.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static CreateItemStack of(final Object item, final String displayName, final List<String> lore) {
		return CreateItemStack.of(item, displayName, lore);
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
		return CreateItemStack.of(itemArray, displayName, Arrays.asList(lore));
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
		return CreateItemStack.of(itemArray, displayName, lore);
	}

	/**
	 * Start to create an item. Finish creation with {@link CreateItemStack#makeItemStackArray()}
	 *
	 * @param item  String name,Matrial or Itemstack.
	 * @param color you want to use.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static <T> CreateItemStack of(final Object item, final String color) {
		return CreateItemStack.of(item, color);
	}

}
