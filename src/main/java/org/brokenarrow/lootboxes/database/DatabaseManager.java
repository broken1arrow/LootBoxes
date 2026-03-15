package org.brokenarrow.lootboxes.database;

import org.broken.arrow.library.database.builders.DataWrapper;
import org.broken.arrow.library.database.builders.LoadDataWrapper;
import org.broken.arrow.library.database.construct.query.QueryBuilder;
import org.broken.arrow.library.database.construct.query.builder.CreateTableHandler;
import org.broken.arrow.library.database.construct.query.builder.tablebuilder.SQLConstraints;
import org.broken.arrow.library.database.construct.query.columnbuilder.ColumnManager;
import org.broken.arrow.library.database.construct.query.utlity.DataType;
import org.broken.arrow.library.database.construct.query.utlity.QueryDefinition;
import org.broken.arrow.library.database.core.Database;
import org.broken.arrow.library.database.core.databases.SQLite;
import org.broken.arrow.library.serialize.utility.converters.LocationSerializer;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.BlockKey;
import org.brokenarrow.lootboxes.lootdata.LootContainerRandomCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

public class DatabaseManager {
    private static Lootboxes plug;
    private Database database;

    public DatabaseManager(@NotNull final Lootboxes plugin) {
        plug = plugin;
        this.setDataBase();
    }

    public void registerTable() {
        this.setTables();
        this.registerTables();
    }

    public void loadAll() {
        this.loadAllDataToCache();
    }

    public void saveAll(final boolean disabling) {
        saveContainerData(disabling);
    }

    public Database getDatabase() {
        return database;
    }

    public void setTables() {
        if (database != null) {
            database.addTable(queryBuilder -> this.getTableForLocations(queryBuilder, this.getContainerTableName()));
        }
    }

    private void saveContainerData(final boolean disabling) {
        final LootContainerRandomCache containerRandomCache = plug.getLootContainerRandomCache();
        if (disabling)
            saveContainers(containerRandomCache, true);
        else
            Bukkit.getScheduler().runTask(plug, () -> saveContainers(containerRandomCache, disabling));
    }

    private void saveContainers(final LootContainerRandomCache inventoryHolders, final boolean disabling) {
        List<DataWrapper> snapshotsTaken = new ArrayList<>();
        Map<BlockKey, LootContainerRandomCache.RandomLootData> cachedLootContainerLocations = inventoryHolders.getCachedLootContainerLocations();
        for (Entry<BlockKey, LootContainerRandomCache.RandomLootData> entry : cachedLootContainerLocations.entrySet()) {
            LootContainerRandomCache.RandomLootData value = entry.getValue();
            Location loc = entry.getKey().getLocation();
            if (value == null || loc == null) continue;
            DataWrapper.PrimaryWrapper primary = new DataWrapper.PrimaryWrapper(
                    buildPrimaryData(loc),
                    builder -> builder
                            .where("world_id").equal(loc.getWorld().getUID().toString())
                            .and().where("loc_x").equal(loc.getBlockX())
                            .and().where("loc_y").equal(loc.getBlockY())
                            .and().where("loc_z").equal(loc.getBlockZ())
            );
            snapshotsTaken.add(new DataWrapper(primary, value));
        }
        if (!snapshotsTaken.isEmpty()) {
            if (disabling)
                this.getDatabase().saveAll(this.getContainerTableName(), snapshotsTaken);
            else
                Bukkit.getScheduler().runTaskAsynchronously(plug, () ->
                        this.getDatabase().saveAll(this.getContainerTableName(), snapshotsTaken)
                );
        }
    }

    private Map<String, Object> buildPrimaryData(final Location loc) {
        Map<String, Object> data = new HashMap<>();
        data.put("world_id", loc.getWorld().getUID().toString());
        data.put("loc_x", loc.getBlockX());
        data.put("loc_y", loc.getBlockY());
        data.put("loc_z", loc.getBlockZ());
        return data;
    }

    public void loadAllDataToCache() {
        loadAllContainersRandomSpawn();
    }


