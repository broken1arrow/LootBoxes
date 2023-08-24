package org.brokenarrow.lootboxes.commandprompt;

import org.broken.arrow.prompt.library.SimpleConversation;
import org.broken.arrow.prompt.library.SimplePrompt;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeysToSave;
import org.brokenarrow.lootboxes.menus.MenuKeys;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.menus.keys.EditKey;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.brokenarrow.lootboxes.menus.MenuKeys.ALTER_CONTAINER_DATA_MENU;
import static org.brokenarrow.lootboxes.menus.MenuKeys.EDITKEY;
import static org.brokenarrow.lootboxes.settings.ChatMessages.*;
import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class ChangeDisplayNameLore extends SimpleConversation {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final MenuKeys menuAccess;
	private final String container;
	private final String keyName;
	private final boolean setLore;

	public ChangeDisplayNameLore(MenuKeys menuAccess, String containerData, String keyName, boolean setLore) {
		super(Lootboxes.getInstance());
		this.menuAccess = menuAccess;
		this.container = containerData;
		this.keyName = keyName;
		this.setLore = setLore;
	}

	@Override
	public Prompt getFirstPrompt() {
		return new Commandprompt();
	}

	public class Commandprompt extends SimplePrompt {

		@Override
		protected String getPrompt(ConversationContext context) {
			if (setLore) {
				return CHANGE_DISPLAYNAME_AND_LORE_LORE.languageMessagePrefix();
			} else {
				if (menuAccess == ALTER_CONTAINER_DATA_MENU)
					return CHANGE_DISPLAYNAME_CONTINEDATA_DISPLAYNAME.languageMessages();
				return CHANGE_DISPLAYNAME_AND_LORE_DISPLAYNAME.languageMessagePrefix();
			}
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			if (menuAccess == EDITKEY) {
				menuEditKey(getPlayer(context), input);
			}
			if (menuAccess == ALTER_CONTAINER_DATA_MENU) {
				ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
				ContainerDataBuilder.Builder builder = data.getBuilder().setDisplayname(input);

				containerDataCache.setContainerData(container, builder.build());
				CHANGE_DISPLAYNAME_CONTINEDATA_CONFIRM.sendMessage(getPlayer(context), input);
				runtaskLater(40, () ->
						new AlterContainerDataMenu(container).menuOpen(getPlayer(context)), false);
			}
			return null;
		}

	}

	private void menuEditKey(Player player, String input) {
		if (menuAccess == EDITKEY) {
			if (setLore) {
				org.brokenarrow.lootboxes.builder.KeysData keysData = containerDataCache.getCacheKey(container, keyName);
				List<String> loreList = keysData.getLore();
				if (input.contains("row-")) {
					for (Map.Entry<Integer, String> entry : formatInput(input).entrySet()) {
						int row = entry.getKey();
						String text = entry.getValue();
						if (loreList.size() > row) {
							loreList.set(row, text);
						} else {
							for (int i = loreList.size(); i < row; i++)
								loreList.add("");
							loreList.add(text);
						}
					}
				} else if (input.startsWith("remove")) {
					int removeAfterEveryRun = 0;
					for (int integer : removeRow(input)) {
						integer -= removeAfterEveryRun;
						if (loreList.size() > integer)
							loreList.remove(integer);
						removeAfterEveryRun++;
					}
				} else
					loreList.add(input);
				containerDataCache.setKeyData(KeysToSave.LORE, loreList, container, keyName);
			} else
				containerDataCache.setKeyData(KeysToSave.DISPLAY_NAME, input, container, keyName);
			CHANGE_DISPLAYNAME_AND_LORE_CONFIRM.sendMessage(player);
			new EditKey(container, keyName).menuOpen(player);
		}
	}

	private Map<Integer, String> formatInput(String inputString) {
		String[] splitInput = inputString.split("row-");
		Map<Integer, String> values = new LinkedHashMap<>();
		for (String inputToCheck : splitInput) {
			if (inputToCheck.length() <= 0) continue;

			int end = inputToCheck.indexOf(':');
			int row = 0;
			try {
				row = Integer.parseInt(inputToCheck.substring(0, end));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			String text = inputToCheck.substring(end + 1);
			values.put(row, text);
		}
		return values;
	}

	private Set<Integer> removeRow(String inputString) {
		Set<Integer> numbers = new LinkedHashSet<>();

		int end = inputString.indexOf(':');
		String text = inputString.substring(end + 1);
		String[] splitInput = text.split(",");

		if (splitInput.length > 0) {
			for (String splt : splitInput) {
				numbers.add(Integer.parseInt(splt));
			}
		} else
			numbers.add(Integer.parseInt(text));
		return numbers;
	}
}

