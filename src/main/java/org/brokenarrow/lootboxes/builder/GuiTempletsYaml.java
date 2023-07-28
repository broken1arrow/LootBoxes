package org.brokenarrow.lootboxes.builder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.settings.ChatMessages;
import org.brokenarrow.lootboxes.settings.GuiTempletSettings;
import org.brokenarrow.lootboxes.settings.Guidata;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.SkullCreator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class GuiTempletsYaml {

	private final Player player;
	private final UUID uuid;
	private final Object[] placeholders;
	private final String menuName;
	private final String menuItemKey;
	private final GuiTempletSettings guiTemplets = GuiTempletSettings.getInstance();
	private static final Lootboxes plugin = Lootboxes.getInstance();

	private GuiTempletsYaml(final Builder builder) {
		this.player = builder.player;
		this.uuid = builder.uuid;
		this.placeholders = builder.placeholder;
		this.menuName = builder.menuName;
		this.menuItemKey = builder.menuKey;
	}

	public Player getPlayer() {
		return player;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Object[] getPlaceholders() {
		return placeholders;
	}

	public String getMenuName() {
		return menuName;
	}

	public String getMenuItemKey() {
		return menuItemKey;
	}

	public static GuiTempletsYaml of(final Player player, final String menuName, final String menuItemKey, final Object... placeholder) {
		return new Builder(player, menuName, menuItemKey).placeholders(placeholder).build();
	}

	public static GuiTempletsYaml build() {
		return new Builder().build();
	}

	public String getGuiTitle() {

		return getGuiTitle(this.menuName, this.placeholders);
	}

	public String getDisplayName() {

		return getDisplayName(this.player, this.menuName, this.menuItemKey, this.placeholders);
	}

	public List<String> getLore() {

		return getLore(this.player, this.menuName, this.menuItemKey, this.placeholders);
	}

	public List<Integer> getSlot() {

		return getSlot(this.menuName, this.menuItemKey);
	}

	public ItemStack getIcon() {

		return getIcon(this.menuName, this.menuItemKey, this.uuid);
	}


	public String getDisplayName(final Player player, final String menuName, final String menuItemKey, final Object... placeholder) {
		if (player != null)
			if (!plugin.isPlaceholderAPIMissing())
				return PlaceholderAPI.setPlaceholders(player, getDisplayName(menuName, menuItemKey, placeholder));
		return getDisplayName(menuName, menuItemKey, placeholder);
	}

	public String getDisplayName(final String menuName, final String menuItemKey, final Object... placeholders) {
		if (menuName != null && menuItemKey != null) {
			final Guidata displayname = guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey);
			if (checkNull(menuName, menuItemKey, YamlKeys.DisplayName, displayname))
				return translatePlaceholders(displayname.getDisplayname(), placeholders);
		}
		return "";
	}

	public List<String> getLore(final Player player, final String menuName, final String menuItemKey, final Object... placeholder) {
		if (player != null)
			if (!plugin.isPlaceholderAPIMissing())
				return PlaceholderAPI.setPlaceholders(player, getLore(menuName, menuItemKey, placeholder));
		return getLore(menuName, menuItemKey, placeholder);
	}

	public List<String> getLore(final String menuName, final String menuItemKey, final Object... placeholders) {
		final List<String> lores = new ArrayList<>();
		if (menuName != null && menuItemKey != null) {
			final Guidata guidata = guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey);
			if (checkNull(menuName, menuItemKey, YamlKeys.Lore, guidata))
				for (final String lore : guidata.getLore()) {
					final boolean containsPlaceholder = checkListForPlaceholders(lores, lore, placeholders);
					/*if (lore.contains("{" + containsList(placeholders) + "}") && containsList(placeholders) != -1)
						for (Object text : (List<?>) placeholders[containsList(placeholders)])
							lores.add(lore.replace("{" + containsList(placeholders) + "}", text));*/
					if (!containsPlaceholder)
						lores.add(translatePlaceholders(lore, placeholders));
				}
			return lores;
		}
		return Collections.singletonList("");
	}

	public static boolean checkListForPlaceholders(final List<String> lores, final String lore, final Object... placeholder) {
		final int number = containsList(placeholder);
		if (number < 0) return false;

		if (lore.contains("{" + number + "}")) {
			for (final Object text : (List<?>) placeholder[number])
				if (text instanceof String)
					lores.add(lore.replace(("{" + number + "}"), (String) text));
				else
					lores.add(lore.replace(("{" + number + "}"), text.toString()));
			return true;
		}
		return false;
	}

	public static int containsList(final Object... placeholder) {
		if (placeholder != null)
			for (int i = 0; i < placeholder.length; i++)
				if (placeholder[i] instanceof List)
					return i;
		return -1;
	}

	public static String translatePlaceholders(final String rawText, final Object... placeholders) {
		String text = rawText;
		if (placeholders != null)
			for (int i = 0; i < placeholders.length; i++) {
				if (placeholders[i] instanceof List)
					continue;
				text = text.replace("{" + i + "}", placeholders[i] != null ? placeholders[i].toString() : "");
			}
		return ifContainsBoolen(text);
	}

	public String getGuiTitle(final String menuName, final Object... placeholders) {
		if (menuName != null) {
			final Map<String, Guidata> gui = this.guiTemplets.getGuiValues(menuName);
			if (gui != null) {
				return ChatColor.translateAlternateColorCodes('&', translatePlaceholders(gui.get(menuName).getMenuTitle(), placeholders));
			}		}
		return "";
	}

	public List<Integer> getFillSpace() {
		return getFillSpace(this.menuName, null);
	}

	public List<Integer> getFillSpace(final String menuItemKey) {
		return getFillSpace(this.menuName, menuItemKey);
	}

	public List<Integer> getFillSpace(final String menuName, final String menuItemKey) {
		final List<Integer> slotList = new ArrayList<>();
		if (menuName != null) {
			final Guidata guiData;
			if (menuItemKey == null)
				guiData = guiTemplets.getGuiValues(menuName).get(menuName);
			else
				guiData = guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey);

			if (!checkNull(menuName, null, YamlKeys.FillSpace, guiData)) return new ArrayList<>();

			final String slots = guiData.getMenuFillSpace();
			try {
				for (final String slot : slots.split(",")) {
					if (slot.equals("")) {
						continue;
					}
					if (slot.contains("-")) {
						final int firstSlot = Integer.parseInt(slot.split("-")[0]);
						final int lastSlot = Integer.parseInt(slot.split("-")[1]);
						slotList.addAll(IntStream.rangeClosed(firstSlot, lastSlot).boxed().collect(Collectors.toList()));
					} else
						slotList.add(Integer.valueOf(slot));

				}
			} catch (final NumberFormatException e) {
				throw new NumberFormatException("can not parse this " + slots + " as numbers.");
			}
		}
		return slotList;
	}

	public int getMaxAmountOfItems(final String menuName) {
		if (menuName != null) {
			final Guidata guiData = guiTemplets.getGuiValues(menuName).get(menuName);
			if (checkNull(menuName, null, YamlKeys.MaxAmountOfItems, guiData))
				return guiData.getMenuMaxAmountOfItems();
		}
		return 26;
	}

	public int getGuiSize() {
		if (this.menuName != null) {
			final Guidata guiData = this.guiTemplets.getGuiValues(this.menuName).get(this.menuName);
			if (checkNull(this.menuName, null, YamlKeys.GuiSize, guiData))
				return guiData.getMenuSize();
		}
		return 9;
	}

	public int getGuiSize(final String menuName) {
		if (menuName != null) {
			final Guidata guiData = this.guiTemplets.getGuiValues(menuName).get(menuName);
			if (checkNull(menuName, null, YamlKeys.GuiSize, guiData))
				return guiData.getMenuSize();
		}
		return 9;
	}

	public List<Integer> getSlot(final String menuName, final String menuItemKey) {
		final List<Integer> slotList = new ArrayList<>();
		String slots = null;
		if (menuName != null && menuItemKey != null) {
			final Guidata guidata = this.guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey);
			if (checkNull(menuName, menuItemKey, YamlKeys.Slot, guidata))
				slots = guidata.getSlot();

			if (slots == null || slots.equals(""))
				return new ArrayList<>();

			try {
				for (final String slot : slots.split(",")) {
					if (slot.equals("")) {
						continue;
					}
					if (slot.contains("-")) {
						final int firstSlot = Integer.parseInt(slot.split("-")[0]);
						final int lastSlot = Integer.parseInt(slot.split("-")[1]);
						slotList.addAll(IntStream.rangeClosed(firstSlot, lastSlot).boxed().collect(Collectors.toList()));
					} else
						slotList.add(Integer.valueOf(slot));

				}
			} catch (final NumberFormatException e) {
				throw new NumberFormatException("can not parse this " + slots + " as numbers.");
			}
		}
		return slotList;
	}

	public ItemStack getIcon(final String menuName, final String menuItemKey, final UUID player) {

		if (menuName != null && menuItemKey != null) {
			final Guidata guiData = this.guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey);
			if (!checkNull(menuName, menuItemKey, YamlKeys.Icon, guiData)) return null;

			final String icon = guiData.getIcon();
			final boolean glow = this.guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey).isGlow();

			if (icon.startsWith("uuid="))
				return SkullCreator.itemFromUuid(UUID.fromString(icon.replaceFirst("uuid=", "")));
			else if (icon.startsWith("base64="))
				return SkullCreator.itemFromBase64(icon.replaceFirst("base64=", ""));
			else if (icon.startsWith("url="))
				return SkullCreator.itemFromUrl(icon.replaceFirst("url=", ""));
			else if (icon.equals("Player_Skull") && player != null) {
				return SkullCreator.itemFromUuid(player);
			} else {
				return CreateItemUtily.of(icon).setGlow(glow).makeItemStack();

			}
		}
		return CreateItemUtily.of(null).makeItemStack();
	}

	private static String ifContainsBoolen(final String text) {
		if (text.contains("true"))
			return text.replace("true", ChatMessages.TRUE.languageMessages());
		else if (text.contains("false"))
			return text.replace("false", ChatMessages.FALSE.languageMessages());
		else
			return text;
	}

	public static List<String> colorize(final List<String> original) {
		return original.stream().map(line -> ChatColor.translateAlternateColorCodes(
				'&', line)).collect(Collectors.toList());
	}


	private static boolean checkNull(final String menu, final String menuItemKey, final YamlKeys typeOfKeyMissing, final Guidata object) {
		if (object == null)
			plugin.getLogger().log(Level.INFO, "In this menu " + menu + (menuItemKey != null ? " and this icon " + menuItemKey : "") + ", do you missing this " + typeOfKeyMissing.name() + " key in your guitemplet.yml.");
		return object != null;
	}

	enum YamlKeys {
		Slot,
		DisplayName,
		Lore,
		Title,
		Icon,
		Glow,
		GuiSize,
		MaxAmountOfItems,
		FillSpace,

	}

	public static class Builder {
		private Player player;
		private UUID uuid;
		private Object[] placeholder;
		private String menuName;
		private String menuKey;

		private Builder() {
		}

		public Builder(final Player player, final String menuName) {
			this.player = player;
			this.menuName = menuName;
		}

		public Builder(final Player player, final String menuName, final String menuKey) {
			this.player = player;
			this.menuName = menuName;
			this.menuKey = menuKey;
		}

		public Builder menuName(final String menuName) {
			this.menuName = menuName;
			return this;
		}

		public Builder menuKey(final String menuKey) {
			this.menuKey = menuKey;
			return this;
		}

		public Builder uuid(final UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder placeholders(final Object... placeholder) {
			this.placeholder = placeholder;
			return this;
		}

		public GuiTempletsYaml build() {
			return new GuiTempletsYaml(this);
		}
	}

}