    public void loadAllContainersRandomSpawn() {
        List<LoadDataWrapper<LootContainerRandomCache.RandomLootData>> loadDataList = database.loadAll(this.getContainerTableName(), LootContainerRandomCache.RandomLootData.class);
        if (loadDataList == null) return;
        for (LoadDataWrapper<LootContainerRandomCache.RandomLootData> loadData : loadDataList) {
            Object uuid = loadData.getPrimaryValue("world_id");
            Object x = loadData.getPrimaryValue("loc_x");
            Object y = loadData.getPrimaryValue("loc_y");
            Object z = loadData.getPrimaryValue("loc_z");
            boolean validUuid = true;
            try {
                UUID.fromString(uuid + "");
            } catch (IllegalArgumentException exception) {
                validUuid = false;
            }

            if (uuid != null && validUuid) {
                BlockKey blockKey = BlockKey.of(UUID.fromString(uuid + ""), Integer.parseInt(x + ""), Integer.parseInt(y + ""), Integer.parseInt(z + ""));
                if (blockKey.isSet()) {
                    plug.getLootContainerRandomCache().putLootCachedLocation(blockKey, loadData.getDeSerializedData());
                }
            }
        }

    }

    public void setDataBase() {
        this.database = new SQLite(Lootboxes.getInstance().getDataFolder() + "/" + "database.db");
    }


    private void registerTables() {
        this.database.createTables((table, primaryConstraintWrapper) -> {
            if (table.equals(this.getContainerTableName())) {
                primaryConstraintWrapper.forEachLoadedData(loadDataWrapper -> {
                    final Object rawLoc = loadDataWrapper.get("Location");
                    if (rawLoc == null || rawLoc.toString().isEmpty()) return null;
                    Location location = LocationSerializer.deserializeLoc(rawLoc);
                    UUID worldID = null;
                    if (location == null || location.getWorld() == null) {
                        String[] parts = rawLoc.toString().split(" ");
                        if (parts.length > 0) {
                            final String world = parts[0];
                            final World bukkitWorld = Bukkit.getWorld(world);
                            if (bukkitWorld == null)
                                worldID = UUID.randomUUID();
                            else
                                worldID = bukkitWorld.getUID();
                        }
                    } else {
                        worldID = location.getWorld().getUID();
                    }

                    Object uuid = loadDataWrapper.get("world_id");
                    if (uuid != null && !uuid.toString().isEmpty()) {
                        worldID = UUID.fromString(uuid + "");
                    }
                    if (location == null) return null;

                    Map<String, Object> map = new HashMap<>();
                    map.put("world_id", worldID + "");
                    map.put("loc_x", location.getBlockX());
                    map.put("loc_y", location.getBlockY());
                    map.put("loc_z", location.getBlockZ());
                    return new DataWrapper.PrimaryWrapper(map, (builder) ->
                            builder.where("world_id").equal(location.getWorld().getUID().toString())
                                    .and().where("loc_x").equal(location.getBlockX())
                                    .and().where("loc_y").equal(location.getBlockY())
                                    .and().where("loc_z").equal(location.getBlockZ())
                    );
                });
            }
        });
    }

    public void removeRandomSpawnedContainer(Location location) {
        QueryDefinition queryBuilder = QueryDefinition.of(new QueryBuilder().setGlobalEnableQueryPlaceholders(false).deleteFrom(this.getContainerTableName()).where(whereBuilder ->
                whereBuilder.where("world_id").equal("'"+ location.getWorld().getUID().toString() + "'")
                        .and().where("loc_x").equal(location.getBlockX())
                        .and().where("loc_y").equal(location.getBlockY())
                        .and().where("loc_z").equal(location.getBlockZ())
        ));
        ((SQLite) this.getDatabase()).executeQuery(queryBuilder, statementWrapper -> {
            try  {
               statementWrapper.getContextResult().execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public String getContainerTableName() {
        return "random_container_loc";
    }

    public CreateTableHandler getTableForLocations(QueryBuilder queryBuilder, String tableName) {
        return queryBuilder.createTableIfNotExists(tableName)
                .addColumns(ColumnManager
                        .tableOf("world_id", DataType.varchar(100), SQLConstraints.primaryKey())
                        .column("loc_x", DataType.dataInt(), SQLConstraints.primaryKey())
                        .column("loc_y", DataType.dataInt(), SQLConstraints.primaryKey())
                        .column("loc_z", DataType.dataInt(), SQLConstraints.primaryKey())
                        .column("container_key", DataType.varchar(200))
                        .build()

                );
    }
}
