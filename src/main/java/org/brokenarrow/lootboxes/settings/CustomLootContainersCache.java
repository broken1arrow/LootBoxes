package org.brokenarrow.lootboxes.settings;

import org.broken.arrow.library.nbt.RegisterNbtAPI;
import org.broken.arrow.library.yaml.YamlFileManager;
import org.broken.arrow.library.yaml.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.CustomContainer;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomLootContainersCache extends YamlFileManager {

    private final Set<CustomContainer> chests = new HashSet<>();

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

    public boolean containsContainer(final ItemStack itemStack) {
        return chests.contains(new CustomContainer(itemStack));
    }

    public Set<CustomContainer> getContainers() {
        return chests;
    }

    public List<ItemStack> getContainerItems() {
        return chests.stream().map(CustomContainer::getContainer).collect(Collectors.toList());
    }

    public void addContainer(final ItemStack itemStack) {
        chests.add(new CustomContainer(itemStack));
    }

    @Override
    protected void saveDataToFile(@NotNull File file, @NotNull ConfigurationWrapper configurationWrapper) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("chests", RegisterNbtAPI.serializeItemStack(chests.stream().map(CustomContainer::getContainer).toArray(ItemStack[]::new)));
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void loadSettingsFromYaml(File file, FileConfiguration loadedConfig) {
        Object rawData = loadedConfig.get("chests");
        if (rawData instanceof byte[]) {
            byte[] primitiveArray = (byte[]) rawData;
            ItemStack[] itemStacks = RegisterNbtAPI.deserializeItemStack(primitiveArray);
            if(itemStacks != null) {
                for (ItemStack stack : itemStacks) {
                    chests.add(new CustomContainer(stack));
                }
            }
            }
        }

        // final ItemStack[] itemStack = RegisterNbtAPI.deserializeItemStack(primitiveArray);
    }


}
