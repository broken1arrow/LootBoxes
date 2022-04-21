package org.brokenarrow.lootboxes.settings;

import org.broken.lib.rbg.TextTranslator;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
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
	public static ChatMessages CREATE_TABLE_TYPE_NAME = new ChatMessages("CREATE_TABLE.TYPE_NAME");
	public static ChatMessages CREATE_TABLE_DUPLICATE = new ChatMessages("CREATE_TABLE.DUPLICATE");
	public static ChatMessages CREATE_TABLE_CONFIRM = new ChatMessages("CREATE_TABLE.CONFIRM");
	public static ChatMessages PREFIX = new ChatMessages("PREFIX");

	public ChatMessages(String idKey) {
		chatMessagesMap.put(idKey, this);
	}

	protected String getMessages(Object... objects) {
		if (this.messages != null && !this.messages.isEmpty()) {
			String msg = this.messages;

			for (int i = 0; i < objects.length; i++)
				msg = msg.replace("{" + i + "}", objects[i].toString());

			return msg;

		}
		return null;
	}

	public String prefix() {
		if (PREFIX.getMessages() != null)
			return PREFIX.getMessages();
		return "";
	}

	public void sendMessage(Player sender, Object... objects) {
		String message = getMessages(objects);
		if (this.messages != null && sender != null)
			if (sender.isConversing())
				sender.sendRawMessage(translateHexCodes(prefix() + message));
			else
				sender.sendMessage(translateHexCodes(prefix() + message));

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

		new AllYamlFilesInFolder("language", true).reload();

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
