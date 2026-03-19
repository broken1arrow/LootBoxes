package org.brokenarrow.lootboxes.builder;

import com.google.common.base.Enums;
import org.brokenarrow.lootboxes.untlity.Facing;
import org.brokenarrow.lootboxes.untlity.LocationWrapper;
import org.brokenarrow.lootboxes.untlity.errors.Valid;
import org.brokenarrow.lootboxes.untlity.particles.ParticlesConversion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.broken.arrow.library.serialize.utility.converters.ObjectConverter.getBoolean;
import static org.brokenarrow.lootboxes.untlity.CheckCastToClazz.castList;
import static org.brokenarrow.lootboxes.untlity.CheckCastToClazz.castMap;

public class ContainerDataBuilder implements ConfigurationSerializable {
    String lootTableLinked;
    String permissionForRandomSpawn;
    Material icon;
    Material randomLootContainerItem;
    Facing randomLootContainerFacing;
    String displayName;
    List<String> lore;
    Set<String> worlds;
    Map<String, ParticleEffect> particleEffects;
    Map<Location, ContainerData> containerData;
    Map<String, KeysData> keysData;
    LocationWrapper spawnLocation;
    boolean spawningContainerWithCooldown;
    boolean enchant;
    boolean randomSpawn;
    boolean showTitle;
    boolean containerShallGlow;
    boolean spawnContainerFromWorldCenter;
    boolean spawnContainerFromPlayerCenter;
    boolean spawnOnSurface;
    long cooldown;
    int attempts;
    int minRadius;
    int maxRadius;
    LootContainerBuilder lootContainerBuilder;

    public String getLootTableLinked() {
        return lootTableLinked;
    }

    public String getPermissionForRandomSpawn() {
        return permissionForRandomSpawn;
    }

    public boolean hasPermissionForRandomSpawn(Player player) {
        String permission = this.getPermissionForRandomSpawn();
        if (permission == null || permission.isEmpty())
            return true;
        return player.hasPermission(permission);
    }

    @Nullable
    public Map<String, ParticleEffect> getParticleEffects() {
        return particleEffects;
    }

    @Nullable
    public ParticleEffect getParticleEffect(final String o) {
        if (o == null) return null;
        Map<String, ParticleEffect> particleEffects = this.getParticleEffects();
        if (particleEffects == null || particleEffects.isEmpty()) return null;

        return particleEffects.get(o);
    }

    public Material getIcon() {
        return icon;
    }

    public Material getRandomLootContainerItem() {
        return randomLootContainerItem;
    }

