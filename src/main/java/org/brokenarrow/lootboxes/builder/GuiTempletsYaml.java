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

public class GuiTempletsYaml {

	private final Player player;
	private final UUID uuid;
	private final Object[] placeholders;
	private final String menuName;
	private final String menuItemKey;
	private final GuiTempletSettings guiTemplets = GuiTempletSettings.getInstance();
	private static final Lootboxes plugin = Lootboxes.getInstance();

	private GuiTempletsYaml(Builder builder) {
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

	public static GuiTempletsYaml of(Player player, String menuName, String menuItemKey, Object... placeholder) {
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


	public String getDisplayName(Player player, String menuName, String menuItemKey, Object... placeholder) {
		if (player != null)
			if (!plugin.isPlaceholderAPIMissing())
				return PlaceholderAPI.setPlaceholders(player, getDisplayName(menuName, menuItemKey, placeholder));
		return getDisplayName(menuName, menuItemKey, placeholder);
	}

	public String getDisplayName(String menuName, String menuItemKey, Object... placeholders) {
		if (menuName != null && menuItemKey != null) {
			Guidata displayname = guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey);
			if (checkNull(menuName, menuItemKey, YamlKeys.DisplayName, displayname))
				return translatePlaceholders(displayname.getDisplayname(), placeholders);
		}
		return "";
	}

	public List<String> getLore(Player player, String menuName, String menuItemKey, Object... placeholder) {
		if (player != null)
			if (!plugin.isPlaceholderAPIMissing())
				return PlaceholderAPI.setPlaceholders(player, getLore(menuName, menuItemKey, placeholder));
		return getLore(menuName, menuItemKey, placeholder);
	}

	public List<String> getLore(String menuName, String menuItemKey, Object... placeholders) {
		List<String> lores = new ArrayList<>();
		if (menuName != null && menuItemKey != null) {
			Guidata guidata = guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey);
			if (checkNull(menuName, menuItemKey, YamlKeys.Lore, guidata))
				for (String lore : guidata.getLore()) {
					boolean containsPlaceholder = checkListForPlaceholders(lores, lore, placeholders);
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

	public static boolean checkListForPlaceholders(List<String> lores, String lore, Object... placeholder) {
		int number = containsList(placeholder);
		if (number < 0) return false;

		if (lore.contains("{" + number + "}")) {
			for (Object text : (List<?>) placeholder[number])
				if (text instanceof String)
					lores.add(lore.replace(("{" + number + "}"), (String) text));
				else
					lores.add(lore.replace(("{" + number + "}"), text.toString()));
			return true;
		}
		return false;
	}

	public static int containsList(Object... placeholder) {
		if (placeholder != null)
			for (int i = 0; i < placeholder.length; i++)
				if (placeholder[i] instanceof List)
					return i;
		return -1;
	}

	public static String translatePlaceholders(String rawText, Object... placeholders) {

		if (placeholders != null)
			for (int i = 0; i < placeholders.length; i++) {
				if (placeholders[i] instanceof List)
					continue;
				rawText = rawText.replace("{" + i + "}", placeholders[i] != null ? placeholders[i].toString() : "");
			}
		return ifContainsBoolen(rawText);
	}

	public String getGuiTitle(String menuName, Object... placeholders) {
		if (menuName != null) {
			Map<String, Guidata> gui = this.guiTemplets.getGuiValues(menuName);
			if (gui != null)
				return ChatColor.translateAlternateColorCodes('&', translatePlaceholders(gui.get(menuName).getMenuTitle(), placeholders));
		}
		return "";
	}

	public List<Integer> getFillSpace() {
		return getFillSpace(this.menuName, null);
	}

	public List<Integer> getFillSpace(String menuItemKey) {
		return getFillSpace(this.menuName, menuItemKey);
	}

	public List<Integer> getFillSpace(String menuName, String menuItemKey) {
		List<Integer> slotList = new ArrayList<>();
		if (menuName != null) {
			Guidata guiData;
			if (menuItemKey == null)
				guiData = guiTemplets.getGuiValues(menuName).get(menuName);
			else
				guiData = guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey);

			if (!checkNull(menuName, null, YamlKeys.FillSpace, guiData)) return new ArrayList<>();

			String slots = guiData.getMenuFillSpace();
			try {
				for (String slot : slots.split(",")) {
					if (slot.equals("")) {
						continue;
					}
					if (slot.contains("-")) {
						int firstSlot = Integer.parseInt(slot.split("-")[0]);
						int lastSlot = Integer.parseInt(slot.split("-")[1]);
						slotList.addAll(IntStream.rangeClosed(firstSlot, lastSlot).boxed().collect(Collectors.toList()));
					} else
						slotList.add(Integer.valueOf(slot));

				}
			} catch (NumberFormatException e) {
				throw new NumberFormatException("can not parse this " + slots + " as numbers.");
			}
		}
		return slotList;
	}

	public int getMaxAmountOfItems(String menuName) {
		if (menuName != null) {
			Guidata guiData = guiTemplets.getGuiValues(menuName).get(menuName);
			if (checkNull(menuName, null, YamlKeys.MaxAmountOfItems, guiData))
				return guiData.getMenuMaxAmountOfItems();
		}
		return 26;
	}

	public int getGuiSize() {
		if (this.menuName != null) {
			Guidata guiData = this.guiTemplets.getGuiValues(this.menuName).get(this.menuName);
			if (checkNull(this.menuName, null, YamlKeys.GuiSize, guiData))
				return guiData.getMenuSize();
		}
		return 9;
	}

	public int getGuiSize(String menuName) {
		if (menuName != null) {
			Guidata guiData = this.guiTemplets.getGuiValues(menuName).get(menuName);
			if (checkNull(menuName, null, YamlKeys.GuiSize, guiData))
				return guiData.getMenuSize();
		}
		return 9;
	}

	public List<Integer> getSlot(String menuName, String menuItemKey) {
		List<Integer> slotList = new ArrayList<>();
		String slots = null;
		if (menuName != null && menuItemKey != null) {
			Guidata guidata = this.guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey);
			if (checkNull(menuName, menuItemKey, YamlKeys.Slot, guidata))
				slots = guidata.getSlot();

			if (slots == null || slots.equals(""))
				return new ArrayList<>();

			try {
				for (String slot : slots.split(",")) {
					if (slot.equals("")) {
						continue;
					}
					if (slot.contains("-")) {
						int firstSlot = Integer.parseInt(slot.split("-")[0]);
						int lastSlot = Integer.parseInt(slot.split("-")[1]);
						slotList.addAll(IntStream.rangeClosed(firstSlot, lastSlot).boxed().collect(Collectors.toList()));
					} else
						slotList.add(Integer.valueOf(slot));

				}
			} catch (NumberFormatException e) {
				throw new NumberFormatException("can not parse this " + slots + " as numbers.");
			}
		}
		return slotList;
	}

	public ItemStack getIcon(String menuName, String menuItemKey, UUID player) {

		if (menuName != null && menuItemKey != null) {
			Guidata guiData = this.guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey);
			if (!checkNull(menuName, menuItemKey, YamlKeys.Icon, guiData)) return null;

			String icon = guiData.getIcon();
			boolean glow = this.guiTemplets.getGuiValues(menuName).get(menuName + "_" + menuItemKey).isGlow();

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

	private static String ifContainsBoolen(String text) {
		if (text.contains("true"))
			return text.replace("true", ChatMessages.TRUE.languageMessages());
		else if (text.contains("false"))
			return text.replace("false", ChatMessages.FALSE.languageMessages());
		else
			return text;
	}

	public static List<String> colorize(List<String> original) {
		return original.stream().map(line -> ChatColor.translateAlternateColorCodes(
				'&', line)).collect(Collectors.toList());
	}


	private static boolean checkNull(String menu, String menuItemKey, YamlKeys typeOfKeyMissing, Guidata object) {
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

		public Builder(Player player, String menuName) {
			this.player = player;
			this.menuName = menuName;
		}

		public Builder(Player player, String menuName, String menuKey) {
			this.player = player;
			this.menuName = menuName;
			this.menuKey = menuKey;
		}

		public Builder menuName(String menuName) {
			this.menuName = menuName;
			return this;
		}

		public Builder menuKey(String menuKey) {
			this.menuKey = menuKey;
			return this;
		}

		public Builder uuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder placeholders(Object... placeholder) {
			this.placeholder = placeholder;
			return this;
		}

		public GuiTempletsYaml build() {
			return new GuiTempletsYaml(this);
		}
	}

}
