package org.brokenarrow.lootboxes.untlity;

import com.google.common.base.Enums;
import org.broken.lib.rbg.TextTranslator;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Make itemstacks simple with this class.
 */
public final class CreateItemUtily {

	private final ItemStack itemStack;
	private final Material matrial;
	private final String stringItem;
	private final Iterable<?> itemArray;
	private final String displayName;
	private final List<String> lore;
	private List<String> enchantments;
	private String itemMetaKey;
	private String itemMetaValue;
	private Map<String, String> itemMetaMap;
	private int amoutOfItems;
	private int enchantmentsLevel;
	private boolean glow;
	private boolean showEnchantments;
	private boolean ignoreLevelRestrictions;

	private CreateItemUtily(final Bulider bulider) {
		this.itemStack = bulider.itemStack;
		this.matrial = bulider.matrial;
		this.stringItem = bulider.stringItem;
		this.itemArray = bulider.itemArray;
		this.displayName = bulider.displayName;
		this.lore = bulider.lore;
	}

	/**
	 * Start to create simple item. Some have no displayname or
	 * lore. Finish creation with {@link #makeItemStack()}
	 *
	 * @param item String name,Matrial or Itemstack.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static CreateItemUtily of(final Object item) {
		return of(item, null, (List<String>) null);
	}

	/**
	 * Start to create simple item. Some have no displayname or
	 * lore, but have metadata. Finish creation with {@link #makeItemStack()}
	 *
	 * @param item          String name,Matrial or Itemstack.
	 * @param itemMetaKey   set metadata key
	 * @param itemMetaValue set metadata value
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static CreateItemUtily of(final Object item, final String itemMetaKey, String itemMetaValue) {
		return of(item).setItemMetaData(itemMetaKey, itemMetaValue);
	}

	/**
	 * Start to create an item. Finish creation with {@link #makeItemStack()}
	 * <p>
	 * This method uses varargs and add it to list, Like this "a","b","c".
	 * You can also skip adding any value to lore too.
	 *
	 * @param item        String name,Matrial or Itemstack.
	 * @param displayName name on the item.
	 * @param lore        as varargs.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static CreateItemUtily of(final Object item, final String displayName, final String... lore) {
		return of(item, displayName, Arrays.asList(lore));
	}


	/**
	 * Start to create an item. Finish creation with {@link #makeItemStack()}
	 *
	 * @param item        String name,Matrial or Itemstack.
	 * @param displayName name on the item.
	 * @param lore        on the item.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static CreateItemUtily of(final Object item, final String displayName, final List<String> lore) {

		if (item instanceof ItemStack)
			return new Bulider((ItemStack) item, displayName, lore).build();
		else if (item instanceof Material)
			return new Bulider((Material) item, displayName, lore).build();
		else if (item instanceof String)
			return new Bulider((String) item, displayName, lore).build();

		return new Bulider((String) null, displayName, lore).build();
	}

	/**
	 * Start to create an item. Finish creation with {@link #makeItemStackArray()}
	 * <p>
	 * This method uses varargs and add it to list, Like this "a","b","c".
	 * You can also skip adding any value to lore too.
	 *
	 * @param itemArray   string name.
	 * @param displayName item name.
	 * @param lore        as varargs.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static <T> CreateItemUtily of(final Iterable<T> itemArray, final String displayName, final String... lore) {
		return of(itemArray, displayName, Arrays.asList(lore));
	}

	/**
	 * Start to create an item. Finish creation with {@link #makeItemStackArray()}
	 *
	 * @param itemArray   string name.
	 * @param displayName item name.
	 * @param lore        on the item.
	 * @return CreateItemUtily class or class with air item (if item are null).
	 */
	public static <T> CreateItemUtily of(final Iterable<T> itemArray, final String displayName, final List<String> lore) {
		return new Bulider(itemArray, displayName, lore).build();
	}

	/**
	 * Amount of items you want to create.
	 *
	 * @param amoutOfItems item amount.
	 * @return this class.
	 */
	public CreateItemUtily setAmoutOfItems(final int amoutOfItems) {
		this.amoutOfItems = amoutOfItems;
		return this;
	}

	/**
	 * Set glow on item and will not show the enchantments.
	 * Use {@link #addEnchantments(List)} or {@link #addEnchantments(String...)}, for set custom
	 * enchants.
	 *
	 * @param glow set it true and the item will glow.
	 * @return this class.
	 */
	public CreateItemUtily isglow(final boolean glow) {
		this.glow = glow;
		return this;
	}

	/**
	 * Set enchantments level on the item.
	 * If you want to bypass level restrictions.
	 * {@link #isignoreLevelRestrictions(boolean)}
	 *
	 * @param enchantmentsLevel type level you want to set.
	 * @return this class.
	 */