    public Facing getRandomLootContainerFacing() {
        if (randomLootContainerFacing == null)
            return Facing.RANDOM;
        return randomLootContainerFacing;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public Map<Location, ContainerData> getLinkedContainerData() {
        return containerData;
    }

    public Map<String, KeysData> getKeysData() {
        return keysData;
    }

    @Nullable
    public KeysData getKeysData(@NotNull final String keyName) {
        return keysData.get(keyName);
    }

    public LocationWrapper getSpawnLocation() {
        return spawnLocation;
    }

    public boolean isSpawningContainerWithCooldown() {
        return spawningContainerWithCooldown;
    }

    public boolean isEnchant() {
        return enchant;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public boolean isContainerShallGlow() {
        return containerShallGlow;
    }

    public boolean isRandomSpawn() {
        return randomSpawn;
    }

    public boolean isSpawnContainerFromWorldCenter() {
        return spawnContainerFromWorldCenter;
    }

    public boolean isSpawnContainerFromPlayerCenter() {
        return spawnContainerFromPlayerCenter;
    }

    public boolean isSpawnOnSurface() {
        return spawnOnSurface;
    }

    public long getCooldown() {
        return cooldown;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getMinRadius() {
        return minRadius;
    }

    public int getMaxRadius() {
        return maxRadius;
    }

    public boolean contains(@Nullable final World world) {
        if (world == null) return false;
        return this.worlds.contains(world.getName());
    }

    public boolean contains(@Nullable final String worldName) {
        return this.worlds.contains(worldName);
    }

    public Set<String> getWorlds() {
        return worlds;
    }

    public boolean allowedWorldToSpawn(final Location location) {
        if (worlds.isEmpty()) return true;

        return contains(location.getWorld());
    }

    public LootContainerBuilder getBuilder() {
        return lootContainerBuilder;
    }

    public LootContainerData convertToLootContainer() {
        LootContainerData lootContainerData = new LootContainerData();
        lootContainerData
                .setContainerData(this.containerData)
                .setContainerDataLinkedToLootTable(lootTableLinked)
                .setSpawningContainerWithCooldown(spawningContainerWithCooldown)
                .setCooldown(cooldown)
                .setParticleEffects(particleEffects != null ? particleEffects : new HashMap<>())
                .setRandomLootWorlds(worlds != null ? new ArrayList<>(worlds) : new ArrayList<>())
                .setEnchant(enchant)
                .setIcon(icon)
                .setRandomLootContainerItem(randomLootContainerItem)
                .setRandomLootContainerFacing(randomLootContainerFacing)
                .setDisplayName(displayName)
                .setLore(lore)
                .setContainerShallGlow(containerShallGlow)
                .setShowTitle(showTitle)
                .setRandomSpawn(randomSpawn)
                .setContainerData(containerData)
                .setKeysData(keysData)
                .setAttempts(attempts)
                .setSpawnContainerFromWorldCenter(spawnContainerFromWorldCenter)
                .setSpawnContainerFromPlayerCenter(spawnContainerFromPlayerCenter)
                .setSpawnOnSurface(spawnOnSurface)
                .setMinRadius(minRadius)
                .setMaxRadius(maxRadius)
                .setSpawnLocation(spawnLocation)
                .setPermissionForRandomSpawn(permissionForRandomSpawn);
        return lootContainerData;
    }

    public static final class LootContainerBuilder extends ContainerDataBuilder {

        public LootContainerBuilder setRandomLootWorlds(List<String> worlds) {
            this.worlds = new HashSet<>(worlds);
            return this;
        }

        public LootContainerBuilder addWorld(String name) {
            this.worlds.add(name);
            return this;
        }

        public LootContainerBuilder removeWorld(String name) {
            this.worlds.remove(name);
            return this;
        }

        public LootContainerBuilder setSpawnContainerFromWorldCenter(final boolean spawnContainerFromWorldCenter) {
            this.spawnContainerFromWorldCenter = spawnContainerFromWorldCenter;
            return this;
        }

        public LootContainerBuilder setPermissionForRandomSpawn(String permissionForRandomSpawn) {
            this.permissionForRandomSpawn = permissionForRandomSpawn;
            return this;
        }

        public LootContainerBuilder setSpawnContainerFromPlayerCenter(final boolean spawnContainerFromPlayerCenter) {
            this.spawnContainerFromPlayerCenter = spawnContainerFromPlayerCenter;
            return this;
        }

        public LootContainerBuilder setSpawnOnSurface(final boolean spawnOnSurface) {
            this.spawnOnSurface = spawnOnSurface;
            return this;
        }

        public LootContainerBuilder setContainerDataLinkedToLootTable(final String containerDataLinkedToLootTable) {
            this.lootTableLinked = containerDataLinkedToLootTable;
            return this;
        }

        public LootContainerBuilder setParticleEffects(final Map<String, ParticleEffect> particleEffects) {
            this.particleEffects = particleEffects;
            return this;
        }

        public LootContainerBuilder setParticleEffect(@NotNull final String particle, @NotNull final ParticleEffect.Builder particleBuilder) {
            if (this.particleEffects == null)
                this.particleEffects = new HashMap<>();

            this.particleEffects.put(particle, particleBuilder.build());
            return this;
        }

        public LootContainerBuilder setIcon(final Material icon) {
            this.icon = icon;
            return this;
        }

        public LootContainerBuilder setRandomLootContainerItem(Material randomLootContainerItem) {
            this.randomLootContainerItem = randomLootContainerItem;
            return this;
        }

        public LootContainerBuilder setRandomLootContainerFacing(Facing randomLootContainerFacing) {
            this.randomLootContainerFacing = randomLootContainerFacing;
            return this;
        }

        public LootContainerBuilder setDisplayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        public LootContainerBuilder setLore(final List<String> lore) {
            this.lore = lore;
            return this;
        }

        public LootContainerBuilder setContainerData(final Map<Location, ContainerData> containerData) {
            this.containerData = containerData;
            return this;
        }

        public LootContainerBuilder setKeysData(final Map<String, KeysData> keysData) {
            this.keysData = keysData;
            return this;
        }

        public LootContainerBuilder setKeysData(final String keyName, final KeysData data) {
            if (this.keysData == null)
                this.keysData = new HashMap<>();
            this.keysData.put(keyName, data);
            return this;
        }

        public LootContainerBuilder writeKeysData(final String keyName, final Consumer<KeysDataWrapper> callBack) {
            if (this.keysData == null)
                this.keysData = new HashMap<>();
            final KeysData keysData = this.keysData.getOrDefault(keyName, new KeysData(keyName, "", "", 1, Material.TRIPWIRE_HOOK, new ArrayList<>()));
            keysData.updateKeyData(callBack);
            return this.setKeysData(keyName, keysData);
        }

        public LootContainerBuilder setSpawnLocation(final LocationWrapper spawnLocation) {
            this.spawnLocation = spawnLocation;
            return this;
        }

        public LootContainerBuilder setSpawningContainerWithCooldown(final boolean spawningContainerWithCooldown) {
            this.spawningContainerWithCooldown = spawningContainerWithCooldown;
            return this;
        }

        public LootContainerBuilder setEnchant(final boolean enchant) {
            this.enchant = enchant;
            return this;
        }

        public LootContainerBuilder setRandomSpawn(final boolean randomSpawn) {
            this.randomSpawn = randomSpawn;
            return this;
        }

        public LootContainerBuilder setShowTitle(boolean showTitle) {
            this.showTitle = showTitle;
            return this;
        }

        public LootContainerBuilder setContainerShallGlow(boolean containerShallGlow) {
            this.containerShallGlow = containerShallGlow;
            return this;
        }

        public LootContainerBuilder setCooldown(final long cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public LootContainerBuilder setAttempts(int attempts) {
            this.attempts = attempts;
            return this;
        }

        public LootContainerBuilder setMinRadius(final int minRadius) {
            this.minRadius = minRadius;
            return this;
        }

        public LootContainerBuilder setMaxRadius(final int maxRadius) {
            this.maxRadius = maxRadius;
            return this;
        }

        public ContainerDataBuilder build() {
            this.lootContainerBuilder = this;
            return this.lootContainerBuilder;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "containerDataLinkedToLootTable='" + lootTableLinked + '\'' +
                    ", icon=" + icon +
                    ", randonLootContainerItem=" + randomLootContainerItem +
                    ", randonLootContainerFaceing=" + randomLootContainerFacing +
                    ", displayname='" + displayName + '\'' +
                    ", lore=" + lore +
                    ", particleEffects=" + particleEffects +
                    ", containerData=" + containerData +
                    ", keysData=" + keysData +
                    ", spawningContainerWithCooldown=" + spawningContainerWithCooldown +
                    ", enchant=" + enchant +
                    ", randomSpawn=" + randomSpawn +
                    ", showTitel=" + showTitle +
                    ", contanerShallglow=" + containerShallGlow +
                    ", cooldown=" + cooldown +
                    ", attempts=" + attempts +
                    '}';
        }

    }

    @Override
    public String toString() {
        return "ContainerDataBuilder{" +
                "lootTableLinked='" + lootTableLinked + '\'' +
                ", icon=" + icon +
                ", randonLootContainerItem=" + randomLootContainerItem +
                ", randonLootContainerFaceing=" + randomLootContainerFacing +
                ", displayname='" + displayName + '\'' +
                ", lore=" + lore +
                ", particleEffects=" + particleEffects +
                ", containerData=" + containerData +
                ", keysData=" + keysData +
                ", spawningContainerWithCooldown=" + spawningContainerWithCooldown +
                ", enchant=" + enchant +
                ", randomSpawn=" + randomSpawn +
                ", showTitel=" + showTitle +
                ", contanerShallglow=" + containerShallGlow +
                ", cooldown=" + cooldown +
                ", attempts=" + attempts +
                ", builder=" + lootContainerBuilder +
                '}';
    }

    /**
     * Creates a Map representation of this class.
     * <p>
     * This class must provide a method to restore this class, as defined in
     * the {@link ConfigurationSerializable} interface javadocs.
     *
     * @return Map containing the current state of this class
     */
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> keysData = new LinkedHashMap<>();
        keysData.put("LootTable_linked", this.lootTableLinked);
        keysData.put("permission", this.permissionForRandomSpawn == null ? "" : this.permissionForRandomSpawn);
        keysData.put("Icon", this.icon + "");
        keysData.put("Random_loot_container", this.randomLootContainerItem + "");
        keysData.put("Random_loot_faceing", this.randomLootContainerFacing + "");
        keysData.put("Worlds_allow_spawn", new ArrayList<>(this.worlds));
        keysData.put("Display_name", this.displayName);
        keysData.put("Lore", this.lore);
        keysData.put("Spawn_on_surface", this.spawnOnSurface + "");
        if (this.particleEffects == null)
            keysData.put("Particle_effect", new HashMap<>());
        else
            keysData.put("Particle_effect", this.particleEffects.entrySet().stream().filter(effect -> effect.getKey() != null).collect(Collectors.toMap(effectEntry -> effectEntry.getKey().toString(), Map.Entry::getValue)));
        keysData.put("Spawn_with_cooldown", this.spawningContainerWithCooldown);
        keysData.put("Enchant", this.enchant);
        keysData.put("Random_spawn", this.randomSpawn);
        keysData.put("Cooldown", this.cooldown);
        keysData.put("Random_loot_titel", this.showTitle);
        keysData.put("Random_loot_glow", this.containerShallGlow);
        keysData.put("Keys", this.keysData);
        keysData.put("Attempts", this.attempts);
        keysData.put("Spawn_world_center", this.spawnContainerFromWorldCenter);
        keysData.put("Spawn_player_center", this.spawnContainerFromPlayerCenter);
        keysData.put("Min_radius", this.minRadius);
        keysData.put("Max_radius", this.maxRadius);
        keysData.put("Spawn-point", this.spawnLocation != null ? this.spawnLocation.serialize() : new HashMap<>());
        keysData.put("Containers", this.containerData);
        return keysData;
    }

    public static ContainerDataBuilder deserialize(final Map<String, Object> map) {
        final String lootTableLinked = (String) map.get("LootTable_linked");
        final String icon = (String) map.get("Icon");
        String displayName = (String) map.getOrDefault("Display_name", "");
        if (displayName == null)
            displayName = "&6Loot Chest";
        final List<String> lore = castList((List<?>) map.get("Lore"), String.class);
        final Object particleEffects = map.get("Particle_effect");
        List<String> particleEffect = null;
        Map<Object, ParticleEffect> particles = null;
        List<ParticleEffect> particleEffectList = null;
        if (particleEffects instanceof List) {
            particleEffect = castList((List<?>) particleEffects, String.class);
            if (particleEffect == null || particleEffect.isEmpty())
                particleEffectList = castList((List<?>) particleEffects, ParticleEffect.class);
        }

        if (particleEffects instanceof Map && (particleEffectList == null || particleEffectList.isEmpty()))
            particles = castMap((Map<?, ?>) particleEffects, Object.class, ParticleEffect.class);

        final Object worldsObject = map.get("Worlds_allow_spawn");
        List<String> worlds = new ArrayList<>();
        if (worldsObject != null)
            worlds = castList((List<?>) worldsObject, String.class);

        final Map<Location, ContainerData> containers = castMap((Map<?, ?>) map.get("Containers"), Location.class, ContainerData.class);
        final Map<String, KeysData> keys = castMap((Map<?, ?>) map.get("Keys"), String.class, KeysData.class);
        final boolean spawningContainerWithCooldown = (boolean) map.get("Spawn_with_cooldown");
        final boolean enchant = (boolean) map.get("Enchant");
        final boolean randomSpawn = getBoolean(map.get("Random_spawn"));
        final long cooldown = (Integer) map.get("Cooldown");
        final Material random_loot_container = Material.getMaterial(String.valueOf(map.get("Random_loot_container")));
        final String random_loot_facing = (String) map.get("Random_loot_faceing");
        final boolean random_loot_title = (boolean) map.getOrDefault("Random_loot_titel", false);
        final boolean random_loot_glow = (boolean) map.getOrDefault("Random_loot_glow", false);
        final int attempts = (Integer) map.getOrDefault("Attempts", 1);
        final boolean spawnContainerFromCenter = (boolean) map.getOrDefault("Spawn_world_center", false);
        final boolean spawnContainerFromPlayerCenter = (boolean) map.getOrDefault("Spawn_player_center", false);
        final boolean spawnOnSurface = getBoolean(map.getOrDefault("Spawn_on_surface", false));

        final int minRadius = (int) map.getOrDefault("Min_radius", spawnContainerFromCenter || spawnContainerFromPlayerCenter ? 100 : 10);
        final int maxRadius = (int) map.getOrDefault("Max_radius", spawnContainerFromCenter || spawnContainerFromPlayerCenter ? 1500 : 80);
        Facing blockFace = null;
        if (random_loot_facing != null)
            blockFace = Enums.getIfPresent(Facing.class, random_loot_facing).orNull();
        if (blockFace == null)
            blockFace = Facing.WEST;

        Valid.checkNotNull(icon, "Material is null for this container");
        Material material = Material.getMaterial(icon);
        if (material == null) {
            material = Material.CHEST;
        }
        ParticlesConversion particlesConversion = new ParticlesConversion();

        final LootContainerBuilder lootContainerBuilder = new LootContainerBuilder()
                .setContainerDataLinkedToLootTable(lootTableLinked)
                .setSpawningContainerWithCooldown(spawningContainerWithCooldown)
                .setCooldown(cooldown)
                .setParticleEffects(particles != null ? particles.entrySet().stream().collect(Collectors.toMap(effectEntry -> effectEntry.getKey().toString(), Map.Entry::getValue)) :
                        particlesConversion.convertToParticleEffect(particleEffect == null || particleEffect.isEmpty() ? particlesConversion.convertParticleEffectList(particleEffectList) : particlesConversion.convertStringList(particleEffect)))
                .setRandomLootWorlds(worlds)
                .setEnchant(enchant)
                .setIcon(material)
                .setRandomLootContainerItem(random_loot_container != null ? random_loot_container : Material.CHEST)
                .setRandomLootContainerFacing(blockFace)
                .setDisplayName(displayName)
                .setLore(lore)
                .setContainerShallGlow(random_loot_glow)
                .setShowTitle(random_loot_title)
                .setRandomSpawn(randomSpawn)
                .setContainerData(containers)
                .setKeysData(keys)
                .setAttempts(attempts)
                .setSpawnContainerFromWorldCenter(spawnContainerFromCenter)
                .setSpawnContainerFromPlayerCenter(spawnContainerFromPlayerCenter)
                .setSpawnOnSurface(spawnOnSurface)
                .setMinRadius(minRadius)
                .setMaxRadius(maxRadius)
                .setSpawnLocation(new LocationWrapper("Spawn-point", map, false))
                .setPermissionForRandomSpawn(String.valueOf(map.get("permission")));

        return lootContainerBuilder.build();
    }


}
