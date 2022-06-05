package org.brokenarrow.lootboxes.settings;

import org.broken.lib.rbg.TextTranslator;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ChatMessages {
	private static final Map<String, ChatMessages> chatMessagesMap = new HashMap<>();
	private String messages;

	public static ChatMessages CHANGE_DISPLAYNAME_AND_LORE_DISPLAYNAME = new ChatMessages("CHANGE_DISPLAYNAME_AND_LORE.DISPLAYNAME");
	public static ChatMessages CHANGE_DISPLAYNAME_AND_LORE_LORE = new ChatMessages("CHANGE_DISPLAYNAME_AND_LORE.LORE");
	public static ChatMessages CHANGE_DISPLAYNAME_AND_LORE_CONFIRM = new ChatMessages("CHANGE_DISPLAYNAME_AND_LORE.CONFIRM");
	public static ChatMessages CONTAINER_DATA_LINKED_LOOTTABLE_NEW_LOOTTABLE = new ChatMessages("CONTAINER_DATA_LINKED_LOOTTABLE.NEW_LOOTTABLE");
	public static ChatMessages CONTAINER_DATA_LINKED_LOOTTABLE_CANGE_NAME = new ChatMessages("CONTAINER_DATA_LINKED_LOOTTABLE.CANGE_NAME");
	public static ChatMessages CONTAINER_DATA_LINKED_LOOTTABLE_NEW_NAME_IS_SAME = new ChatMessages("CONTAINER_DATA_LINKED_LOOTTABLE.NEW_NAME_IS_SAME");
	public static ChatMessages CREATE_CONTAINER_DATA_NAME_CREATE_NEW = new ChatMessages("CREATE_CONTAINER_DATA_NAME.CREATE_NEW");
	public static ChatMessages CREATE_CONTAINER_DATA_NAME_ALREDY_EXIST = new ChatMessages("CREATE_CONTAINER_DATA_NAME.ALREDY_EXIST");
	public static ChatMessages CREATE_CONTAINER_DATA_NAME_CONFIRM = new ChatMessages("CREATE_CONTAINER_DATA_NAME.CONFIRM");
	public static ChatMessages SAVE_ENCHANTMENT_SET_LEVEL = new ChatMessages("SAVE_ENCHANTMENT.SET_LEVEL");
	public static ChatMessages SAVE_ENCHANTMENT_NOT_A_NUMBER = new ChatMessages("SAVE_ENCHANTMENT.NOT_A_NUMBER");
	public static ChatMessages SAVE_ENCHANTMENT_CONFIRM = new ChatMessages("SAVE_ENCHANTMENT.CONFIRM");
	public static ChatMessages SEACH_FOR_ENCHANTMENT_TYPE_NAME = new ChatMessages("SEACH_FOR_ENCHANTMENT.TYPE_NAME");
	public static ChatMessages SEACH_FOR_ITEM_TYPE_NAME = new ChatMessages("SEACH_FOR_ITEM.TYPE_NAME");
	public static ChatMessages SET_NAME_ON_KEY_DUPLICATE = new ChatMessages("SET_NAME_ON_KEY. DUPLICATE");
	public static ChatMessages SET_NAME_ON_KEY_TYPE_NAME = new ChatMessages("SET_NAME_ON_KEY.TYPE_NAME");
	public static ChatMessages SET_NAME_ON_KEY_CONFIRM = new ChatMessages("SET_NAME_ON_KEY.CONFIRM");
	public static ChatMessages SET_NAME_ON_KEY_CONFIRM_FINISH = new ChatMessages("SET_NAME_ON_KEY.CONFIRM_FINISH");
	public static ChatMessages SPECIFY_TIME_TYPE_TIME = new ChatMessages("SPECIFY_TIME.TYPE_TIME");
	public static ChatMessages SPECIFY_TIME_CONFIRM = new ChatMessages("SPECIFY_TIME.CONFIRM");
	public static ChatMessages ADD_CONTINERS_LEFT_CLICK_BLOCK = new ChatMessages("ADD_CONTINERS.LEFT_CLICK_BLOCK");
	public static ChatMessages ADD_CONTINERS_RIGHT_CLICK_BLOCK = new ChatMessages("ADD_CONTINERS.RIGHT_CLICK_BLOCK");
	public static ChatMessages TURNED_ON_ADD_CONTAINERS_WHEN_PLACE_CONTAINER = new ChatMessages("ADD_CONTINERS.TURNED_ON_ADD_CONTAINERS_WHEN_PLACE_CONTAINER");
	public static ChatMessages ADD_CONTINERS_TURN_OFF_ADD_CONTAINERS = new ChatMessages("ADD_CONTINERS.TURN_OFF_ADD_CONTAINERS");
	public static ChatMessages ADD_CONTINERS_THIS_CONTAINER_IS_USED_ALREDY = new ChatMessages("ADD_CONTINERS.THIS_CONTAINER_IS_USED_ALREDY");
	public static ChatMessages ADD_CONTINERS_TURN_ON_ADD_CONTAINERS = new ChatMessages("ADD_CONTINERS.TURNED_ON_ADD_CONTAINERS");
	public static ChatMessages ADD_CONTINERS_YOU_DROP_LINK_TOOL = new ChatMessages("ADD_CONTINERS.YOU_DROP_LINK_TOOL");
	public static ChatMessages ADD_CONTINERS_YOU_SWITCH_SLOT_LINK_TOOL = new ChatMessages("ADD_CONTINERS.YOU_SWITCH_SLOT_LINK_TOOL");
	public static ChatMessages ADD_CONTINERS_TURNED_ON_ADD_CONTAINERS_WITH_TOOL = new ChatMessages("ADD_CONTINERS.TURNED_ON_ADD_CONTAINERS_WITH_TOOL");
	public static ChatMessages YOU_DONT_HAVE_PERMISSION_TO_LINK = new ChatMessages("ADD_CONTINERS.YOU_DONT_HAVE_PERMISSION_TO_LINK");
	public static ChatMessages CREATE_TABLE_TYPE_NAME = new ChatMessages("CREATE_TABLE.TYPE_NAME");
	public static ChatMessages CREATE_TABLE_DUPLICATE = new ChatMessages("CREATE_TABLE.DUPLICATE");
	public static ChatMessages CREATE_TABLE_CONFIRM = new ChatMessages("CREATE_TABLE.CONFIRM");
	public static ChatMessages LOOKED_CONTAINER_NOT_RIGHT_ITEM = new ChatMessages("CONTAINER_OPEN.LOOKED_CONTAINER_NOT_RIGHT_ITEM");
	public static ChatMessages LOOKED_CONTAINER_NOT_RIGHT_AMOUNT = new ChatMessages("CONTAINER_OPEN.LOOKED_CONTAINER_NOT_RIGHT_AMOUNT");
	public static ChatMessages LOOKED_CONTAINER_SOUND = new ChatMessages("CONTAINER_OPEN.LOOKED_CONTAINER_SOUND");
	public static ChatMessages UNLOOKED_CONTAINER_SOUND = new ChatMessages("CONTAINER_OPEN.UNLOOKED_CONTAINER_SOUND");
	public static ChatMessages LOOKED_CONTAINER_TRY_OPEN = new ChatMessages("CONTAINER_OPEN.LOOKED_CONTAINER_TRY_OPEN");
	public static ChatMessages LOOKED_CONTAINER_NO_LOOTTABLE_LINKED = new ChatMessages("CONTAINER_OPEN.LOOKED_CONTAINER_NO_LOOTTABLE_LINKED");
	public static ChatMessages LOOKED_CONTAINER_NO_KEY_ADDED = new ChatMessages("CONTAINER_OPEN.LOOKED_CONTAINER_NO_KEY_ADDED");
	public static ChatMessages HAS_NOT_REFILL_CONTAINER = new ChatMessages("CONTAINER_OPEN.HAS_NOT_REFILL_CONTAINER");
	public static ChatMessages OPEN_CONTAINER = new ChatMessages("CONTAINER_OPEN.OPEN_CONTAINER");
	public static ChatMessages CHANGE_DISPLAYNAME_CONTINEDATA_CONFIRM = new ChatMessages("CHANGE_DISPLAYNAME_CONTINEDATA.CONFIRM");
	public static ChatMessages CHANGE_DISPLAYNAME_CONTINEDATA_DISPLAYNAME = new ChatMessages("CHANGE_DISPLAYNAME_CONTINEDATA.DISPLAYNAME");
	public static ChatMessages CONTINER_IS_NOT_OBSTACLE = new ChatMessages("TELEPORT.CONTINER_IS_NOT_OBSTACLE");
	public static ChatMessages CONTINER_IS_OBSTACLE_ON_ALL_SIDES = new ChatMessages("TELEPORT.CONTINER_IS_OBSTACLE_ON_ALL_SIDES");
	public static ChatMessages CONTINER_IS_OBSTACLE_ON_SOME_SIDES = new ChatMessages("TELEPORT.CONTINER_IS_OBSTACLE_ON_SOME_SIDES");
	public static ChatMessages PREFIX = new ChatMessages("PREFIX");
	public static ChatMessages RELOAD = new ChatMessages("RELOAD");
	public static ChatMessages DAY = new ChatMessages("DAY");
	public static ChatMessages HOUR = new ChatMessages("HOUR");
	public static ChatMessages MINUTE = new ChatMessages("MINUTE");
	public static ChatMessages SECOND = new ChatMessages("SECOND");
	public static ChatMessages DAYS = new ChatMessages("DAYS");
	public static ChatMessages HOURS = new ChatMessages("HOURS");
	public static ChatMessages MINUTES = new ChatMessages("MINUTES");
	public static ChatMessages SECONDS = new ChatMessages("SECONDS");

	public ChatMessages(String idKey) {
		chatMessagesMap.put(idKey, this);
	}

	protected String getMessages(Object... objects) {
		if (this.messages != null && !this.messages.isEmpty()) {
			String msg = this.messages;

			for (int i = 0; i < objects.length; i++) {
				Object object = convertObject(objects[i]);
				msg = msg.replace("{" + i + "}", object.toString());
			}
			return msg;

		}
		return null;
	}

	public String prefix() {
		if (PREFIX.getMessages() != null)
			return PREFIX.getMessages();
		return "";
	}

	public Object convertObject(Object object) {
		if (object instanceof List) {
			String converted = object.toString();
			int start = converted.indexOf('[');
			return converted.substring(start + 1, converted.length() - 1);
		} else if (object instanceof Location) {
			Location converted = (Location) object;
			return converted.getWorld() + "" + converted.getBlockX() + converted.getBlockY() + converted.getBlockZ();
		} else return object;


	}

	public void sendMessage(Player sender, Object... objects) {
		String message = getMessages(objects);
		if (this.messages != null && sender != null)
			if (sender.isConversing())
				sender.sendRawMessage(translateHexCodes(prefix() + message));
			else
				sender.sendMessage(translateHexCodes(prefix() + message));

	}

	public String languageMessagePrefix(Object... objects) {
		String message = getMessages(objects);
		if (this.messages != null)
			return translateHexCodes(prefix() + message);
		return "";
	}

	public String languageMessages(Object... objects) {
		String message = getMessages(objects);
		if (this.messages != null)
			return translateHexCodes(message);
		return "";
	}

	private void setMessages(String messages) {
		this.messages = messages;
	}

	public static void messagesReload(Plugin plugin) {

		File file = new File(plugin.getDataFolder() + "/language", "language_" + Lootboxes.getInstance().getSettings().getSettings().getLanguage() + ".yml");

		new AllYamlFilesInFolder("language", true).getAllFiles();

		if (!file.exists())
			Lootboxes.getInstance().saveResource("language/language_" + Lootboxes.getInstance().getSettings().getSettings().getLanguage() + ".yml", false);

		YamlConfiguration loadedFile = YamlConfiguration.loadConfiguration(file);

		for (String keys : chatMessagesMap.keySet()) {
			chatMessagesMap.get(keys).setMessages(loadedFile.getString(keys, ""));
		}
	}

	private static String translateHexCodes(String textTranslate) {
		if (textTranslate == null) {
			Lootboxes.getInstance().getLogger().log(Level.SEVERE, "one or several message not exist inside language file");
			return "";
		}

		return TextTranslator.toSpigotFormat(textTranslate);
	}
}
