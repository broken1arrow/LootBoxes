package org.brokenarrow.lootboxes.commandprompt;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.lootdata.KeysData;
import org.brokenarrow.lootboxes.menus.EditKeysToOpen;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChangeDisplaynameLore extends SimpleConversation {
	private final ContainerData containerData = ContainerData.getInstance();
	private String container;
	private String keyName;
	private boolean setlore;

	public ChangeDisplaynameLore(String containerData, String keyName, boolean setlore) {
		this.container = containerData;
		this.keyName = keyName;
		this.setlore = setlore;
	}

	@Override
	protected Prompt getFirstPrompt() {
		return new Commandprompt();
	}

	public class Commandprompt extends SimplePromp {

		@Override
		protected String getPrompt(ConversationContext context) {
			if (setlore) {
				return "Use row-0: to change first line, to edit several rows: row-0: your text. row-1: your second row.";
			} else
				return "Type in display name on this item.";
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {

			if (setlore) {
				ContainerDataBuilder.KeysData keysData = containerData.getCacheKeys(container, keyName);
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
				} else
					loreList.add(input);
				containerData.setKeyData(KeysData.LORE, loreList, container, keyName);
			} else
				containerData.setKeyData(KeysData.DISPLAY_NAME, input, container, keyName);

			new EditKeysToOpen.EditKey(container, keyName).menuOpen(getPlayer(context));
			return null;
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
	}
}
