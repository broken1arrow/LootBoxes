package org.brokenarrow.lootboxes.commandprompt;

import org.broken.arrow.library.itemcreator.SkullCreator;
import org.broken.arrow.library.prompt.SimpleConversation;
import org.broken.arrow.library.prompt.SimplePrompt;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.menus.keys.EditKeysToOpenMenu;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class SetKeyName extends SimpleConversation {

    private final ItemStack[] itemStacks;
    private final String containerKey;
    private final KeyDropData keyDropData = KeyDropData.getInstance();
    private final ContainerDataCache containerCache = Lootboxes.getInstance().getContainerDataCache();
    private static final Map<Player, StoreData> chachedPlayer = new HashMap<>();

    public SetKeyName(ItemStack[] itemStacks, String containerKey) {
        super(Lootboxes.getInstance());
        this.itemStacks = itemStacks;
        this.containerKey = containerKey;
    }

    @Override
    public Prompt getFirstPrompt() {
        return new Commandprompt();
    }

    @Override
    protected void onConversationEnd(ConversationAbandonedEvent event) {
        if (event.getCanceller() instanceof Player) {
            final Player player = (Player) event.getCanceller();
            chachedPlayer.remove(player);
        }
    }

    public class Commandprompt extends SimplePrompt {

        @Override
        protected String getPrompt(ConversationContext context) {
            Player player = getPlayer(context);

            for (int i = 0; i < itemStacks.length; i++) {
                ItemStack itemStack = itemStacks[i];
                if (itemStack == null) continue;
                StoreData data = chachedPlayer.get(player);
                Set<Integer> numbersUsed = new HashSet<>();
                if (data != null)
                    numbersUsed.addAll(data.getNumbersUsed());
                numbersUsed.add(i);
                chachedPlayer.put(player, new StoreData(i, numbersUsed));
                return SET_NAME_ON_KEY_TYPE_NAME.languageMessagePrefix(itemStack.getType());
            }
            return SET_NAME_ON_KEY_TYPE_NAME.languageMessagePrefix();
        }

        @Nullable
        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
            Player player = getPlayer(context);

            if (containerCache.containsKeyName(containerKey, input)) {
                SET_NAME_ON_KEY_DUPLICATE.sendMessage(player, input);
                return getFirstPrompt();
            }

            int placeInList = chachedPlayer.get(player).getNumber();
            ItemStack item = itemStacks[placeInList];
            Prompt prompt = null;
            if (item != null) {
                if (item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        final KeysData data = new KeysData(
                                input,
                                meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name().toLowerCase(),
                                containerCache.getCacheContainerData(containerKey).getLootTableLinked(),
                                item.getAmount(),
                                item.getType(),
                                meta.hasLore() ? meta.getLore() : new ArrayList<>());
                        prompt = containerCache.write(containerKey, builder -> {
                            if (builder.containsKey(input)) {
                                SET_NAME_ON_KEY_DUPLICATE.sendMessage(player, input);
                                return getFirstPrompt();
                            }
                            if (meta instanceof SkullMeta) {
                                data.setUrl(SkullCreator.getSkullUrl((SkullMeta) meta));
                            }
                            if(Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_16)){
                                if(meta.hasCustomModelData()){
                                    data.setModelData(meta.getCustomModelData());
                                }
                            }
                            if (meta instanceof Damageable)
                                data.setDamage((byte) ((Damageable) meta).getDamage());
                            builder.setKeysData(input, data);
                            return null;
                        });
                    }
                } else {
                    final KeysData data = new KeysData(
                            input,
                            item.getType().name().toLowerCase(),
                            containerCache.getCacheContainerData(containerKey).getLootTableLinked(),
                            item.getAmount(),
                            item.getType(),
                            new ArrayList<>());
                    prompt = containerCache.write(containerKey, builder -> {
                        if (builder.containsKey(input)) {
                            SET_NAME_ON_KEY_DUPLICATE.sendMessage(player, input);
                            return getFirstPrompt();
                        }
                        builder.setKeysData(input, data);
                        return null;
                    });
                }
            }
            if (prompt != null)
                return getFirstPrompt();

            if (!checkAllItems(player)) {
                SET_NAME_ON_KEY_CONFIRM.sendMessage(player, input);
                return getFirstPrompt();
            }
            SET_NAME_ON_KEY_CONFIRM_FINISH.sendMessage(player, item != null ? item.getType() : "Item null");
            new EditKeysToOpenMenu(containerKey).menuOpen(getPlayer(context));
            return null;
        }

        private boolean checkAllItems(Player player) {
            StoreData data = chachedPlayer.get(player);
            boolean hasCheckAllItems = false;
            if (data != null) {
                List<Integer> addnumbers = new ArrayList<>();
                for (int i = 0; i < itemStacks.length; i++) {
                    ItemStack itemStack = itemStacks[i];
                    if (itemStack == null) continue;
                    addnumbers.add(i);
                }
                for (Integer number : addnumbers) {
                    hasCheckAllItems = data.getNumbersUsed().contains(number);
                }
            }
            return hasCheckAllItems;
        }
    }

    public class StoreData {
        private final int number;
        private final Set<Integer> numbersUsed;

        public StoreData(int number, Set<Integer> numbersUsed) {
            this.number = number;
            this.numbersUsed = numbersUsed;
        }

        public int getNumber() {
            return number;
        }

        public Set<Integer> getNumbersUsed() {
            return numbersUsed;
        }
    }
}