	public CreateItemUtily setEnchantmentsLevel(final int enchantmentsLevel) {
		this.enchantmentsLevel = enchantmentsLevel;
		return this;
	}

	/**
	 * Add own enchantments. Set {@link #isShowEnchantments(boolean)} to true
	 * if you whant to hide all enchants (defult so will it not hide enchants).
	 * <p>
	 * This method uses varargs and add it to list, Like this "a","b","c".
	 *
	 * @param enchantments list of enchantments you want to add.
	 * @return this class.
	 */

	public CreateItemUtily addEnchantments(final String... enchantments) {
		return addEnchantments(Arrays.asList(enchantments));
	}

	/**
	 * Add own enchantments. Set {@link #isShowEnchantments(boolean)} to true
	 * if you whant to hide all enchants (defult so will it not hide enchants).
	 *
	 * @param enchantments list of enchantments you want to add.
	 * @return this class.
	 */

	public CreateItemUtily addEnchantments(final List<String> enchantments) {
		this.enchantments = enchantments;
		return this;
	}

	/**
	 * Ignore level restrictions. So you can set any level you want.
	 *
	 * @param ignoreLevelRestrictions true if you want to bypass level restrictions;
	 * @return this class.
	 */
	public CreateItemUtily isignoreLevelRestrictions(final boolean ignoreLevelRestrictions) {
		this.ignoreLevelRestrictions = ignoreLevelRestrictions;
		return this;
	}

	/**
	 * When use {@link #addEnchantments(List)} or {@link #addEnchantments(String...)} and
	 * want to hide enchants set it to true. When use {@link #isglow(boolean)} it will defult hide
	 * enchants, but if you set this to true it will show the enchant.
	 *
	 * @param showEnchantments true and will show enchants.
	 * @return this class.
	 */
	public CreateItemUtily isShowEnchantments(final boolean showEnchantments) {
		this.showEnchantments = showEnchantments;
		return this;
	}

	/**
	 * Set custom metadata on item.
	 *
	 * @param itemMetaKey   key for get value.
	 * @param itemMetaValue value you want to set.
	 * @return this class.
	 */
	public CreateItemUtily setItemMetaData(final String itemMetaKey, final String itemMetaValue) {
		this.itemMetaKey = itemMetaKey;
		this.itemMetaValue = itemMetaValue;
		return this;
	}

	/**
	 * Map list of metadata you want to set on a item.
	 * It use map key and value form the map.
	 *
	 * @param itemMetaMap map of values.
	 * @return this class.
	 */
	public CreateItemUtily setItemMetaDataList(final Map<String, String> itemMetaMap) {
		this.itemMetaMap = itemMetaMap;
		return this;
	}

	/**
	 * Create itemstack, call it after you added all data you want
	 * on the item.
	 *
	 * @return new itemstack with amount of 1 if you not set it.
	 */
	public ItemStack makeItemStack() {
		ItemStack itemstack = checkTypeOfItem();

		if (itemstack != null && itemstack.getType() != Material.AIR) {
		/*if (this.itemMetaMap != null) {
				for (final Map.Entry<String, String> entitys : this.itemMetaMap.entrySet()) {
					itemstack = CompMetadata.setMetadata(itemstack, entitys.getKey(), entitys.getValue());
				}
			} else if (this.itemMetaKey != null && this.itemMetaValue != null)
				itemstack = CompMetadata.setMetadata(itemstack, this.itemMetaKey, this.itemMetaValue);
*/
			final ItemMeta itemMeta = itemstack.getItemMeta();

			if (itemMeta != null) {
				if (this.displayName != null) {
					itemMeta.setDisplayName(translateColors(this.displayName));
				}
				if (this.lore != null && !this.lore.isEmpty()) {
					itemMeta.setLore(translateColors(this.lore));
				}
				addEnchantments(itemMeta);

			}
			itemstack.setItemMeta(itemMeta);
			itemstack.setAmount(this.amoutOfItems == 0 ? 1 : this.amoutOfItems);
		}
		return itemstack != null ? itemstack : new ItemStack(Material.AIR);
	}

