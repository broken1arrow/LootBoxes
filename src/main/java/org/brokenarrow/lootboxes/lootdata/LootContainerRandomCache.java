package org.brokenarrow.lootboxes.lootdata;

import org.broken.arrow.library.yaml.YamlFileManager;
import org.broken.arrow.library.yaml.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.BlockKey;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LootContainerRandomCache extends YamlFileManager {
    private final Map<BlockKey, String> cachedLootContainerLocations = new HashMap<>();
    private final Map<String, String> needBeSaved = new ConcurrentHashMap<>();

    public LootContainerRandomCache() {
        super(Lootboxes.getInstance(), "containersSpawned", false, true);
        this.setExtension("db");
    }

    public void tickTask() {
        if (!needBeSaved.isEmpty()) {
            Iterator<String> it = needBeSaved.keySet().iterator();
            while (it.hasNext()) {
                String containerName = it.next();
                this.save(containerName);
                it.remove();
            }
        }
    }

    public Map<BlockKey, String> getCachedLootContainerLocations() {
        return cachedLootContainerLocations;
    }

    public String getCachedLootContainerLocation(Location location) {
        return cachedLootContainerLocations.get(BlockKey.of(location));
    }

    public void putLootCachedLocation(Location cachedLocations, String containerName) {
        this.cachedLootContainerLocations.put(BlockKey.of(cachedLocations), containerName);
    }

    public void putLootCachedLocation(BlockKey blockKey, String containerName) {
        String key = this.cachedLootContainerLocations.put(blockKey, containerName);
        if (key == null)
            needBeSaved.put(containerName, "");
    }

    public void removeCachedLootContainerLocation(final Location cachedLocation) {
        this.cachedLootContainerLocations.remove(BlockKey.of(cachedLocation));
    }

    @Override
    protected void saveDataToFile(@NotNull final File file, @NotNull final ConfigurationWrapper configurationWrapper) {
        try {
            File tmp = new File(file.getParentFile(), file.getName() + ".temp");
            String nameOfFile = this.getNameOfFile(file.toString());
            List<BlockKey> blockKeys = cachedLootContainerLocations.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(nameOfFile))
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            final FileConfiguration configuration = new YamlConfiguration();
            configuration.set("Locations", blockKeys);
            this.saveToFile(tmp, configuration);
            Files.move(
                    tmp.toPath(),
                    file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (final Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save cached container data", ex);
        }
    }

    @Override
    protected void loadSettingsFromYaml(final File file, FileConfiguration fileConfiguration) {
        File realFile = resolveFile(file);
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(realFile);
        List<BlockKey> locations = new ArrayList<>();
        for (Object obj : configuration.getList("Locations", Collections.emptyList())) {
            if (obj instanceof BlockKey) {
                BlockKey key = (BlockKey) obj;
                locations.add(key);
            }
        }
        if (realFile.getName().endsWith(".temp")) {
            try {
                Files.move(realFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not correctly resolve the random cached lootcontainers.", e);
            }
        }
        final String containerName = this.getNameOfFile(file.toString());
        locations.forEach(blockKey -> putLootCachedLocation(blockKey, containerName));

    }

    public File resolveFile(File file) {
        File tmp = new File(file.getParentFile(), file.getName() + ".temp");
        if (tmp.exists()) {
            if (!file.exists() || tmp.lastModified() > file.lastModified()) {
                return tmp;
            }
        }
        return file;
    }

}
