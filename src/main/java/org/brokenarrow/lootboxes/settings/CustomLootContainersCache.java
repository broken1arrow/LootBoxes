package org.brokenarrow.lootboxes.settings;

import org.broken.arrow.library.nbt.RegisterNbtAPI;
import org.broken.arrow.library.yaml.YamlFileManager;
import org.broken.arrow.library.yaml.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.CustomContainer;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CustomLootContainersCache extends YamlFileManager {

    private final Set<CustomContainer> chests = new HashSet<>();
    private final Map<Material, List<CustomContainer>> chestsCache = new HashMap<>();

    public CustomLootContainersCache() {
        super(Lootboxes.getInstance(), "custom_loot_containers.yml", true, true);
        addContainer(new ItemStack(Material.CHEST));
        addContainer(new ItemStack(Material.TRAPPED_CHEST));
        addContainer(new ItemStack(Material.SHULKER_BOX));
        addContainer(new ItemStack(Material.HOPPER));
        addContainer(new ItemStack(Material.DISPENSER));
        addContainer(new ItemStack(Material.DROPPER));
        if (Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_14))
            addContainer(new ItemStack(Material.BARREL));
    }

    public boolean containsContainerType(Material type) {
        return chestsCache.containsKey(type);
    }

    public Set<CustomContainer> getContainers() {
        return chests;
    }

    public Map<Material, List<CustomContainer>> getChestsCache() {
        return chestsCache;
    }

    public CustomContainer getSimilarContainer(Material material, ItemStack itemStack) {
        List<CustomContainer> materialType = chestsCache.get(material);
        if (materialType == null) return null;

        CustomContainer fallback = null;
        String targetName = getDisplayName(itemStack);

        for (CustomContainer container : materialType) {
            ItemStack cItem = container.getContainer();

            if (cItem.isSimilar(itemStack)) {
                return container;
            }

            if (fallback == null && Objects.equals(getDisplayName(cItem), targetName)) {
                fallback = container;
            }

            if (fallback == null) {
                fallback = container;
            }
        }

        return fallback;
    }

    public List<ItemStack> getContainerItems() {
        return chestsCache.values().stream().flatMap(customContainers -> customContainers.stream().map(CustomContainer::getContainer)).collect(Collectors.toList());
    }

    public void addContainer(final ItemStack itemStack) {
        chestsCache.computeIfAbsent(itemStack.getType() ,material -> new ArrayList<>()).add(new CustomContainer(itemStack));
    }

    @Override
    protected void saveDataToFile(@NotNull File file, @NotNull ConfigurationWrapper configurationWrapper) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        for (Map.Entry<Material, List<CustomContainer>> custom : chestsCache.entrySet()) {
            yamlConfiguration.set("chests." + custom.getKey().name(), RegisterNbtAPI.serializeItemStack(custom.getValue().stream().map(CustomContainer::getContainer).toArray(ItemStack[]::new)));
        }
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void loadSettingsFromYaml(File file, FileConfiguration loadedConfig) {
        ConfigurationSection configurationSection = loadedConfig.getConfigurationSection("chests");
        if(configurationSection == null)
            return;

        for(String section : configurationSection.getKeys(false)) {
            Object rawData = loadedConfig.get("chests." + section);
            if (rawData instanceof byte[]) {
                byte[] primitiveArray = (byte[]) rawData;
                ItemStack[] itemStacks = RegisterNbtAPI.deserializeItemStack(primitiveArray);
                if (itemStacks != null) {
                    for (ItemStack stack : itemStacks) {
                        chestsCache.computeIfAbsent(stack.getType() ,material -> new ArrayList<>()).add(new CustomContainer(stack));
                    }
                }
            }
        }
    }

    private String getDisplayName(ItemStack item) {
        if (!item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() ? meta.getDisplayName() : null;
    }


}