	/**
	 * Create itemstack array, call it after you added all data you want
	 * on the item.
	 *
	 * @return new itemstack array with amount of 1 if you not set it.
	 */
	public ItemStack[] makeItemStackArray() {
		ItemStack itemstack = null;
		final List<ItemStack> list = new ArrayList<>();

		if (this.itemArray != null)
			for (final Object itemStringName : this.itemArray) {
				if (!(itemStringName instanceof String)) continue;

				itemstack = new ItemStack(Enums.getIfPresent(Material.class, (String) itemStringName).orNull() == null ? Material.AIR : Material.valueOf((String) itemStringName));

				if (!(itemstack.getType() == Material.AIR)) {
				/*	if (itemMetaMap != null) {
						for (final Map.Entry<String, String> entitys : this.itemMetaMap.entrySet()) {
							itemstack = CompMetadata.setMetadata(itemstack, entitys.getKey(), entitys.getValue());
						}
					} else if (this.itemMetaKey != null && this.itemMetaValue != null)
						itemstack = CompMetadata.setMetadata(itemstack, this.itemMetaKey, this.itemMetaValue);
*/
					final ItemMeta itemMeta = itemstack.getItemMeta();

					if (itemMeta != null) {
						if (this.displayName != null) {
							itemMeta.setDisplayName(translateColors(this.displayName));
						}
						if (this.lore != null && !this.lore.isEmpty()) {
							itemMeta.setLore(translateColors(this.lore));
						}
						addEnchantments(itemMeta);

					}
					itemstack.setItemMeta(itemMeta);
					itemstack.setAmount(this.amoutOfItems == 0 ? 1 : this.amoutOfItems);
					list.add(itemstack);
				}
			}
		return itemstack != null ? list.toArray(new ItemStack[0]) : new ItemStack[]{new ItemStack(Material.AIR)};
	}


	private ItemStack checkTypeOfItem() {
		if (this.itemStack != null)
			return itemStack;
		else if (this.matrial != null)
			return new ItemStack(matrial);
		else if (this.stringItem != null)
			return new ItemStack(Enums.getIfPresent(Material.class, this.stringItem).orNull() == null ? Material.AIR : Material.valueOf(this.stringItem));

		return null;
	}

	private void addEnchantments(final ItemMeta itemMeta) {
		if (this.enchantments != null && !this.enchantments.isEmpty()) {
			for (final String enchant : this.enchantments) {
				final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchant));
				if (enchantment != null)
					itemMeta.addEnchant(enchantment, this.enchantmentsLevel, this.ignoreLevelRestrictions);
				else
					Lootboxes.getInstance().getLogger().log(Level.INFO, "your enchantment: " + enchant + " ,are not valid.");
			}
			if (this.showEnchantments)
				hideEnchantments(itemMeta);
		} else if (this.glow) {
			itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
			if (!this.showEnchantments)
				hideEnchantments(itemMeta);
		}
	}

	private void hideEnchantments(final ItemMeta itemMeta) {
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
	}

	private List<String> translateColors(final List<String> rawLore) {
		final List<String> lores = new ArrayList<>();
		for (final String lore : rawLore)
			if (lore != null)
				lores.add(TextTranslator.toSpigotFormat(lore));
		return lores;
	}

	private String translateColors(final String rawSingelLine) {
		return TextTranslator.toSpigotFormat(rawSingelLine);
	}

	private static class Bulider {

		private ItemStack itemStack;
		private Material matrial;
		private String stringItem;
		private Iterable<?> itemArray;
		private final String displayName;
		private final List<String> lore;

		/**
		 * Create one itemStack, with name and lore. You can also add more
		 * like enchants and metadata.
		 *
		 * @param itemStack   item you want to create.
		 * @param displayName name onb item.
		 * @param lore        lore on item.
		 */
		private Bulider(final ItemStack itemStack, final String displayName, final List<String> lore) {
			this.itemStack = itemStack;
			this.displayName = displayName;
			this.lore = lore;
		}

		/**
		 * Create one itemStack, with name and lore. You can also add more
		 * like enchants and metadata.
		 *
		 * @param matrial     you want to create.
		 * @param displayName name onb item.
		 * @param lore        lore on item.
		 */
		private Bulider(final Material matrial, final String displayName, final List<String> lore) {
			this.matrial = matrial;
			this.displayName = displayName;
			this.lore = lore;
		}

		/**
		 * Create one itemStack, with name and lore. You can also add more
		 * like enchants and metadata.
		 *
		 * @param stringItem  you want to create.
		 * @param displayName name onb item.
		 * @param lore        lore on item.
		 */
		private Bulider(final String stringItem, final String displayName, final List<String> lore) {
			this.stringItem = stringItem;
			this.displayName = displayName;
			this.lore = lore;
		}

		/**
		 * Create array of itemStack´s, with name and lore. You can also add more
		 * like enchants and metadata.
		 *
		 * @param itemArray   you want to create.
		 * @param displayName name onb item.
		 * @param lore        lore on item.
		 */
		private <T> Bulider(final Iterable<T> itemArray, final String displayName, final List<String> lore) {
			this.itemArray = itemArray;
			this.displayName = displayName;
			this.lore = lore;
		}

		/**
		 * Build your item. And call {@link #makeItemStack()} or {@link #makeItemStackArray()}
		 * depending on if you want to create array of items or ony 1 stack.
		 *
		 * @return CreateItemUtily class with your data you have set.
		 */
		public CreateItemUtily build() {
			return new CreateItemUtily(this);
		}
	}
	

}
